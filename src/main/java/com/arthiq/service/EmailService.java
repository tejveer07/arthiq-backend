package com.arthiq.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final WebClient webClient;

    public EmailService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            Map<String, Object> emailRequest = new HashMap<>();

            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("email", senderEmail);
            sender.put("name", senderName);
            emailRequest.put("sender", sender);

            // Recipient
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", toEmail);
            emailRequest.put("to", List.of(recipient));

            // Email content
            emailRequest.put("subject", "arthIQ - Email Verification OTP");
            emailRequest.put("htmlContent",
                    "<html><body>" +
                            "<h2>arthIQ Email Verification</h2>" +
                            "<p>Your OTP for email verification is:</p>" +
                            "<h1 style='color: #4CAF50;'>" + otp + "</h1>" +
                            "<p>This OTP is valid for 10 minutes.</p>" +
                            "<p>If you didn't request this, please ignore this email.</p>" +
                            "<p>Thanks,<br/>arthIQ Team</p>" +
                            "</body></html>");

            // Send email via Brevo API
            webClient.post()
                    .uri("/smtp/email")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("api-key", brevoApiKey)
                    .bodyValue(emailRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> System.out.println("✅ Email sent successfully"))
                    .doOnError(error -> System.err.println("❌ Failed to send email: " + error.getMessage()))
                    .subscribe(); // Non-blocking async send

        } catch (Exception e) {
            System.err.println("❌ Email error: " + e.getMessage());
            // Don't throw - allow registration to succeed even if email fails
        }
    }
}





//package com.arthiq.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.from}")
//    private String fromEmail;
//
//    public EmailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    public void sendOtpEmail(String toEmail, String otp) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromEmail);
//            message.setTo(toEmail);
//            message.setSubject("arthIQ - Email Verification OTP");
//            message.setText(
//                    "Your OTP for email verification is: " + otp +
//                            "\n\nThis OTP is valid for 10 minutes." +
//                            "\n\nIf you didn't request this, please ignore this email." +
//                            "\n\nThanks,\narthIQ Team"
//            );
//
//            mailSender.send(message);
//            System.out.println("✅ OTP email sent successfully to: " + toEmail);
//        } catch (Exception e) {
//            System.err.println("❌ Failed to send email: " + e.getMessage());
//            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
//        }
//    }
//}
//
//
//
//
//
//
////package com.arthiq.service;
////
////import com.resend.Resend;
////import com.resend.services.emails.Emails;
////import com.resend.services.emails.model.CreateEmailOptions;
////import org.springframework.stereotype.Service;
////
////@Service
////public class EmailService {
////
////    private static final String API_KEY =  "re_K9NNRB4b_NSkj51fzwATHtuDV4t7QJ5j1";
////    private static final String FROM_EMAIL = "onboarding@resend.dev"; // sandbox sender
////
////    public void sendOtpEmail(String toEmail, String otp) {
////        try {
////            Resend resend = new Resend(API_KEY);
////
////            CreateEmailOptions options = CreateEmailOptions.builder()
////                    .from(FROM_EMAIL)
////                    .to(toEmail)
////                    .subject("arthIQ - Email Verification OTP")
////                    .html("<p>Your OTP for email verification is: <b>" + otp + "</b></p>"
////                            + "<p>This OTP is valid for 10 minutes.<br>"
////                            + "If you didn’t request this, please ignore this email.</p>"
////                            + "<p>Thanks,<br>arthIQ Team</p>")
////                    .build();
////
////            Emails emails = resend.emails();
////            emails.send(options);
////
////            System.out.println("✅ OTP email sent successfully to: " + toEmail);
////
////        } catch (Exception e) {
////            System.err.println("❌ Failed to send email: " + e.getMessage());
////            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
////        }
////    }
////}
////
////
//
//
//
//
////package com.arthiq.service;
////
////import com.resend.Resend;
////import com.resend.core.exception.ResendException;
////import com.resend.services.emails.model.CreateEmailOptions;
////import com.resend.services.emails.model.CreateEmailResponse;
////import org.springframework.stereotype.Service;
////
////@Service
////public class EmailService {
////
////    public void sendOtpEmail(String toEmail, String otp) {
////        try {
//////            String apiKey = System.getenv("RESEND_API_KEY");
//////            String from = System.getenv("FROM_EMAIL");
////
////            String apiKey = "re_K9NNRB4b_NSkj51fzwATHtuDV4t7QJ5j1";
////            String from = "artihq.app@gmail.com";
////
////            Resend resend = new Resend(apiKey);
////
////            String body = "Your OTP for email verification is: " + otp +
////                    "\n\nThis OTP is valid for 10 minutes.\n\n— arthIQ Team";
////
////            CreateEmailOptions params = CreateEmailOptions.builder()
////                    .from(from)
////                    .to(toEmail)
////                    .subject("arthIQ - Email Verification OTP")
////                    .text(body)
////                    .build();
////
////            CreateEmailResponse data = resend.emails().send(params);
////            System.out.println("✅ Email sent successfully: " + data.getId());
////        } catch (ResendException e) {
////            System.err.println("Error sending email: " + e.getMessage());
////            throw new RuntimeException("Failed to send OTP email");
////        }
////    }
////}
//
//
//
//
//
////package com.arthiq.service;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.mail.SimpleMailMessage;
////import org.springframework.mail.javamail.JavaMailSender;
////import org.springframework.stereotype.Service;
////
////@Service
////public class EmailService {
////
////    @Autowired
////    private JavaMailSender mailSender;
////
////    public void sendOtpEmail(String toEmail, String otp) {
////        try {
////            SimpleMailMessage message = new SimpleMailMessage();
////            message.setFrom("noreply@arthiq.com");
////            message.setTo(toEmail);
////            message.setSubject("arthIQ - Email Verification OTP");
////            message.setText("Your OTP for email verification is: " + otp +
////                    "\n\nThis OTP is valid for 10 minutes." +
////                    "\n\nIf you didn't request this, please ignore this email." +
////                    "\n\nThanks,\narthIQ Team");
////
////            mailSender.send(message);
////            System.out.println("OTP email sent successfully to: " + toEmail);
////        } catch (Exception e) {
////            System.err.println("Error sending email: " + e.getMessage());
////            throw new RuntimeException("Failed to send OTP email");
////        }
////    }
////}
