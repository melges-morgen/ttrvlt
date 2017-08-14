package ru.revolut.testtask.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class TransactionAmountValidator  implements ConstraintValidator<ValidTransactionAmount, BigDecimal> {
    @Override
    public void initialize(ValidTransactionAmount validTransactionAmount) {

    }

    @Override
    public boolean isValid(BigDecimal bigDecimal, ConstraintValidatorContext constraintValidatorContext) {
        return bigDecimal.compareTo(BigDecimal.ZERO) > 0;
    }
}
