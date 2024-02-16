package br.com.microservices.orchestrated.orderservice;


public enum SagaStatus {
    SUCCESS,
    ROLLBACK_PENDING,
    FAIL
}
