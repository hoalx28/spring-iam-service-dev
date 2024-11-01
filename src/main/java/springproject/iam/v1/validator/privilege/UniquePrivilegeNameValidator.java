package springproject.iam.v1.validator.privilege;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import springproject.iam.v1.repository.privilege.JpaPrivilegeRepository;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UniquePrivilegeNameValidator
    implements ConstraintValidator<UniquePrivilegeName, String> {
  @NonFinal int min;

  JpaPrivilegeRepository jpaPrivilegeRepository;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (Objects.isNull(value)) {
      return true;
    }
    return jpaPrivilegeRepository.countExistedByName(value) == 0;
  }

  @Override
  public void initialize(UniquePrivilegeName constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    min = constraintAnnotation.min();
  }
}
