package springproject.iam.v1.mapper.struct;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import springproject.iam.v1.model.BadCredentialCache;
import springproject.iam.v1.model.dto.badcredentialcache.BadCredentialCacheCreation;
import springproject.iam.v1.model.dto.badcredentialcache.BadCredentialCacheResponse;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BadCredentialCacheMapper {
  BadCredentialCache asModel(BadCredentialCacheCreation creation);

  BadCredentialCacheResponse asResponse(BadCredentialCache model);
}
