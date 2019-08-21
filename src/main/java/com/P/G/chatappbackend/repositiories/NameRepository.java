package com.P.G.chatappbackend.repositiories;

import com.P.G.chatappbackend.models.NameHolder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NameRepository extends MongoRepository<NameHolder, String> {
}
