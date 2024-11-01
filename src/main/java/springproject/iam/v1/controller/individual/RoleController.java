package springproject.iam.v1.controller.individual;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springproject.iam.v1.constant.Success;
import springproject.iam.v1.mapper.hateoas.RoleModelAssembler;
import springproject.iam.v1.model.dto.role.RoleCreation;
import springproject.iam.v1.model.dto.role.RoleResponse;
import springproject.iam.v1.model.dto.role.RoleUpdate;
import springproject.iam.v1.response.MultiResource;
import springproject.iam.v1.response.PagingResponse;
import springproject.iam.v1.response.Response;
import springproject.iam.v1.service.role.AbstractRoleService;

@Hidden
@RestController
@RequestMapping(path = "/api/v1/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
  @Qualifier("jpaRoleService") AbstractRoleService jpaRoleService;

  RoleModelAssembler roleModelAssembler;

  @PostMapping
  public ResponseEntity<Response<Long>> save(@RequestBody @Valid RoleCreation creation) {
    Success created = Success.SAVE;
    RoleResponse saved = jpaRoleService.save(creation);
    Response<Long> response =
        Response.<Long>builder()
            .code(created.getCode())
            .message(created.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(saved.getId())
            .build();
    return ResponseEntity.status(created.getHttpStatus()).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Response<EntityModel<RoleResponse>>> findById(
      @PathVariable(name = "id", required = true) Long id) {
    Success ok = Success.FIND_BY_ID;
    RoleResponse queried = jpaRoleService.findById(id);

    EntityModel<RoleResponse> model = roleModelAssembler.toModel(queried);
    Response<EntityModel<RoleResponse>> response =
        Response.<EntityModel<RoleResponse>>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(model)
            .build();
    return ResponseEntity.ok().body(response);
  }

  @GetMapping
  public ResponseEntity<PagingResponse<CollectionModel<EntityModel<RoleResponse>>>> findAll(
      @RequestParam(name = "page", defaultValue = "0")
          @Min(value = 0, message = "page index must be start from 0.")
          int page,
      @RequestParam(name = "size", defaultValue = "50")
          @Min(value = 5, message = "page size must be greater than 5.")
          @Max(value = 50, message = "page size must be less than 50.")
          int size) {
    Success ok = Success.FIND_ALL;
    MultiResource<RoleResponse> queried = jpaRoleService.findAll(page, size);

    CollectionModel<EntityModel<RoleResponse>> models =
        roleModelAssembler.toCollectionModel(queried.getValues());
    models.add(linkTo(methodOn(this.getClass()).findAll(page, size)).withSelfRel());
    PagingResponse<CollectionModel<EntityModel<RoleResponse>>> response =
        PagingResponse.<CollectionModel<EntityModel<RoleResponse>>>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(models)
            .paging(queried.getPaging())
            .build();
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/archived")
  public ResponseEntity<PagingResponse<CollectionModel<EntityModel<RoleResponse>>>>
      findAllByDeletedTrue(
          @RequestParam(name = "page", defaultValue = "0")
              @Min(value = 0, message = "page index must be start from 0.")
              int page,
          @RequestParam(name = "size", defaultValue = "50")
              @Min(value = 5, message = "page size must be greater than 5.")
              @Max(value = 50, message = "page size must be less than 50.")
              int size) {
    Success ok = Success.FIND_ALL;
    MultiResource<RoleResponse> queried = jpaRoleService.findAllByDeletedTrue(page, size);

    CollectionModel<EntityModel<RoleResponse>> models =
        roleModelAssembler.toCollectionModel(queried.getValues());
    models.add(linkTo(methodOn(this.getClass()).findAllByDeletedTrue(page, size)).withSelfRel());
    PagingResponse<CollectionModel<EntityModel<RoleResponse>>> response =
        PagingResponse.<CollectionModel<EntityModel<RoleResponse>>>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(models)
            .paging(queried.getPaging())
            .build();
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/search")
  public ResponseEntity<PagingResponse<CollectionModel<EntityModel<RoleResponse>>>>
      findAllByNameContains(
          @RequestParam(name = "name", required = true, defaultValue = "") String name,
          @RequestParam(name = "page", defaultValue = "0")
              @Min(value = 0, message = "page index must be start from 0.")
              int page,
          @RequestParam(name = "size", defaultValue = "50")
              @Min(value = 5, message = "page size must be greater than 5.")
              @Max(value = 50, message = "page size must be less than 50.")
              int size) {
    Success ok = Success.FIND_ALL_BY;
    MultiResource<RoleResponse> queried = jpaRoleService.findAllByNameContains(name, page, size);

    CollectionModel<EntityModel<RoleResponse>> models =
        roleModelAssembler.toCollectionModel(queried.getValues());
    models.add(
        linkTo(methodOn(this.getClass()).findAllByNameContains(name, page, size)).withSelfRel());
    PagingResponse<CollectionModel<EntityModel<RoleResponse>>> response =
        PagingResponse.<CollectionModel<EntityModel<RoleResponse>>>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(models)
            .paging(queried.getPaging())
            .build();
    return ResponseEntity.ok().body(response);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Response<Long>> updateById(
      @PathVariable(name = "id", required = true) Long id, @RequestBody RoleUpdate update) {
    Success ok = Success.UPDATE;
    RoleResponse latest = jpaRoleService.updateById(id, update);
    Response<Long> response =
        Response.<Long>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(latest.getId())
            .build();
    return ResponseEntity.ok().body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Response<Long>> deleteById(
      @PathVariable(name = "id", required = true) Long id) {
    Success ok = Success.DELETE;
    RoleResponse old = jpaRoleService.deleteById(id);
    Response<Long> response =
        Response.<Long>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(old.getId())
            .build();
    return ResponseEntity.ok().body(response);
  }
}
