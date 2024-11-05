package springproject.iam.v1.service.mail;

import java.util.Map;
import springproject.iam.v1.model.dto.mail.MailOption;

public interface AbstractMailService {
  boolean sendMail(MailOption mailOption);

  boolean sendTemplateMail(
      MailOption mailOption, String templateFilePath, Map<String, Object> mailContext);
}
