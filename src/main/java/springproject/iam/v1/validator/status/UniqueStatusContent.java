package springproject.iam.v1.validator.status;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueStatusContentValidator.class})
public @interface UniqueStatusContent {
  String message() default "status content must be unique, this content already exists.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
