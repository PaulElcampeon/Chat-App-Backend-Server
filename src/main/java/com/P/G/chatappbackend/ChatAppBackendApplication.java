package com.P.G.chatappbackend;

import com.P.G.chatappbackend.services.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ChatAppBackendApplication {

	@Autowired
	private ChatRoomService chatRoomService;

	public static void main(String[] args) {
		SpringApplication.run(ChatAppBackendApplication.class, args);
	}

	@PostConstruct
	public void init() {
		chatRoomService.initializeNameCache();
	}



}
