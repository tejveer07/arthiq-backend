package com.arthiq.service;

import com.resend.Resend;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // ✅ Load values from environment variables or application.properties
    @Value("${RESEND_API_KEY:${RESEND_API_KEY_ENV:}}")
    private String apiKey;

    @Value("${FROM_EMAIL:onboarding@resend.dev}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("RESEND_API_KEY is missing — set it in Render environment variables.");
            }

            Resend resend = new Resend(apiKey);

            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(toEmail)
                    .subject("arthIQ - Email Verification OTP")
                    .html("<p>Your OTP for email verification is: <b>" + otp + "</b></p>"
                            + "<p>This OTP is valid for 10 minutes.<br>"
                            + "If you didn’t request this, please ignore this email.</p>"
                            + "<p>Thanks,<br>arthIQ Team</p>")
                    .build();

            Emails emails = resend.emails();
            emails.send(options);

            System.out.println("✅ OTP email sent successfully to: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }
}






//package com.arthiq.service;
//
//import com.resend.Resend;
//import com.resend.services.emails.Emails;
//import com.resend.services.emails.model.CreateEmailOptions;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    private static final String API_KEY =  "re_K9NNRB4b_NSkj51fzwATHtuDV4t7QJ5j1";
//    private static final String FROM_EMAIL = "onboarding@resend.dev"; // sandbox sender
//
//    public void sendOtpEmail(String toEmail, String otp) {
//        try {
//            Resend resend = new Resend(API_KEY);
//
//            CreateEmailOptions options = CreateEmailOptions.builder()
//                    .from(FROM_EMAIL)
//                    .to(toEmail)
//                    .subject("arthIQ - Email Verification OTP")
//                    .html("<p>Your OTP for email verification is: <b>" + otp + "</b></p>"
//                            + "<p>This OTP is valid for 10 minutes.<br>"
//                            + "If you didn’t request this, please ignore this email.</p>"
//                            + "<p>Thanks,<br>arthIQ Team</p>")
//                    .build();
//
//            Emails emails = resend.emails();
//            emails.send(options);
//
//            System.out.println("✅ OTP email sent successfully to: " + toEmail);
//
//        } catch (Exception e) {
//            System.err.println("❌ Failed to send email: " + e.getMessage());
//            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
//        }
//    }
//}
//
//




//package com.arthiq.service;
//
//import com.resend.Resend;
//import com.resend.core.exception.ResendException;
//import com.resend.services.emails.model.CreateEmailOptions;
//import com.resend.services.emails.model.CreateEmailResponse;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    public void sendOtpEmail(String toEmail, String otp) {
//        try {
////            String apiKey = System.getenv("RESEND_API_KEY");
////            String from = System.getenv("FROM_EMAIL");
//
//            String apiKey = "re_K9NNRB4b_NSkj51fzwATHtuDV4t7QJ5j1";
//            String from = "artihq.app@gmail.com";
//
//            Resend resend = new Resend(apiKey);
//
//            String body = "Your OTP for email verification is: " + otp +
//                    "\n\nThis OTP is valid for 10 minutes.\n\n— arthIQ Team";
//
//            CreateEmailOptions params = CreateEmailOptions.builder()
//                    .from(from)
//                    .to(toEmail)
//                    .subject("arthIQ - Email Verification OTP")
//                    .text(body)
//                    .build();
//
//            CreateEmailResponse data = resend.emails().send(params);
//            System.out.println("✅ Email sent successfully: " + data.getId());
//        } catch (ResendException e) {
//            System.err.println("Error sending email: " + e.getMessage());
//            throw new RuntimeException("Failed to send OTP email");
//        }
//    }
//}





//package com.arthiq.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendOtpEmail(String toEmail, String otp) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom("noreply@arthiq.com");
//            message.setTo(toEmail);
//            message.setSubject("arthIQ - Email Verification OTP");
//            message.setText("Your OTP for email verification is: " + otp +
//                    "\n\nThis OTP is valid for 10 minutes." +
//                    "\n\nIf you didn't request this, please ignore this email." +
//                    "\n\nThanks,\narthIQ Team");
//
//            mailSender.send(message);
//            System.out.println("OTP email sent successfully to: " + toEmail);
//        } catch (Exception e) {
//            System.err.println("Error sending email: " + e.getMessage());
//            throw new RuntimeException("Failed to send OTP email");
//        }
//    }
//}
