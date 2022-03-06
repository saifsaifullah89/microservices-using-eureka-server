package com.microservices.customer.service;

import com.microservices.customer.model.Customer;
import com.microservices.customer.record.CustomerRegistrationRequest;
import com.microservices.customer.repository.CustomerRepository;
import com.microservices.customer.tempResponse.TemporaryCheckResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@Service
public class CustomerService{

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer= Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        //todo: check if email valid
        //todo: check if email not taken

        //store customer in db
        customerRepository.saveAndFlush(customer);
        //check if customer is temporary
       TemporaryCheckResponse temporaryCheckResponse= restTemplate.getForObject(

                       "http://TEMPORARY/api/v1/temporary/{customerId}",
                       TemporaryCheckResponse.class,
                       customer.getId()
               );

        assert temporaryCheckResponse != null;
        if(temporaryCheckResponse.isTemporary()){
           throw new IllegalStateException("is Temporary Customer.");
       }


    }
}
