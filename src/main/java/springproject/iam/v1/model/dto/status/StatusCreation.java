package springproject.iam.v1.model.dto.status;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import springproject.iam.v1.validator.status.UniqueStatusContent;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusCreation {
  @UniqueStatusContent
  @NotBlank(message = "content can not be blank")
  String content;

  Long userId;
}
