package springproject.iam.v1.validator.status;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import springproject.iam.v1.repository.status.JpaStatusRepository;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UniqueStatusContentValidator
    implements ConstraintValidator<UniqueStatusContent, String> {
  JpaStatusRepository jpaStatusRepository;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (Objects.isNull(value)) {
      return true;
    }
    return jpaStatusRepository.countExistedByContent(value) == 0;
  }

  @Override
  public void initialize(UniqueStatusContent constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }
}
