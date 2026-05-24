package com.workflow.failureawareworkflow.repository;

import com.workflow.failureawareworkflow.entity.FailureAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface
FailureAnalysisRepository extends JpaRepository<FailureAnalysisEntity, String> {

    Optional<FailureAnalysisEntity> findByWorkflowId(String workflowId);

}
