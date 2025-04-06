package com.example.pfespace.Repository;

import com.example.pfespace.Entity.PlagiarismReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlagiarismReportRepository extends JpaRepository<PlagiarismReport, Long> {
    PlagiarismReport findByDeliverableId(Long deliverableId);
} 