package springproject.iam.v1.model.dto.auth;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
  @Parameter(name = "Username.", description = "Username.", required = true)
  @Schema(description = "Username.", name = "Username.", type = "string")
  @NotBlank(message = "username can not be blank")
  String username;

  @Parameter(name = "Password.", description = "Password.", required = true)
  @Schema(description = "Password.", name = "Password.", type = "string")
  @NotBlank(message = "password can not be blank")
  String password;

  @Parameter(name = "List of role id.", description = "List of role id.", required = true)
  @Schema(description = "List of role id.", name = "List of role id.", type = "string")
  Set<Long> roleIds = new HashSet<>(List.of(1L));
}
