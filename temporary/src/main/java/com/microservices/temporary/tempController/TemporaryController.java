package com.microservices.temporary.tempController;

import com.microservices.temporary.tempResponse.TemporaryCheckResponse;
import com.microservices.temporary.tempServicec.TemporaryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/v1/temporary/")
public class TemporaryController {

    private final TemporaryService temporaryService;



    @GetMapping(path = "{customerId}")
    public TemporaryCheckResponse isTemporary(@PathVariable("customerId") Integer customerId){

       boolean isTemporaryCustomer= temporaryService.isTemporaryCustomer(customerId);
       log.info("Temporary Service Check for customer {}", customerId);
        return new TemporaryCheckResponse(isTemporaryCustomer);
    }
}
