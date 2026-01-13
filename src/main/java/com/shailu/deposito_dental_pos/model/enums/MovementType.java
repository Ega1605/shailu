package com.shailu.deposito_dental_pos.model.enums;

public enum MovementType {
    IN("Dentro"),
    OUT("Fuera"),
    ADJUSTMENT("Ajuste");

    private final String movementType;

    MovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getMovementType() {
        return movementType;
    }
}
