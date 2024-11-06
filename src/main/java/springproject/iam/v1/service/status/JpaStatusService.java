package springproject.iam.v1.service.status;

import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import springproject.iam.v1.constant.Failed;
import springproject.iam.v1.exception.ServiceException;
import springproject.iam.v1.mapper.struct.StatusMapper;
import springproject.iam.v1.model.Status;
import springproject.iam.v1.model.User;
import springproject.iam.v1.model.dto.status.StatusCreation;
import springproject.iam.v1.model.dto.status.StatusResponse;
import springproject.iam.v1.model.dto.status.StatusUpdate;
import springproject.iam.v1.repository.status.JpaStatusRepository;
import springproject.iam.v1.repository.user.JpaUserRepository;
import springproject.iam.v1.response.MultiResource;
import springproject.iam.v1.response.Paging;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaStatusService implements AbstractStatusService {
  JpaStatusRepository jpaStatusRepository;
  StatusMapper statusMapper;
  JpaUserRepository jpaUserRepository;

  @Override
  public void ensureNotExistedByContent(String content) {
    long existedRecord = jpaStatusRepository.countExistedByContent(content);
    if (existedRecord > 0) {
      throw new ServiceException(Failed.ALREADY_EXISTED);
    }
  }

  @PreAuthorize("hasAnyAuthority('CREATE')")
  @Override
  public StatusResponse save(StatusCreation creation) {
    try {
      this.ensureNotExistedByContent(creation.getContent());
      Status model = statusMapper.asModel(creation);
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // TODO: I am too lazy to thinking about how to refactor this, I am also
      // implement authentication using JWT with both Spring Security and OAuth2
      // Resource Service, by the way, this is also Tech-stack practice project...
      if (authentication instanceof JwtAuthenticationToken) {
        Long userId =
            ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
                .getToken()
                .getClaim("userId");
        Optional<User> owning = jpaUserRepository.findById(userId);
        if (!owning.isPresent()) {
          throw new ServiceException(Failed.OWNING_SIDE_NOT_EXISTS);
        }
        model.setUser(owning.get());
      } else {
        User owning =
            (User)
                ((UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication())
                    .getPrincipal();
        model.setUser(owning);
      }
      Status saved = jpaStatusRepository.save(model);
      return statusMapper.asResponse(saved);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.SAVE);
    }
  }

  @PreAuthorize("hasAnyAuthority('READ')")
  @Override
  public StatusResponse findById(Long id) {
    try {
      Optional<Status> queried = jpaStatusRepository.findById(id);
      if (!queried.isPresent()) {
        throw new ServiceException(Failed.FIND_BY_ID_NO_CONTENT);
      }
      StatusResponse response = statusMapper.asResponse(queried.get());
      return response;
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.FIND_BY_ID);
    }
  }

  @PreAuthorize("hasAnyAuthority('READ')")
  @Override
  public MultiResource<StatusResponse> findAll(int page, int size) {
    try {
      Page<Status> pageable = jpaStatusRepository.findAll(PageRequest.of(page, size));
      List<StatusResponse> values = statusMapper.asCollectionResponse(pageable.getContent());
      if (CollectionUtils.isEmpty(values)) {
        throw new ServiceException(Failed.FIND_ALL_NO_CONTENT);
      }
      Paging paging = new Paging(page, pageable.getTotalPages(), pageable.getTotalElements());
      return new MultiResource<>(values, paging);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.FIND_ALL);
    }
  }

  @PreAuthorize("hasAnyAuthority('READ')")
  @Override
  public MultiResource<StatusResponse> findAllByDeletedTrue(int page, int size) {
    try {
      Page<Status> pageable = jpaStatusRepository.findAllByDeletedTrue(PageRequest.of(page, size));
      List<StatusResponse> values = statusMapper.asCollectionResponse(pageable.getContent());
      if (CollectionUtils.isEmpty(values)) {
        throw new ServiceException(Failed.FIND_ALL_NO_CONTENT);
      }
      Paging paging = new Paging(page, pageable.getTotalPages(), pageable.getTotalElements());
      return new MultiResource<>(values, paging);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.FIND_ALL);
    }
  }

  @PreAuthorize("hasAnyAuthority('READ')")
  @Override
  public MultiResource<StatusResponse> findAllByContentContains(
      String content, int page, int size) {
    try {
      Page<Status> pageable =
          jpaStatusRepository.findAllByContentContains(content, PageRequest.of(page, size));
      List<StatusResponse> values = statusMapper.asCollectionResponse(pageable.getContent());
      if (CollectionUtils.isEmpty(values)) {
        throw new ServiceException(Failed.FIND_ALL_BY_NO_CONTENT);
      }
      Paging paging = new Paging(page, pageable.getTotalPages(), pageable.getTotalElements());
      return new MultiResource<>(values, paging);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.FIND_ALL_BY);
    }
  }

  @PreAuthorize("hasAnyAuthority('UPDATE')")
  @Override
  public StatusResponse updateById(Long id, StatusUpdate update) {
    try {
      Optional<Status> old = jpaStatusRepository.findById(id);
      if (!old.isPresent()) {
        throw new ServiceException(Failed.NOT_EXISTS);
      }
      Status value = old.get();
      String oldIdentity = value.getContent();
      statusMapper.mergeModel(value, update);
      if (!oldIdentity.equals(value.getContent())) {
        this.ensureNotExistedByContent(value.getContent());
      }
      Status model = jpaStatusRepository.save(value);
      StatusResponse response = statusMapper.asResponse(model);
      return response;
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.UPDATE);
    }
  }

  @PreAuthorize("hasAnyAuthority('DELETE')")
  @Override
  public StatusResponse deleteById(Long id) {
    try {
      Optional<Status> old = jpaStatusRepository.findById(id);
      if (!old.isPresent()) {
        throw new ServiceException(Failed.NOT_EXISTS);
      }
      Status value = old.get();
      StatusResponse response = statusMapper.asResponse(value);
      jpaStatusRepository.delete(value);
      return response;
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.DELETE);
    }
  }
}
