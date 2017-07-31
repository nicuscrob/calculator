package com.example.calculator.service;

import com.example.calculator.model.OperationResult;
import com.google.common.cache.Cache;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class ResultsServiceTest {

    @Mock
    private Cache<Long, OperationResult> cache;
    @InjectMocks
    private ResultsService victim;

    private final Long ID = 1l;

    @BeforeMethod
    public void init() {
        victim = new ResultsService();
        MockitoAnnotations.initMocks(this);
        victim.setCache(cache);
    }

    @Test
    public void list_AllResults_ShouldReturnAndInvalidate() {
        //given
        when(cache.asMap()).thenReturn(new ConcurrentHashMap<>());
        //when
        victim.list();
        //then
        verify(cache).asMap();
        verify(cache).invalidateAll();
    }

    @Test
    public void get_ExistingId_ShoudReturnAndInvalidate() {
        final OperationResult operationResult = OperationResult.builder().build();
        //given
        when(cache.getIfPresent(ID)).thenReturn(operationResult);
        //when
        final OperationResult result = victim.get(ID);
        //then
        assertEquals(operationResult, result);
        verify(cache).invalidate(ID);
    }

    @Test
    public void ack_ExitingId_ShouldInvalidate() {
        //when
        victim.ack(ID);
        //then
        verify(cache).invalidate(ID);
    }

    @Test
    public void save_OperationResult_ShouldStoreInCache() {
        //given
        final OperationResult result = OperationResult.builder().id(ID).build();
        //when
        victim.save(result);
        //then
        verify(cache).put(ID, result);
    }

}
