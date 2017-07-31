package com.example.calculator.controller;

import com.example.calculator.model.Operation;
import com.example.calculator.model.OperationResult;
import com.example.calculator.service.ComputationService;
import com.example.calculator.service.ResultsService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ComputationControllerTest {

    @Mock
    private ComputationService computationService;
    @Mock
    private ResultsService resultsService;
    @InjectMocks
    private ComputationController victim;

    @BeforeMethod
    public void init() {
        victim = new ComputationController();
        MockitoAnnotations.initMocks(this);
        victim.setComputationService(computationService);
        victim.setResultsService(resultsService);
    }

    @Test
    public void compute_Operation_ShoudComputeAndSave() {
        //given
        final Operation operation = Operation.builder().id(1L).build();
        ArgumentCaptor<OperationResult> resultCaptor = ArgumentCaptor.forClass(OperationResult.class);
        doNothing().when(resultsService).save(resultCaptor.capture());
        when(computationService.compute(operation)).thenReturn(BigDecimal.ONE);
        //when
        final OperationResult result= victim.compute(operation);
        //then
        verify(computationService).compute(operation);
        verify(resultsService).save(resultCaptor.getValue());
        assertEquals(BigDecimal.ONE, resultCaptor.getValue().getValue());
        assertTrue(result.isSuccess());

    }

    @Test
    public void compute_InvalidOperation_ShoudReturnFalse() {
        //given
        final Operation operation = Operation.builder().id(1L).build();
        ArgumentCaptor<OperationResult> resultCaptor = ArgumentCaptor.forClass(OperationResult.class);
        doNothing().when(resultsService).save(resultCaptor.capture());
        when(computationService.compute(operation)).thenThrow(new IllegalArgumentException("message"));
        //when
        final OperationResult result = victim.compute(operation);
        //then
        verify(computationService).compute(operation);
        verify(resultsService).save(resultCaptor.getValue());
        assertFalse(resultCaptor.getValue().isSuccess());
        assertEquals("message", result.getErrorMessage());
        assertFalse(result.isSuccess());

    }

}
