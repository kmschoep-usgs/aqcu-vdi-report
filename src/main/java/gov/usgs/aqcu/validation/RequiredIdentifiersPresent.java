package gov.usgs.aqcu.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { RequiredIdentifiersPresentValidator.class })
public @interface RequiredIdentifiersPresent {

	String message() default "VDiagram report missing required Discharge and Stage parameters";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
