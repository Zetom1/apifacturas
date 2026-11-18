package com.moocode.apifacturas.service;

import com.moocode.apifacturas.enums.OtpPurpose;
import com.moocode.apifacturas.exception.BusinessException;
import com.moocode.apifacturas.exception.ExpiredOtpException;
import com.moocode.apifacturas.exception.InvalidOtpException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** OTP de seis dígitos. Redis conserva solo el hash, el contador y sus vencimientos. */
@Service
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationService emailService;
    private final long expirationMinutes;
    private final int maxAttempts;
    private final long resendCooldownSeconds;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(StringRedisTemplate redisTemplate, PasswordEncoder passwordEncoder,
                      EmailNotificationService emailService,
                      @Value("5") long expirationMinutes,
                      @Value("3") int maxAttempts,
                      @Value("${app.security.otp.resend-cooldown-seconds}") long resendCooldownSeconds) {
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.expirationMinutes = expirationMinutes;
        this.maxAttempts = maxAttempts;
        this.resendCooldownSeconds = resendCooldownSeconds;
    }

    public void issue(String email, OtpPurpose purpose) {
        String normalizedEmail = normalize(email);
        String cooldownKey = cooldownKey(normalizedEmail, purpose);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
                cooldownKey, "1", Duration.ofSeconds(resendCooldownSeconds));
        if (!Boolean.TRUE.equals(acquired)) {
            throw new BusinessException("Espera antes de solicitar otro código.");
        }

        String code = "%06d".formatted(secureRandom.nextInt(1_000_000));
        String otpKey = otpKey(normalizedEmail, purpose);
        String attemptsKey = attemptsKey(normalizedEmail, purpose);
        redisTemplate.opsForValue().set(otpKey, passwordEncoder.encode(code), Duration.ofMinutes(expirationMinutes));
        redisTemplate.opsForValue().set(attemptsKey, "0", Duration.ofMinutes(expirationMinutes));
        try {
            emailService.sendCode(normalizedEmail, code, expirationMinutes, purpose);
        } catch (RuntimeException exception) {
            redisTemplate.delete(otpKey);
            redisTemplate.delete(attemptsKey);
            redisTemplate.delete(cooldownKey);
            throw exception;
        }
    }

    public void verify(String email, OtpPurpose purpose, String submittedCode) {
        String normalizedEmail = normalize(email);
        String otpKey = otpKey(normalizedEmail, purpose);
        String attemptsKey = attemptsKey(normalizedEmail, purpose);
        String passwordHash = redisTemplate.opsForValue().get(otpKey);
        if (passwordHash == null) {
            throw new ExpiredOtpException("El código expiró o no fue solicitado.");
        }

        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts == null || attempts > maxAttempts) {
            redisTemplate.delete(otpKey);
            redisTemplate.delete(attemptsKey);
            throw new InvalidOtpException("Se agotaron los intentos para este código.");
        }
        if (!passwordEncoder.matches(submittedCode, passwordHash)) {
            throw new InvalidOtpException("El código de verificación no es válido.");
        }

        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptsKey);
    }

    public String pendingKey(String email) { return "auth:registration:pending:" + normalize(email); }
    public String verifiedKey(String email) { return "auth:registration:verified:" + normalize(email); }

    private String otpKey(String email, OtpPurpose purpose) {
        return "auth:otp:" + purpose.name().toLowerCase(Locale.ROOT) + ":" + email;
    }

    private String attemptsKey(String email, OtpPurpose purpose) {
        return "auth:otp:attempts:" + purpose.name().toLowerCase(Locale.ROOT) + ":" + email;
    }

    private String cooldownKey(String email, OtpPurpose purpose) {
        return "auth:otp:cooldown:" + purpose.name().toLowerCase(Locale.ROOT) + ":" + email;
    }

    private String normalize(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}