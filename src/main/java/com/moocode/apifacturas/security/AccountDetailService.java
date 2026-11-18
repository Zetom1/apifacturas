package com.moocode.apifacturas.security;

import com.moocode.apifacturas.entity.UserEntity;
import com.moocode.apifacturas.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public AccountDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        UserEntity account = userRepository.findByPerson_EmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("cuenta no encontrada"));
        return User.withUsername(account.getPerson().getEmail())
                .password(account.getPasswordHash())
                .authorities("ROLE_"+account.getRole().getCode())
                .disabled(!account.isActive() || !account.getPerson().isActive())
                .build();
    }
}
