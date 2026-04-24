package org.example.fitgymbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url:http://localhost:5173}")
    private String appUrl;

    @Value("${spring.mail.dev.mode:false}")
    private boolean devMode;

    //  VERIFICACIÓN DE EMAIL
    public void enviarEmailVerificacion(String to, String name, String token) {
        String verificationUrl = appUrl + "/verify-email?token=" + token;

        String subject = "🏋️ Verifica tu email - FitGym";
        String htmlContent = buildVerificationEmail(name, verificationUrl, token);

        sendEmail(to, subject, htmlContent, "verificación");
    }

    //  RECUPERACIÓN DE CONTRASEÑA
    public void enviarEmailRecuperacion(String to, String name, String token) {
        String resetUrl = appUrl + "/reset-password?token=" + token;

        String subject = "🔐 Recupera tu contraseña - FitGym";
        String htmlContent = buildRecoveryEmail(name, resetUrl, resetUrl);

        sendEmail(to, subject, htmlContent, "recuperación");
    }

    //  MÉTODO GENÉRICO PARA ENVIAR EMAILS
    private void sendEmail(String to, String subject, String htmlContent, String tipo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@fitgym.com"); // Remitente genérico
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            //  LOGS DETALLADOS PARA MAILTRAP
            if (devMode) {
                System.out.println("╔══════════════════════════════════════════════════════╗");
                System.out.println("║          📧 EMAIL ENVIADO (Mailtrap)                 ║");
                System.out.println("╠══════════════════════════════════════════════════════╣");
                System.out.println("║ Tipo: " + tipo);
                System.out.println("║ Para: " + to);
                System.out.println("║ Asunto: " + subject);
                System.out.println("║ Servidor: sandbox.smtp.mailtrap.io:2525");
                System.out.println("║ 📨 Revisa Mailtrap: https://mailtrap.io/inboxes");
                System.out.println("╚══════════════════════════════════════════════════════╝");
            } else {
                System.out.println("✅ Email de " + tipo + " enviado a: " + to);
            }

        } catch (MessagingException e) {
            System.err.println("❌ Error enviando email de " + tipo + ": " + e.getMessage());
            throw new RuntimeException("Error al enviar el email: " + e.getMessage(), e);
        }
    }

    //  TEMPLATE EMAIL DE VERIFICACIÓN
    private String buildVerificationEmail(String name, String verificationUrl, String token) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 0; background: #f4f4f9; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 32px; font-weight: bold; }
                    .header p { margin: 10px 0 0; opacity: 0.9; font-size: 16px; }
                    .content { padding: 40px 30px; }
                    .content h2 { color: #333; margin-top: 0; }
                    .content p { color: #666; line-height: 1.6; font-size: 16px; }
                    .button { display: inline-block; padding: 15px 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-decoration: none; border-radius: 30px; font-weight: bold; font-size: 16px; margin: 25px 0; text-align: center; }
                    .button:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4); }
                    .link-text { word-break: break-all; color: #667eea; font-size: 14px; background: #f0f0f0; padding: 15px; border-radius: 8px; margin: 15px 0; }
                    .token-box { background: #f8f9fa; border: 2px dashed #667eea; padding: 20px; border-radius: 10px; text-align: center; margin: 20px 0; }
                    .token { font-size: 24px; font-weight: bold; color: #667eea; letter-spacing: 3px; }
                    .footer { background: #f8f9fa; padding: 20px; text-align: center; color: #999; font-size: 12px; }
                    .emoji { font-size: 40px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="emoji">💪</div>
                        <h1>FitGym</h1>
                        <p>Tu comunidad fitness</p>
                    </div>
                    <div class="content">
                        <h2>¡Hola %s! 👋</h2>
                        <p>Gracias por registrarte en <strong>FitGym</strong>. Estamos emocionados de que te unas a nuestra comunidad fitness.</p>
                        <p>Para completar tu registro y comenzar a usar la aplicación, verifica tu dirección de email haciendo clic en el botón:</p>
                        
                        <center>
                            <a href="%s" class="button">✅ Verificar mi Email</a>
                        </center>
                        
                        <p>O copia y pega este enlace en tu navegador:</p>
                        <div class="link-text">%s</div>
                        
                        <div class="token-box">
                            <p style="margin:0 0 10px 0; color: #666;">Tu código de verificación:</p>
                            <div class="token">%s</div>
                        </div>
                        
                        <p style="color: #999; font-size: 14px;">⏰ Este enlace expirará en 24 horas.</p>
                        <p style="color: #999; font-size: 14px;">Si no creaste esta cuenta, puedes ignorar este mensaje.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 FitGym. Todos los derechos reservados.</p>
                        <p>Este es un email automático, por favor no respondas a este mensaje.</p>
                    </div>
                </div>
            </body>
            </html>
        """.formatted(name, verificationUrl, verificationUrl, token);
    }

    // 🎨 TEMPLATE EMAIL DE RECUPERACIÓN
    private String buildRecoveryEmail(String name, String resetUrl, String token) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 0; background: #f4f4f9; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 40px 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 32px; }
                    .content { padding: 40px 30px; }
                    .button { display: inline-block; padding: 15px 40px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; text-decoration: none; border-radius: 30px; font-weight: bold; margin: 25px 0; }
                    .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px; }
                    .footer { background: #f8f9fa; padding: 20px; text-align: center; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 FitGym</h1>
                        <p>Solicitud de recuperación de contraseña</p>
                    </div>
                    <div class="content">
                        <h2>Hola %s 👋</h2>
                        <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta en <strong>FitGym</strong>.</p>
                        <p>Para continuar con el proceso, haz clic en el siguiente botón:</p>
                        
                        <center>
                            <a href="%s" class="button">🔄 Restablecer Contraseña</a>
                        </center>
                        
                        <p>O copia y pega este enlace en tu navegador:</p>
                        <p style="word-break: break-all; color: #f5576c; font-size: 14px;">%s</p>
                        
                        <div class="warning">
                            <strong>⚠️ Importante:</strong>
                            <ul style="margin: 10px 0 0; padding-left: 20px;">
                                <li>Este enlace expirará en <strong>15 minutos</strong></li>
                                <li>Si no solicitaste este cambio, ignora este mensaje</li>
                                <li>Nunca compartas este enlace con nadie</li>
                            </ul>
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2024 FitGym. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
        """.formatted(name, resetUrl, token);
    }
}