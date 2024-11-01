package springproject.iam.v1.mapper.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import springproject.iam.v1.controller.individual.UserController;
import springproject.iam.v1.model.dto.user.UserResponse;

@Component
public class UserModelAssembler
    implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

  @NonNull @Override
  public EntityModel<UserResponse> toModel(@NonNull UserResponse entity) {
    EntityModel<UserResponse> model = EntityModel.of(entity);
    String username = entity.getUsername();
    Link self = linkTo(methodOn(UserController.class).findById(entity.getId())).withSelfRel();
    Link collection = linkTo(methodOn(UserController.class).findAll(0, 50)).withRel("collection");
    Link search =
        linkTo(methodOn(UserController.class).findAllByUsernameContains(username, 0, 50))
            .withRel("search");
    Link archived =
        linkTo(methodOn(UserController.class).findAllByDeletedTrue(0, 50)).withRel("archived");
    model.add(List.of(self, collection, search, archived));
    return model;
  }
}
