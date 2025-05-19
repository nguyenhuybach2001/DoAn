package com.bach.RoomRentalManagementSystem.config;

import com.bach.RoomRentalManagementSystem.model.MaintenanceExpenses;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneAssetValidator implements ConstraintValidator<AtLeastOneAssetPresent, MaintenanceExpenses> {

    @Override
    public boolean isValid(MaintenanceExpenses expenses, ConstraintValidatorContext context) {
        return expenses.getAssetInRoom() != null || expenses.getAssetOfBuilding() != null;
    }
}
