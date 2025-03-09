package com.gobag.gobag_service.repository;

import com.gobag.gobag_service.model.WhatsAppLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhatsAppLogRepository extends JpaRepository<WhatsAppLog, Long> {
    List<WhatsAppLog> findByPhoneNumber(String phoneNumber);
}
