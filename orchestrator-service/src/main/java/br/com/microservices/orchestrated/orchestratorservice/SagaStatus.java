package br.com.microservices.orchestrated.orchestratorservice;


public enum SagaStatus {
    SUCCESS,
    ROLLBACK_PENDING,
    FAIL
}
