package com.P.G.chatappbackend.config;

import com.P.G.chatappbackend.services.PublicChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.springframework.messaging.simp.SimpMessageType.CONNECT_ACK;

@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private PublicChatRoomService publicChatRoomService;

    private Logger logger = Logger.getLogger(WebSocketConfig.class.getName());

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ima")//(ima = Instant Messaging App);
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration
                .interceptors(new ChannelInterceptor() {
                    @Override
                    public Message<?> preSend(Message<?> message, MessageChannel channel) {
                        final StompCommand command = (StompCommand) message.getHeaders().get("stompCommand");
                        final String sessionId = (String) message.getHeaders().get("simpSessionId");
                        final StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
                        if (command == StompCommand.DISCONNECT) {

                            publicChatRoomService.removeClientFromOnlineUsers(sessionId);

                            publicChatRoomService.updateChatRoomWithCurrentUsers();
                        }

//                        } else if (command == StompCommand.SUBSCRIBE && stompHeaderAccessor.getDestination().equals("/topic/public-room")) {
//
//                            String username;
//
//                            if (stompHeaderAccessor.containsNativeHeader("username")) {
//
//                                username = stompHeaderAccessor.getNativeHeader("username").get(0);
//
//                                if (username.equals("")) {
//
//                                    publicChatRoomService.giveClientName(sessionId);
//
//                                } else {
//
//                                    publicChatRoomService.addClientToOnlineUsers(username, sessionId);
//                                }
//
//                            } else {
//
//                                publicChatRoomService.giveClientName(sessionId);
//
//                            }
//
//                            publicChatRoomService.updateChatRoomWithCurrentUsers();
//
//                        }
                        return message;
                    }
                });
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration
                .interceptors(new ChannelInterceptor() {
                    @Override
                    public Message<?> preSend(Message<?> message, MessageChannel channel) {
                        final String sessionId = (String) message.getHeaders().get("simpSessionId");
                        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
                        final SimpMessageType messageType = headerAccessor.getMessageType();
                        final GenericMessage connectHeader = (GenericMessage) headerAccessor.getHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER);    // FIXME find a way to pass the username to the server

                        if (messageType == CONNECT_ACK) {
                            final StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECTED);
                            final Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) connectHeader.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

                            if (nativeHeaders.containsKey("username")) {

                                if (nativeHeaders.get("username").get(0).equals("")) {

                                    String name = publicChatRoomService.giveClientName(sessionId);

                                    accessor.addNativeHeader("name", name);
                                } else {

                                    String username = nativeHeaders.get("username").get(0);

                                    publicChatRoomService.addClientToOnlineUsers(username, sessionId);

                                }

                                publicChatRoomService.updateChatRoomWithCurrentUsers();
                            }

                            accessor.setSessionId(sessionId);
                            // add custom headers
                            accessor.addNativeHeader("sessionId", sessionId);

                            final Message<?> newMessage = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

                            return newMessage;
                        }

                        return message;
                    }
                });
    }
}
