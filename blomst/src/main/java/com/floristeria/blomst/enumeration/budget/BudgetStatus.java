package com.floristeria.blomst.enumeration.budget;

public enum BudgetStatus {
    /**
     * Estado cuando el presupuesto ha sido creado pero no revisado.
     */
    CREATED,

    /**
     * Estado cuando el presupuesto está siendo procesado por el equipo.
     */
    IN_PROGRESS,

    /**
     * Estado cuando el presupuesto ha sido completado y enviado al cliente.
     */
    COMPLETED,

    /**
     * Estado cuando el presupuesto ha sido cancelado por el cliente o el equipo.
     */
    CANCELED,

    /**
     * Estado cuando el presupuesto está pendiente de confirmación por parte del cliente.
     */
    PENDING_CONFIRMATION,

    /**
     * Estado cuando el cliente ha aprobado el presupuesto y está listo para proceder.
     */
    APPROVED
}
