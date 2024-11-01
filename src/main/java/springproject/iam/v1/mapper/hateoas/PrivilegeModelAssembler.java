package springproject.iam.v1.mapper.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import springproject.iam.v1.controller.individual.PrivilegeController;
import springproject.iam.v1.model.dto.privilege.PrivilegeResponse;

@Component
public class PrivilegeModelAssembler
    implements RepresentationModelAssembler<PrivilegeResponse, EntityModel<PrivilegeResponse>> {

  @NonNull @Override
  public EntityModel<PrivilegeResponse> toModel(@NonNull PrivilegeResponse entity) {
    EntityModel<PrivilegeResponse> model = EntityModel.of(entity);
    String name = entity.getName();
    Link self = linkTo(methodOn(PrivilegeController.class).findById(entity.getId())).withSelfRel();
    Link collection =
        linkTo(methodOn(PrivilegeController.class).findAll(0, 50)).withRel("collection");
    Link search =
        linkTo(methodOn(PrivilegeController.class).findAllByNameContains(name, 0, 50))
            .withRel("search");
    Link archived =
        linkTo(methodOn(PrivilegeController.class).findAllByDeletedTrue(0, 50)).withRel("archived");
    model.add(List.of(self, collection, search, archived));
    return model;
  }
}
