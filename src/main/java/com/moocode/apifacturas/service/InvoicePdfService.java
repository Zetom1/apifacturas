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
public class InvoicePdfService {

}
