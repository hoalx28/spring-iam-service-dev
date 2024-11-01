package springproject.iam.v1.mapper.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import springproject.iam.v1.controller.individual.RoleController;
import springproject.iam.v1.model.dto.role.RoleResponse;

@Component
public class RoleModelAssembler
    implements RepresentationModelAssembler<RoleResponse, EntityModel<RoleResponse>> {

  @NonNull @Override
  public EntityModel<RoleResponse> toModel(@NonNull RoleResponse entity) {
    EntityModel<RoleResponse> model = EntityModel.of(entity);
    String name = entity.getName();
    Link self = linkTo(methodOn(RoleController.class).findById(entity.getId())).withSelfRel();
    Link collection = linkTo(methodOn(RoleController.class).findAll(0, 50)).withRel("collection");
    Link search =
        linkTo(methodOn(RoleController.class).findAllByNameContains(name, 0, 50)).withRel("search");
    Link archived =
        linkTo(methodOn(RoleController.class).findAllByDeletedTrue(0, 50)).withRel("archived");
    model.add(List.of(self, collection, search, archived));
    return model;
  }
}
