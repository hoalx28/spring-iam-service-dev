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
import springproject.iam.v1.mapper.hateoas.DeviceModelAssembler;
import springproject.iam.v1.model.dto.device.DeviceCreation;
import springproject.iam.v1.model.dto.device.DeviceResponse;
import springproject.iam.v1.model.dto.device.DeviceUpdate;
import springproject.iam.v1.response.MultiResource;
import springproject.iam.v1.response.PagingResponse;
import springproject.iam.v1.response.Response;
import springproject.iam.v1.service.device.AbstractDeviceService;

@Hidden
@RestController
@RequestMapping(path = "/api/v1/devices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceController {
  @Qualifier("jpaDeviceService") AbstractDeviceService jpaDeviceService;

  DeviceModelAssembler deviceModelAssembler;

  @PostMapping
  public ResponseEntity<Response<Long>> save(@RequestBody @Valid DeviceCreation creation) {
    Success created = Success.SAVE;
    DeviceResponse saved = jpaDeviceService.save(creation);
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
  public ResponseEntity<Response<EntityModel<DeviceResponse>>> findById(
      @PathVariable(name = "id", required = true) Long id) {
    Success ok = Success.FIND_BY_ID;
    DeviceResponse queried = jpaDeviceService.findById(id);

    EntityModel<DeviceResponse> model = deviceModelAssembler.toModel(queried);
    Response<EntityModel<DeviceResponse>> response =
        Response.<EntityModel<DeviceResponse>>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(model)
            .build();
    return ResponseEntity.ok().body(response);
  }

  @GetMapping
  public ResponseEntity<PagingResponse<CollectionModel<EntityModel<DeviceResponse>>>> findAll(
      @RequestParam(name = "page", defaultValue = "0")
          @Min(value = 0, message = "page index must be start from 0.")
          int page,
      @RequestParam(name = "size", defaultValue = "50")
          @Min(value = 5, message = "page size must be greater than 5.")
          @Max(value = 50, message = "page size must be less than 50.")
          int size) {
    Success ok = Success.FIND_ALL;
    MultiResource<DeviceResponse> queried = jpaDeviceService.findAll(page, size);

    CollectionModel<EntityModel<DeviceResponse>> models =
        deviceModelAssembler.toCollectionModel(queried.getValues());
    models.add(linkTo(methodOn(this.getClass()).findAll(page, size)).withSelfRel());
    PagingResponse<CollectionModel<EntityModel<DeviceResponse>>> response =
        PagingResponse.<CollectionModel<EntityModel<DeviceResponse>>>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(models)
            .paging(queried.getPaging())
            .build();
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/archived")
  public ResponseEntity<PagingResponse<CollectionModel<EntityModel<DeviceResponse>>>>
      findAllByDeletedTrue(
          @RequestParam(name = "page", defaultValue = "0")
              @Min(value = 0, message = "page index must be start from 0.")
              int page,
          @RequestParam(name = "size", defaultValue = "50")
              @Min(value = 5, message = "page size must be greater than 5.")
              @Max(value = 50, message = "page size must be less than 50.")
              int size) {
    Success ok = Success.FIND_ALL;
    MultiResource<DeviceResponse> queried = jpaDeviceService.findAllByDeletedTrue(page, size);

    CollectionModel<EntityModel<DeviceResponse>> models =
        deviceModelAssembler.toCollectionModel(queried.getValues());
    models.add(linkTo(methodOn(this.getClass()).findAllByDeletedTrue(page, size)).withSelfRel());
    PagingResponse<CollectionModel<EntityModel<DeviceResponse>>> response =
        PagingResponse.<CollectionModel<EntityModel<DeviceResponse>>>builder()
            .code(ok.getCode())
            .message(ok.getMessage())
            .timestamp(System.currentTimeMillis())
            .payload(models)
            .paging(queried.getPaging())
            .build();
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/search")
  public ResponseEntity<PagingResponse<CollectionModel<EntityModel<DeviceResponse>>>>
      findAllByUserAgentContains(
          @RequestParam(name = "user_agent", required = true, defaultValue = "") String userAgent,
          @RequestParam(name = "page", defaultValue = "0")
              @Min(value = 0, message = "page index must be start from 0.")
              int page,
          @RequestParam(name = "size", defaultValue = "50")
              @Min(value = 5, message = "page size must be greater than 5.")
              @Max(value = 50, message = "page size must be less than 50.")
              int size) {
    Success ok = Success.FIND_ALL_BY;
    MultiResource<DeviceResponse> queried =
        jpaDeviceService.findAllByUserAgentContains(userAgent, page, size);

    CollectionModel<EntityModel<DeviceResponse>> models =
        deviceModelAssembler.toCollectionModel(queried.getValues());
    models.add(
        linkTo(methodOn(this.getClass()).findAllByUserAgentContains(userAgent, page, size))
            .withSelfRel());
    PagingResponse<CollectionModel<EntityModel<DeviceResponse>>> response =
        PagingResponse.<CollectionModel<EntityModel<DeviceResponse>>>builder()
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
      @PathVariable(name = "id", required = true) Long id, @RequestBody DeviceUpdate update) {
    Success ok = Success.UPDATE;
    DeviceResponse latest = jpaDeviceService.updateById(id, update);
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
    DeviceResponse old = jpaDeviceService.deleteById(id);
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
