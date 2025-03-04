package com.floristeria.blomst.enumeration.order;

public enum ProposalStatus {
    PENDING,    // La propuesta ha sido enviada y está esperando una respuesta
    ACCEPTED,   // La propuesta ha sido aceptada por la floristería
    REJECTED,   // La propuesta ha sido rechazada por la floristería
    EXPIRED,    // La propuesta ha expirado sin respuesta
    CANCELLED   // La propuesta ha sido cancelada manualmente antes de una respuesta    
}
