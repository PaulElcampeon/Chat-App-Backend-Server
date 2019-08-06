package com.P.G.chatappbackend.repositiories;

import java.util.List;

import com.P.G.chatappbackend.models.Message;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, ObjectId> {

    List<Message> findFirst10By_idLessThan(ObjectId _id);

    List<Message> findFirst10ByOrderByTimeSentDesc();
}
