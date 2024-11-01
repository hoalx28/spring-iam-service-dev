package springproject.iam.v1.service.device;

import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import springproject.iam.v1.constant.Failed;
import springproject.iam.v1.exception.ServiceException;
import springproject.iam.v1.mapper.struct.DeviceMapper;
import springproject.iam.v1.model.Device;
import springproject.iam.v1.model.User;
import springproject.iam.v1.model.dto.device.DeviceCreation;
import springproject.iam.v1.model.dto.device.DeviceResponse;
import springproject.iam.v1.model.dto.device.DeviceUpdate;
import springproject.iam.v1.repository.device.JpaDeviceRepository;
import springproject.iam.v1.repository.user.JpaUserRepository;
import springproject.iam.v1.response.MultiResource;
import springproject.iam.v1.response.Paging;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaDeviceService implements AbstractDeviceService {
  JpaDeviceRepository jpaDeviceRepository;
  DeviceMapper deviceMapper;
  JpaUserRepository jpaUserRepository;

  @Override
  public void ensureNotExistedByIpAddress(String ipAddress) {
    long existedRecord = jpaDeviceRepository.countExistedByIpAddress(ipAddress);
    if (existedRecord > 0) {
      throw new ServiceException(Failed.ALREADY_EXISTED);
    }
  }

  @Override
  public DeviceResponse save(DeviceCreation creation) {
    try {
      this.ensureNotExistedByIpAddress(creation.getIpAddress());
      Device model = deviceMapper.asModel(creation);
      Optional<User> owning = jpaUserRepository.findById(creation.getUserId());
      if (!owning.isPresent()) {
        throw new ServiceException(Failed.OWNING_SIDE_NOT_EXISTS);
      }
      model.setUser(owning.get());
      Device saved = jpaDeviceRepository.save(model);
      return deviceMapper.asResponse(saved);
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      if (e instanceof DataIntegrityViolationException) {
        throw new ServiceException(Failed.OWNING_SIDE_NOT_AVAILABLE);
      }
      e.printStackTrace();
      throw new ServiceException(Failed.SAVE);
    }
  }

  @Override
  public DeviceResponse findById(Long id) {
    try {
      Optional<Device> queried = jpaDeviceRepository.findById(id);
      if (!queried.isPresent()) {
        throw new ServiceException(Failed.FIND_BY_ID_NO_CONTENT);
      }
      DeviceResponse response = deviceMapper.asResponse(queried.get());
      return response;
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.FIND_BY_ID);
    }
  }

  @Override
  public MultiResource<DeviceResponse> findAll(int page, int size) {
    try {
      Page<Device> pageable = jpaDeviceRepository.findAll(PageRequest.of(page, size));
      List<DeviceResponse> values = deviceMapper.asCollectionResponse(pageable.getContent());
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

  @Override
  public MultiResource<DeviceResponse> findAllByDeletedTrue(int page, int size) {
    try {
      Page<Device> pageable = jpaDeviceRepository.findAllByDeletedTrue(PageRequest.of(page, size));
      List<DeviceResponse> values = deviceMapper.asCollectionResponse(pageable.getContent());
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

  @Override
  public MultiResource<DeviceResponse> findAllByUserAgentContains(
      String userAgent, int page, int size) {
    try {
      Page<Device> pageable =
          jpaDeviceRepository.findAllByUserAgentContains(userAgent, PageRequest.of(page, size));
      List<DeviceResponse> values = deviceMapper.asCollectionResponse(pageable.getContent());
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

  @Override
  public DeviceResponse updateById(Long id, DeviceUpdate update) {
    try {
      Optional<Device> old = jpaDeviceRepository.findById(id);
      if (!old.isPresent()) {
        throw new ServiceException(Failed.NOT_EXISTS);
      }
      Device value = old.get();
      String oldIdentity = value.getIpAddress();
      deviceMapper.mergeModel(value, update);
      if (!oldIdentity.equals(value.getIpAddress())) {
        this.ensureNotExistedByIpAddress(value.getIpAddress());
      }
      Device model = jpaDeviceRepository.save(value);
      DeviceResponse response = deviceMapper.asResponse(model);
      return response;
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.UPDATE);
    }
  }

  @Override
  public DeviceResponse deleteById(Long id) {
    try {
      Optional<Device> old = jpaDeviceRepository.findById(id);
      if (!old.isPresent()) {
        throw new ServiceException(Failed.NOT_EXISTS);
      }
      Device value = old.get();
      DeviceResponse response = deviceMapper.asResponse(value);
      jpaDeviceRepository.delete(value);
      return response;
    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(Failed.DELETE);
    }
  }
}
