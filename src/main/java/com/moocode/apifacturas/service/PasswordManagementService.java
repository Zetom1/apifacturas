package com.moocode.apifacturas.service;

import com.moocode.apifacturas.dto.request.ChangePasswordRequestDTO;
import com.moocode.apifacturas.dto.request.ResetPasswordRequestDTO;
import com.moocode.apifacturas.entity.UserEntity;
import com.moocode.apifacturas.enums.OtpPurpose;
import com.moocode.apifacturas.exception.InvalidCredentialException;
import com.moocode.apifacturas.repository.UserRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final AuthService authService;

    public PasswordManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                     OtpService otpService, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.authService = authService;
    }

    public void requestRecovery(String submittedEmail) {
        String email = normalize(submittedEmail);
        userRepository.findByPerson_EmailIgnoreCase(email)
                .filter(UserEntity::isActive)
                .ifPresent(user -> {
                    try {
                        otpService.issue(user.getPerson().getEmail(), OtpPurpose.PASSWORD_RESET);
                    } catch (com.moocode.apifacturas.exception.BusinessException ignored) {
                        // Responde igual para evitar revelar si la cuenta existe o tiene cooldown.
                    }
                });
    }

    @Transactional
    public void reset(ResetPasswordRequestDTO request) {
        String email = normalize(request.email());
        otpService.verify(email, OtpPurpose.PASSWORD_RESET, request.code());
        UserEntity account = userRepository.findByPerson_EmailIgnoreCase(email)
                .filter(UserEntity::isActive)
                .orElseThrow(InvalidCredentialException::new);
        account.changePasswordHash(passwordEncoder.encode(request.newPassword()));
        authService.revokeAllRefreshTokens(email);
    }

    @Transactional
    public void change(String email, ChangePasswordRequestDTO request) {
        UserEntity account = userRepository.findByPerson_EmailIgnoreCase(normalize(email))
                .filter(UserEntity::isActive)
                .orElseThrow(InvalidCredentialException::new);
        if (!passwordEncoder.matches(request.currentPassword(), account.getPasswordHash())) {
            throw new InvalidCredentialException();
        }
        account.changePasswordHash(passwordEncoder.encode(request.newPassword()));
        authService.revokeAllRefreshTokens(email);
    }

    private String normalize(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}