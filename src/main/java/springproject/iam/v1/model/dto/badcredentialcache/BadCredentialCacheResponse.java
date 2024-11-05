package springproject.iam.v1.model.dto.badcredentialcache;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BadCredentialCacheResponse {
  Long id;

  @NotNull(message = "accessTokenId can not be blank") String accessTokenId;

  @NotNull(message = "accessTokenExpiredAt can not be blank") Date accessTokenExpiredAt;

  @NotNull(message = "userId can not be blank") Long userId;
}
