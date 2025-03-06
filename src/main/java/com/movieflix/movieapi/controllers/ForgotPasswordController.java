package com.movieflix.movieapi.controllers;

import com.movieflix.movieapi.auth.entities.ForgotPassword;
import com.movieflix.movieapi.auth.entities.User;
import com.movieflix.movieapi.auth.repositories.ForgotPasswordRepository;
import com.movieflix.movieapi.auth.repositories.UserRepository;
import com.movieflix.movieapi.auth.utils.ChangePassword;
import com.movieflix.movieapi.dto.MailBody;
import com.movieflix.movieapi.services.EmailService;
import com.movieflix.movieapi.services.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final ForgotPasswordRepository forgotPasswordRepository;

    private final PasswordEncoder passwordEncoder;

    private final OtpService otpService;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder, OtpService otpService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    //send mail for email verification
    @PostMapping("/verifyEmail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));

        Integer otp = otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is your otp for your Forgot Password request: " + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid otp for email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {

            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }

        String otpToken = otpService.generateOtpToken(fp);

        return ResponseEntity.ok("OTP verified! Here is your OTP token: " + otpToken);
    }

    @PostMapping("/changePassword/{email}/{otpToken}")
    public ResponseEntity<String> changePasswordHandler(@PathVariable String email, @PathVariable String otpToken, @RequestBody ChangePassword changePassword) {

        userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));

        if (!otpService.isTokenValid(otpToken, email)) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Invalid OTP token");
        }

        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Both passwords don't match!", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);
        return ResponseEntity.ok("Password has been changed successfully!");
    }

    private Integer otpGenerator() {

        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
