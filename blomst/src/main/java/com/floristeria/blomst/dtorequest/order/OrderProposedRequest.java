package com.floristeria.blomst.dtorequest.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderProposedRequest {
    @NotNull(message = "ID del pedido no puede ser nulo o vacio")
    private Long orderId;
    @NotNull(message = "ID del florista no puede ser nulo o vacio")
    private Long customerId;
    @NotNull(message = "Proporciona una url de la imagen del pedido")
    private String imageUrl;
    @NotNull(message = "El numero de pedido es obligatorio")
    private String number;
    @NotNull(message = "El destinatario es obligatorio")
    private String sendTo;
    private String dedicatory;
    private String delivery_card;
    @NotNull(message = "La direccion de entrega es obligatoria")
    private String direction;
    private String locality;
    private String state;
    private String postcode;
    private String customer_note;
    @NotNull(message = "Introduce un importe para la propuesta")
    private double allocated_amount;
    private double transport_amount;
    private String product_description;
    private String web;
    private String additionalProductImg;
    private String phone;
    private String deliveryDate;
}
