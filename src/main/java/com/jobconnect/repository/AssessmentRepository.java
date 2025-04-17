package com.jobconnect.repository;

import com.jobconnect.model.Assessment;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends MongoRepository<Assessment, String> {
    Optional<Assessment> findByTitle(String title);
}
