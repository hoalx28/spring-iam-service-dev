package springproject.iam.v1.service.badcredentialcache;

import springproject.iam.v1.model.dto.badcredentialcache.BadCredentialCacheCreation;
import springproject.iam.v1.model.dto.badcredentialcache.BadCredentialCacheResponse;

public interface AbstractBadCredentialCacheService {
  BadCredentialCacheResponse save(BadCredentialCacheCreation creation);

  void ensureNotBadCredential(String token);
}
