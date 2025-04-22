package br.com.microservices.orchestrated.paymentservice.exceptions;

public record ExceptionDetails(int status, String message) {
}
