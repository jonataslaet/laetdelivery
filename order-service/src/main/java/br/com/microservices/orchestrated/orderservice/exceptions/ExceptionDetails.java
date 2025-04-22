package br.com.microservices.orchestrated.orderservice.exceptions;

public record ExceptionDetails(int status, String message) {
}
