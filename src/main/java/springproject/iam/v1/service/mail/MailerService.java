package springproject.iam.v1.service.mail;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import springproject.iam.v1.constant.Failed;
import springproject.iam.v1.exception.ServiceException;
import springproject.iam.v1.model.dto.mail.MailOption;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailerService implements AbstractMailService {
  @NonFinal
  @Value("${spring.mail.from}")
  String mailFrom;

  JavaMailSender javaMailSender;
  SpringTemplateEngine springTemplateEngine;

  @Override
  public boolean sendMail(MailOption mailOption) {
    try {
      String recipients = mailOption.getRecipients();
      List<MultipartFile> attachments = mailOption.getAttachments();
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(mailFrom, "Lê Xuân Hòa");
      if (recipients.contains(",")) {
        helper.setTo(InternetAddress.parse(recipients));
      } else {
        helper.setTo(recipients);
      }
      if (CollectionUtils.isEmpty(attachments)) {
        for (MultipartFile attachment : attachments) {
          helper.addAttachment(
              Objects.requireNonNull(attachment.getOriginalFilename()), attachment);
        }
      }
      helper.setSubject(mailOption.getSubject());
      helper.setText(mailOption.getContent(), true);
      javaMailSender.send(message);
      return true;
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.MAIL_DELIVERY);
    }
  }

  @Override
  public boolean sendTemplateMail(
      MailOption mailOption, String templateFilePath, Map<String, Object> mailContext) {
    try {
      String recipients = mailOption.getRecipients();
      List<MultipartFile> attachments = mailOption.getAttachments();
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(
              message,
              MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
              StandardCharsets.UTF_8.name());
      Context context = new Context();
      context.setVariables(mailContext);
      helper.setFrom(mailFrom, "Lê Xuân Hòa");
      if (recipients.contains(",")) {
        helper.setTo(InternetAddress.parse(recipients));
      } else {
        helper.setTo(recipients);
      }
      if (CollectionUtils.isEmpty(attachments)) {
        for (MultipartFile attachment : attachments) {
          helper.addAttachment(
              Objects.requireNonNull(attachment.getOriginalFilename()), attachment);
        }
      }
      helper.setSubject(mailOption.getSubject());
      helper.setText(springTemplateEngine.process(templateFilePath, context), true);
      javaMailSender.send(message);
      return true;
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.MAIL_DELIVERY);
    }
  }
}
