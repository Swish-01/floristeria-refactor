package com.floristeria.blomst.enumeration.budget;

public enum PreBudgetStatus {
    /**
     * El prepresupuesto ha sido creado pero no se ha procesado.
     */
    CREATED,

    /**
     * El prepresupuesto está siendo revisado o procesado.
     */
    UNDER_REVIEW,

    /**
     * El prepresupuesto ha sido aprobado y está listo para proceder.
     */
    APPROVED,

    /**
     * El prepresupuesto ha sido rechazado por el cliente o el equipo.
     */
    REJECTED,

    /**
     * El prepresupuesto ha sido completado y está listo para que el cliente lo revise.
     */
    COMPLETED,

    /**
     * El prepresupuesto está pendiente de confirmación por parte del cliente.
     */
    PENDING_CONFIRMATION,

    /**
     * El prepresupuesto ha sido cancelado.
     */
    CANCELED


}
