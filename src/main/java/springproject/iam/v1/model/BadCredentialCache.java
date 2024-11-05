package springproject.iam.v1.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@EqualsAndHashCode(exclude = {})
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash(value = "badCredentialCache", timeToLive = 3600)
public class BadCredentialCache implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Indexed
  @NotNull(message = "accessTokenId can not be blank") String accessTokenId;

  @NotNull(message = "accessTokenExpiredAt can not be blank") Date accessTokenExpiredAt;

  @NotNull(message = "userId can not be blank") Long userId;
}
