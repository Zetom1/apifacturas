package com.moocode.apifacturas.service;

import com.moocode.apifacturas.dto.response.AuthenticatedProfileResponseDTO;
import com.moocode.apifacturas.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticatedProfileService {
    private final UserRepository userRepository;

    public AuthenticatedProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AuthenticatedProfileResponseDTO getUserProfile(String email) {
        var user = userRepository.findByPerson_EmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("No se encontró la cuenta autentificada"));
        String middleName = user.getPerson().getMaternalSurname();
        String fullName = String.join(" ",java.util.stream.Stream.of(user.getPerson().getFirstName(),
                user.getPerson().getPaternalSurname(),middleName).filter(value->value!= null&&!value.isBlank()).toList());
        return new AuthenticatedProfileResponseDTO("USER-%06d".formatted(user.getPublicNumber()),
                fullName,user.getPerson().getEmail(),user.getRole().getCode());
    }
}
