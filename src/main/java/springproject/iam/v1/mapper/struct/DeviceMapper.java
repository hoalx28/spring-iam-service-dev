package springproject.iam.v1.mapper.struct;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import springproject.iam.v1.model.Device;
import springproject.iam.v1.model.dto.device.DeviceCreation;
import springproject.iam.v1.model.dto.device.DeviceResponse;
import springproject.iam.v1.model.dto.device.DeviceUpdate;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceMapper {
  Device asModel(DeviceCreation creation);

  DeviceResponse asResponse(Device model);

  List<DeviceResponse> asCollectionResponse(List<Device> models);

  void mergeModel(@MappingTarget Device model, DeviceUpdate update);
}
