package com.example.calculator.controller;

import com.example.calculator.model.OperationResult;
import com.example.calculator.service.ResultsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class ResultsControllerTest {

    @Mock
    private ResultsService resultsService;
    @InjectMocks
    private ResultsController victim;

    private static final String ID = "id";


    @BeforeMethod
    public void init() {
        victim = new ResultsController();
        MockitoAnnotations.initMocks(this);
        victim.setResultsService(resultsService);
    }

    @Test
    public void list_CachedResponses_ShouldReturn() {
        //given
        when(resultsService.list()).thenReturn(new HashSet<>());
        //when
        final ResponseEntity<Collection<OperationResult>> response = victim.list();
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void get_CachedResponse_ShouldReturnOk() {
        //given
        final OperationResult operationResult = OperationResult.builder().build();
        when(resultsService.get(ID)).thenReturn(operationResult);
        //when
        final ResponseEntity<OperationResult> response = victim.get(ID);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(operationResult, response.getBody());
    }

    @Test
    public void get_NoResponse_ShouldReturnNotFound() {
        //given
        when(resultsService.get(ID)).thenReturn(null);
        //when
        final ResponseEntity<OperationResult> response = victim.get(ID);
        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void ack_CachedResponse_ShouldAck() {
        //when
        victim.ack(ID);
        //then
        verify(resultsService).ack(ID);
    }
}
