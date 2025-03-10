package com.gobag.gobag_service.repository;

import com.gobag.gobag_service.model.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByCustomerNumber(String customerNumber);
}
