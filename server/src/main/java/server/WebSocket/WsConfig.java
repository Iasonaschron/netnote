package server.WebSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WsConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker for WebSocket communication
     *
     * @param regBrker Defines the application's message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry regBrker) {
        // Used by clients to subscribe to topics such as collection and note changes
        regBrker.enableSimpleBroker("/topic");
        // Used by clients to send messages to the server
        regBrker.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the endpoint for the WebSocket connection
     * @param regStmp The registry for the STOMP endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry regStmp) {
        regStmp.addEndpoint("/ws").setAllowedOrigins("*");
        // Fallback option for older systems
        regStmp.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}

