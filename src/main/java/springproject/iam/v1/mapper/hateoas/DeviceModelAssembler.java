package springproject.iam.v1.mapper.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import springproject.iam.v1.controller.individual.DeviceController;
import springproject.iam.v1.model.dto.device.DeviceResponse;

@Component
public class DeviceModelAssembler
    implements RepresentationModelAssembler<DeviceResponse, EntityModel<DeviceResponse>> {

  @NonNull @Override
  public EntityModel<DeviceResponse> toModel(@NonNull DeviceResponse entity) {
    EntityModel<DeviceResponse> model = EntityModel.of(entity);
    String userAgent = entity.getUserAgent();
    Link self = linkTo(methodOn(DeviceController.class).findById(entity.getId())).withSelfRel();
    Link collection = linkTo(methodOn(DeviceController.class).findAll(0, 50)).withRel("collection");
    Link search =
        linkTo(methodOn(DeviceController.class).findAllByUserAgentContains(userAgent, 0, 50))
            .withRel("search");
    Link archived =
        linkTo(methodOn(DeviceController.class).findAllByDeletedTrue(0, 50)).withRel("archived");
    model.add(List.of(self, collection, search, archived));
    return model;
  }
}
