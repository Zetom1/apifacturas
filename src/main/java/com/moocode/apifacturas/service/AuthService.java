package com.moocode.apifacturas.service;

import com.moocode.apifacturas.dto.request.LoginRequestDTO;
import com.moocode.apifacturas.dto.response.TokenResponseDTO;
import com.moocode.apifacturas.exception.InvalidCredentialException;
import com.moocode.apifacturas.security.AccountDetailService;
import com.moocode.apifacturas.security.JwtService;
import com.moocode.apifacturas.service.OtpService;
import com.moocode.apifacturas.enums.OtpPurpose;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;
    private final AccountDetailService accountDetailService;
    private final OtpService otpService;
    private final long refreshTokenHours;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
                       StringRedisTemplate redisTemplate, AccountDetailService accountDetailService,
                       OtpService otpService,
                       @Value("${app.security.jwt.refresh-token-hours}")long refreshTokenHours){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
        this.accountDetailService = accountDetailService;
        this.otpService = otpService;
        this.refreshTokenHours = refreshTokenHours;
    }

    public TokenResponseDTO login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email().trim().toLowerCase(Locale.ROOT), request.password()
                    )
            );
            return issueTokens((UserDetails) authentication.getPrincipal());
        }
        catch (BadCredentialsException | DisabledException exception) {
            throw new InvalidCredentialException();
        }
    }

    public TokenResponseDTO refresh(String refreshToken) {
        String email = redisTemplate.opsForValue().getAndDelete(refreshKey(refreshToken));

        if (email == null) {
            throw new InvalidCredentialException();
        }

        redisTemplate.opsForSet().remove(refreshIndexKey(email), refreshToken);

        UserDetails account;
        try {
            account = accountDetailService.loadUserByUsername(email);
        } catch (UsernameNotFoundException exception) {
            throw new InvalidCredentialException();
        }

        if (!account.isEnabled()) {
            throw new InvalidCredentialException();
        }

        return issueTokens(account);
    }

    public void logout (String refreshToken,String authenticationEmail) {
        String key = refreshKey(refreshToken);
        String owner = redisTemplate.opsForValue().get(key);

        if(owner != null){
            String deleteOwner = redisTemplate.opsForValue().getAndDelete(key);
            if(deleteOwner != null){
                redisTemplate.opsForSet().remove(refreshIndexKey(deleteOwner),refreshToken);}
        }
    }

    public void requestLoginOtp(String email) {
        UserDetails account;
        try {
            account = accountDetailService.loadUserByUsername(email.trim().toLowerCase(Locale.ROOT));
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException exception) {
            return;
        }

        if (account.isEnabled()) {
            try {
                otpService.issue(account.getUsername(), OtpPurpose.LOGIN);
            } catch (com.moocode.apifacturas.exception.BusinessException ignored) {
                // La respuesta no distingue entre correo desconocido y límite de reenvío.
            }
        }
    }

    public TokenResponseDTO verifyLoginOtp(String email, String code) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        try {
            otpService.verify(normalizedEmail, OtpPurpose.LOGIN, code);

            UserDetails account = accountDetailService.loadUserByUsername(normalizedEmail);

            if (!account.isEnabled()) {
                throw new InvalidCredentialException();
            }

            return issueTokens(account);

        } catch (com.moocode.apifacturas.exception.InvalidOtpException
                 | com.moocode.apifacturas.exception.ExpiredOtpException
                 | org.springframework.security.core.userdetails.UsernameNotFoundException exception) {

            throw new InvalidCredentialException();
        }
    }

    public void revokeAllRefreshTokens(String email) {
        String indexKey = refreshIndexKey(email);
        SetOperations<String, String> sets = redisTemplate.opsForSet();
        java.util.Set<String> tokens = sets.members(indexKey);

        if (tokens != null && !tokens.isEmpty()) {
            tokens.forEach(token -> redisTemplate.delete(refreshKey(token)));
        }

        redisTemplate.delete(indexKey);
    }

    private TokenResponseDTO issueTokens(UserDetails account) {
        String accessToken = jwtService.createAccessToken(account);

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String refreshToken =
                Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        redisTemplate.opsForValue().set(
                refreshKey(refreshToken),
                account.getUsername(),
                Duration.ofHours(refreshTokenHours)
        );

        redisTemplate.opsForSet().add(
                refreshIndexKey(account.getUsername()),
                refreshToken
        );

        redisTemplate.expire(
                refreshIndexKey(account.getUsername()),
                Duration.ofHours(refreshTokenHours)
        );

        return new TokenResponseDTO(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenLifetimeSeconds(),
                refreshTokenHours * 3600
        );
    }

    private String refreshKey(String token) {
        return "auth:refresh:" + token;
    }

    private String refreshIndexKey(String email) {
        return "auth:refresh-index:" + email.trim().toLowerCase(Locale.ROOT);
    }
}
