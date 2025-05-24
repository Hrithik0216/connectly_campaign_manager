package com.connectly_cm.Connectly_CM.repositories.sequence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailSequenceLatestRepository extends MongoRepository<com.connectly_cm.Connectly_CM.models.sequences.EmailSequenceLatest,String> {
}
