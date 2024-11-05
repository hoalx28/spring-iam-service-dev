package springproject.iam.v1.repository.badcredentialcache;

import org.springframework.data.repository.CrudRepository;
import springproject.iam.v1.model.BadCredentialCache;

public interface RedisBadCredentialCacheRepository
    extends CrudRepository<BadCredentialCache, Long> {
  boolean existsByAccessTokenId(String id);
}
