package com.microservices.customer.record;

public record CustomerRegistrationRequest(String firstName,
                                          String lastName,
                                          String email) {
}
