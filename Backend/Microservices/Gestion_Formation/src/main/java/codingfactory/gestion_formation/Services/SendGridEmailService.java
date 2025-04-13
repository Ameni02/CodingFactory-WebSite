package codingfactory.gestion_formation.Services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public void sendEmail(String to, String subject, String message) throws Exception {
        Email from = new Email("ton-email@domaine.com"); // Tu peux mettre n'importe quel email comme expéditeur
        Email toEmail = new Email(to);  // Le destinataire de l'email
        Content content = new Content("text/plain", message);  // Le corps du message
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);  // Initialisation de SendGrid avec la clé API
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Email envoyé : " + response.getStatusCode());
        } catch (IOException ex) {
            throw new Exception("Erreur lors de l'envoi de l'email : " + ex.getMessage());
        }
    }
}
