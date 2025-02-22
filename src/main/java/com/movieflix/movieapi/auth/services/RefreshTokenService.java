package com.movieflix.movieapi.auth.services;

import com.movieflix.movieapi.auth.entities.RefreshToken;
import com.movieflix.movieapi.auth.entities.User;
import com.movieflix.movieapi.auth.repositories.RefreshTokenRepository;
import com.movieflix.movieapi.auth.repositories.UserRepository;
import com.movieflix.movieapi.exceptions.TokenExpiredException;
import com.movieflix.movieapi.exceptions.TokenNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        RefreshToken refreshToken = user.getRefreshToken();

        if (refreshToken == null) {
            long refreshTokenValidity = 30 * 100000;
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token not found"));

        if (refToken.getExpirationTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refToken);
            throw new TokenExpiredException("Refresh token expired");
        }

        return refToken;
    }
}
