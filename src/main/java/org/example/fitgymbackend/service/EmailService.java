package org.example.fitgymbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app-url}")
    private String appUrl;

    /**
     * Envia email de verificacion de cuenta
     */
    @Async
    public void sendVerificationEmail(String to, String token) {
        String subject = "FitGym - Verifica tu cuenta";
        String verificationLink = appUrl + "/verify-email?token=" + token;

        String content = buildVerificationEmail(verificationLink);
        sendEmail(to, subject, content);
    }

    /**
     * Envia email para resetear contrasena
     */
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "FitGym - Restablece tu contrasena";
        String resetLink = appUrl + "/reset-password?token=" + token;

        String content = buildResetPasswordEmail(resetLink);
        sendEmail(to, subject, content);
    }

    /**
     * Template HTML para verificacion de email
     */
    private String buildVerificationEmail(String link) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                ".container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #606de5, #8b5cf6); padding: 30px; text-align: center; color: white; }" +
                ".content { padding: 30px; }" +
                "h1 { margin: 0; font-size: 24px; }" +
                "p { color: #666; line-height: 1.6; }" +
                ".button { display: inline-block; padding: 12px 30px; background: #606de5; color: white; text-decoration: none; border-radius: 25px; margin: 20px 0; font-weight: bold; }" +
                ".footer { padding: 20px; text-align: center; color: #999; font-size: 12px; border-top: 1px solid #eee; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Bienvenido a FitGym</h1></div>" +
                "<div class='content'>" +
                "<p>Gracias por registrarte. Solo falta un paso para activar tu cuenta.</p>" +
                "<p>Para verificar tu email, haz clic en el siguiente boton:</p>" +
                "<div style='text-align: center;'><a href='" + link + "' class='button'>Verificar Email</a></div>" +
                "<p style='font-size: 12px; color: #999;'>Si el boton no funciona, copia y pega este enlace en tu navegador:</p>" +
                "<p style='font-size: 11px; color: #606de5; word-break: break-all;'>" + link + "</p>" +
                "</div>" +
                "<div class='footer'><p>FitGym 2024. Todos los derechos reservados.</p></div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Template HTML para reset de password
     */
    private String buildResetPasswordEmail(String link) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                ".container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #606de5, #8b5cf6); padding: 30px; text-align: center; color: white; }" +
                ".content { padding: 30px; }" +
                "h1 { margin: 0; font-size: 24px; }" +
                "p { color: #666; line-height: 1.6; }" +
                ".button { display: inline-block; padding: 12px 30px; background: #606de5; color: white; text-decoration: none; border-radius: 25px; margin: 20px 0; font-weight: bold; }" +
                ".footer { padding: 20px; text-align: center; color: #999; font-size: 12px; border-top: 1px solid #eee; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Olvidaste tu contrasena?</h1></div>" +
                "<div class='content'>" +
                "<p>No te preocupes, recibimos una solicitud para restablecer tu contrasena.</p>" +
                "<p>Haz clic en el boton de abajo para crear una nueva contrasena:</p>" +
                "<div style='text-align: center;'><a href='" + link + "' class='button'>Restablecer Contrasena</a></div>" +
                "<p style='font-size: 12px; color: #999;'>Si tu no solicitaste esto, ignora este email.</p>" +
                "<p style='font-size: 12px; color: #999;'>Este enlace expira en 1 hora.</p>" +
                "<p style='font-size: 11px; color: #606de5; word-break: break-all;'>" + link + "</p>" +
                "</div>" +
                "<div class='footer'><p>FitGym 2024. Todos los derechos reservados.</p></div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Metodo generico para enviar email
     */
    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);  // true = HTML
            helper.setFrom("noreply@fitgym.com");

            mailSender.send(message);
            System.out.println("Email enviado exitosamente a: " + to);

        } catch (MessagingException e) {
            System.err.println("Error al enviar email: " + e.getMessage());
            throw new RuntimeException("Error al enviar el email", e);
        }
    }
}