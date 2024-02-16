package br.com.microservices.orchestrated.orderservice.enums;


public enum SagaStatusEnum {
    SUCCESS,
    ROLLBACK_PENDING,
    FAIL
}
