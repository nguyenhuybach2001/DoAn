package com.bach.RoomRentalManagementSystem.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AtLeastOneAssetValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneAssetPresent {
    String message() default "At least one of assetInRoom or assetOfBuilding must be present.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
