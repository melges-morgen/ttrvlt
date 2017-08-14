package ru.revolut.testtask.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TransactionAmountValidator.class)
@Documented
public @interface ValidTransactionAmount {
    String message() default "Amount for transaction should be above zero";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
