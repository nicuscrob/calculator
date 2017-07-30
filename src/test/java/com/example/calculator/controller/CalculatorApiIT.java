package com.example.calculator.controller;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.calculator.model.Operation;
import com.example.calculator.model.OperationResult;
import com.example.calculator.service.Operand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalculatorApiIT {

    @LocalServerPort
    private int port;

    private SockJsClient sockJsClient;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    @Before
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testAdd() throws Exception {

        final FinalResponse response = new FinalResponse();
        StompSessionHandler handler = new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/results", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return OperationResult.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        response.result = (OperationResult) payload;
                    }
                });
                final Operation operation = Operation.builder()
                        .left(BigDecimal.ONE)
                        .right(BigDecimal.ONE)
                        .operand(Operand.ADD)
                        .sleep(2)
                        .build();
                session.send("/app/compute", operation);
            }
        };

        this.stompClient.connect("ws://localhost:{port}/websocket", this.headers, handler, this.port);
        Thread.sleep(3000);
        assertNotNull("Response not received", response.result);
        assertTrue(response.result.isSuccess());
        assertEquals(BigDecimal.valueOf(2), response.result.getValue());


    }

    private class FinalResponse {

        private OperationResult result;

    }

}