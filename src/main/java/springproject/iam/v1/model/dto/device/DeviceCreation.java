package springproject.iam.v1.model.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceCreation {
  @NotBlank(message = "ip_address can not be blank")
  String ipAddress;

  @NotBlank(message = "user_agent can not be blank")
  String userAgent;

  @NotNull(message = "user_id can not be blank") Long userId;
}
