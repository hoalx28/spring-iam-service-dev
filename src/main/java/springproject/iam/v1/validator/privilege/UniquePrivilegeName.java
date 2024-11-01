package springproject.iam.v1.validator.privilege;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniquePrivilegeNameValidator.class})
public @interface UniquePrivilegeName {
  String message() default "privilege name must be unique, this name already exists.";

  int min() default 0;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
