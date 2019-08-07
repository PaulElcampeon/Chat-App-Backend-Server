package com.P.G.chatappbackend.repositiories;

import com.P.G.chatappbackend.models.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivateChatRoomRepository extends MongoRepository<Room, String> {
}
