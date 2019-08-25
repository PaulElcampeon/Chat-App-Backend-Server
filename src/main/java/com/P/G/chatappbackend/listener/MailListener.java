package com.P.G.chatappbackend.listener;

import com.P.G.chatappbackend.ChatAppBackendApplication;
import com.P.G.chatappbackend.models.MessageConverter;
import com.P.G.chatappbackend.services.PublicChatRoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import static jdk.nashorn.internal.objects.NativeMath.log;

@Component
public class MailListener {

    private PublicChatRoomService publicChatRoomService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = Logger.getLogger(MailListener.class.getName());

    public MailListener(PublicChatRoomService publicChatRoomService) {
        this.publicChatRoomService = publicChatRoomService;
    }

    @RabbitListener(queues = ChatAppBackendApplication.MAIL_MESSAGE_QUEUE)
    public void receiveMessage(final MessageConverter message) {
        System.out.println(message);
        log("Received message as generic: {}", message.toString());
    }


    //When sending text
//    public void receiveMessage(byte[] bytes) throws UnsupportedEncodingException {
//        log.info("Received <" + getMessage(bytes) + ">");
//        Long id = Long.valueOf(message.get("id"));
//        Product product = productRepository.findById(id).orElse(null);
//        product.setMessageReceived(true);
//        product.setMessageCount(product.getMessageCount() + 1);
//
//        productRepository.save(product);
//        log.info("Message processed...");
//    }

    private String getMessage(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "utf-8");
    }
}
