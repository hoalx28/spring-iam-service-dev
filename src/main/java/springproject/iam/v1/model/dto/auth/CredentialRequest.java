package springproject.iam.v1.model.dto.auth;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CredentialRequest {

  @Parameter(name = "Username", description = "Username", required = true)
  @Schema(description = "Username", name = "username", type = "string")
  @NotBlank(message = "username can not be blank")
  String username;

  @Parameter(name = "Username", description = "Username", required = true)
  @Schema(description = "password", name = "password", type = "string")
  @NotBlank(message = "password can not be blank")
  String password;
}
