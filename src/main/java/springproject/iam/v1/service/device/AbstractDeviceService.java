package springproject.iam.v1.service.device;

import springproject.iam.v1.model.dto.device.DeviceCreation;
import springproject.iam.v1.model.dto.device.DeviceResponse;
import springproject.iam.v1.model.dto.device.DeviceUpdate;
import springproject.iam.v1.response.MultiResource;

public interface AbstractDeviceService {
  void ensureNotExistedByIpAddress(String ipAddress);

  DeviceResponse save(DeviceCreation creation);

  DeviceResponse findById(Long id);

  MultiResource<DeviceResponse> findAll(int page, int size);

  MultiResource<DeviceResponse> findAllByDeletedTrue(int page, int size);

  MultiResource<DeviceResponse> findAllByUserAgentContains(String userAgent, int page, int size);

  DeviceResponse updateById(Long id, DeviceUpdate update);

  DeviceResponse deleteById(Long id);
}
