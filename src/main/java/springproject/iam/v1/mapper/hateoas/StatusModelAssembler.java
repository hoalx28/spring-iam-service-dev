package springproject.iam.v1.mapper.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import springproject.iam.v1.controller.individual.StatusController;
import springproject.iam.v1.model.dto.status.StatusResponse;

@Component
public class StatusModelAssembler
    implements RepresentationModelAssembler<StatusResponse, EntityModel<StatusResponse>> {

  @NonNull @Override
  public EntityModel<StatusResponse> toModel(@NonNull StatusResponse entity) {
    EntityModel<StatusResponse> model = EntityModel.of(entity);
    String content = entity.getContent();
    Link self = linkTo(methodOn(StatusController.class).findById(entity.getId())).withSelfRel();
    Link collection = linkTo(methodOn(StatusController.class).findAll(0, 50)).withRel("collection");
    Link search =
        linkTo(methodOn(StatusController.class).findAllByContentContains(content, 0, 50))
            .withRel("search");
    Link archived =
        linkTo(methodOn(StatusController.class).findAllByDeletedTrue(0, 50)).withRel("archived");
    model.add(List.of(self, collection, search, archived));
    return model;
  }
}
