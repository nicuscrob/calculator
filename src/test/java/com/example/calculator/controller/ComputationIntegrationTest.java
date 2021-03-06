package com.example.calculator.controller;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.calculator.model.Operation;
import com.example.calculator.model.OperationCreateDto;
import com.example.calculator.model.OperationResult;
import com.example.calculator.service.Operand;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
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
public class ComputationIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private final WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
    private TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders headers = new HttpHeaders();

    private final static String ID = "id";

    @Before
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        headers.setContentType(MediaType.APPLICATION_JSON);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void shouldCompute() throws Exception {

        final FinalResponse response = new FinalResponse();
        final StompSession session = getSession(response);

        session.send("/app/compute", getDefaultOperation());
        Thread.sleep(2000);
        assertNotNull("Response not received", response.result);
        assertTrue(response.result.isSuccess());
        assertEquals(BigDecimal.valueOf(2), response.result.getValue());

    }

    @Test
    public void shouldNotReceiveResult() throws Exception {

        final FinalResponse response = new FinalResponse();
        final StompSession session = getSession(response);

        session.send("/app/compute",getDefaultOperation());
        Thread.sleep(500);
        this.stompClient.stop();
        Thread.sleep(500);
        assertNull("Response received", response.result);

    }

    @Test
    public void shouldRetrieveResultAfterDisconnect() throws Exception {
        final FinalResponse response = new FinalResponse();
        final StompSession session = getSession(response);
        //compute
        session.send("/app/compute",getDefaultOperation());
        Thread.sleep(500);
        this.stompClient.stop();
        Thread.sleep(1000);

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        //retrieve result
        ResponseEntity<String> retrieveResult = restTemplate.exchange(
                createURLWithPort("/result/" + ID),
                HttpMethod.GET, entity, String.class);

        assertEquals(retrieveResult.getStatusCode(), HttpStatus.OK);
        assertEquals(getDefaultOperationJson(), retrieveResult.getBody());
    }

    @Test
    public void shouldNotRetrieveTheResultTwice() throws Exception {
        //compute and retrieve result
        shouldRetrieveResultAfterDisconnect();
        //retrieve result again
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> retrieveResult = restTemplate.exchange(
                createURLWithPort("/result/" + ID),
                HttpMethod.GET, entity, String.class);

        assertEquals(retrieveResult.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(retrieveResult.getBody());
    }

    @Test
    public void shouldNotRetrieveAfterAcknowledge() throws Exception {
        final FinalResponse response = new FinalResponse();
        final StompSession session = getSession(response);
        //compute
        session.send("/app/compute",getDefaultOperation());
        Thread.sleep(500);
        this.stompClient.stop();
        Thread.sleep(1000);

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> acknowledge = restTemplate.exchange(
                createURLWithPort("/result/ack/" + ID),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.OK, acknowledge.getStatusCode());

        ResponseEntity<String> retrieveResult = restTemplate.exchange(
                createURLWithPort("/result/" + ID),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, retrieveResult.getStatusCode());
        assertNull(retrieveResult.getBody());
    }

    @Test
    public void shouldAddOperationToCompute() throws Exception {

        HttpEntity<String> entity = new HttpEntity<>(getOperationCreateJson(), headers);

        ResponseEntity<String> addOperation = restTemplate.exchange(
                createURLWithPort("/compute/"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.OK, addOperation.getStatusCode());

    }

    @Test
    public void shouldComputeTheAddedOperationOnlyAfterDurationHasPassed() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(getOperationCreateJson(), headers);

        ResponseEntity<String> addOperation = restTemplate.exchange(
                createURLWithPort("/compute/"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.OK, addOperation.getStatusCode());
        JSONObject responseJson = new JSONObject(addOperation.getBody());
        final String opId = responseJson.getString("id");
        //should fail since it hasn't finish executing
        ResponseEntity<String> retrieveResult =  getResult(opId);
        assertEquals(HttpStatus.NOT_FOUND, retrieveResult.getStatusCode());
        Thread.sleep(1100);

        retrieveResult = getResult(opId);
        assertEquals(HttpStatus.OK, retrieveResult.getStatusCode());
        JSONObject resultJson = new JSONObject(retrieveResult.getBody());
        assertEquals(3, resultJson.getLong("value"));


    }

    private ResponseEntity<String> getResult(String id) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(
                createURLWithPort("/result/" + id),
                HttpMethod.GET, entity, String.class);
    }

    private Operation getDefaultOperation() {
        return Operation.builder()
                .left(BigDecimal.ONE)
                .right(BigDecimal.ONE)
                .operand(Operand.ADD)
                .sleep(1)
                .id(ID)
                .build();
    }

    private String getOperationCreateJson() {
        return "{\"left\":1,\"right\":2,\"sleep\":1,\"operand\":\"ADD\"}";
    }

    private String getDefaultOperationJson() {
        return "{\"id\":\"id\",\"value\":2,\"success\":true,\"errorMessage\":null}";
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }


    private StompSession getSession(FinalResponse response) throws Exception {
        OperationStompSessionHandlerAdapter handler = new OperationStompSessionHandlerAdapter(response);

        return this.stompClient.connect("ws://localhost:{port}/websocket",
                this.webSocketHttpHeaders, handler, this.port).get();
    }



    private class OperationStompSessionHandlerAdapter extends StompSessionHandlerAdapter {

        private FinalResponse response;

        public OperationStompSessionHandlerAdapter(FinalResponse response) {
            this.response = response;
        }

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
        }

    }

    private class FinalResponse {

        private OperationResult result;

    }

}