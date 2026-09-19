package com.shailu.deposito_dental_pos.model.enums;

public enum MovementReason {
    PURCHASE("Compra"),
    SALE("Venta"),
    RETURN_FROM_CUSTOMER("Devolución de la cliente"),
    RETURN_TO_SUPPLIER("Regresar al proveedor"),
    INVENTORY_ADJUSTMENT("Ajuste de inventario"),
    DAMAGED_PRODUCT("Producto dañado"),
    INITIAL_STOCK("Stock inicial");

    private final String reason;

    MovementReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
