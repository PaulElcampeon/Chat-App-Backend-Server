package com.P.G.chatappbackend.repositiories;

import com.P.G.chatappbackend.models.Mail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends MongoRepository<Mail, ObjectId> {
}
