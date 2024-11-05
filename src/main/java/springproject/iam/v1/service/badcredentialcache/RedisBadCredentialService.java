package springproject.iam.v1.service.badcredentialcache;

import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springproject.iam.v1.constant.Failed;
import springproject.iam.v1.exception.ServiceException;
import springproject.iam.v1.mapper.struct.BadCredentialCacheMapper;
import springproject.iam.v1.model.BadCredentialCache;
import springproject.iam.v1.model.dto.badcredentialcache.BadCredentialCacheCreation;
import springproject.iam.v1.model.dto.badcredentialcache.BadCredentialCacheResponse;
import springproject.iam.v1.model.dto.user.UserResponse;
import springproject.iam.v1.repository.badcredentialcache.RedisBadCredentialCacheRepository;
import springproject.iam.v1.token.AbstractJWTAuthProvider;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisBadCredentialService implements AbstractBadCredentialCacheService {
  @NonFinal
  @Value("${jwt.access-token.secret}")
  String accessTokenSecret;

  AbstractJWTAuthProvider<SignedJWT, UserResponse> jwtAuthProvider;
  BadCredentialCacheMapper badCredentialCacheMapper;
  RedisBadCredentialCacheRepository redisBadCredentialCacheRepository;

  @Override
  public BadCredentialCacheResponse save(BadCredentialCacheCreation creation) {
    try {
      BadCredentialCache model = badCredentialCacheMapper.asModel(creation);
      BadCredentialCache saved = redisBadCredentialCacheRepository.save(model);
      return badCredentialCacheMapper.asResponse(saved);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.SAVE);
    }
  }

  @Override
  public void ensureNotBadCredential(String token) {
    try {
      SignedJWT signedJWT = jwtAuthProvider.verify(token, accessTokenSecret);
      String tokenId = signedJWT.getJWTClaimsSet().getJWTID();
      boolean isBadCredential = redisBadCredentialCacheRepository.existsByAccessTokenId(tokenId);
      if (isBadCredential) {
        throw new ServiceException(Failed.JWT_TOKEN_BLOCKED);
      }
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.ENSURE_JWT_TOKEN_NOT_BAD_CREDENTIALS);
    }
  }
}
