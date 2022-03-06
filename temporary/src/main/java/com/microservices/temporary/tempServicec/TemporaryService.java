package com.microservices.temporary.tempServicec;

import com.microservices.temporary.tempModel.TemporaryCheckHistory;
import com.microservices.temporary.tempRepository.TemporaryCheckHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TemporaryService {

    private final TemporaryCheckHistoryRepository temporaryCheckHistoryRepository;

    public TemporaryService(TemporaryCheckHistoryRepository temporaryCheckHistoryRepository) {
        this.temporaryCheckHistoryRepository = temporaryCheckHistoryRepository;
    }

    public boolean isTemporaryCustomer(Integer customerId){
        temporaryCheckHistoryRepository.save(
                TemporaryCheckHistory.builder()
                        .customerId(customerId)
                        .isTemporary(true)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        return false;
    }
}
