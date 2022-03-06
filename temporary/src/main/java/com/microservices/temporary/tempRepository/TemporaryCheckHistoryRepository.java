package com.microservices.temporary.tempRepository;

import com.microservices.temporary.tempModel.TemporaryCheckHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryCheckHistoryRepository extends JpaRepository<TemporaryCheckHistory, Integer> {
}
