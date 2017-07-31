package com.example.calculator.service;

import com.example.calculator.model.OperationResult;
import com.google.common.cache.Cache;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

@Service
public class ResultsService {

    @Resource(name = "resultsCache")
    private Cache<Long, OperationResult> cache;

    public Collection<OperationResult> list() {
        final Collection<OperationResult> results = cache.asMap().values();
        cache.invalidateAll();
        return results;
    }

    public OperationResult get(Long id) {
        final OperationResult result = cache.getIfPresent(id);
        cache.invalidate(id);
        return result;
    }

    public void ack(Long id) {
        cache.invalidate(id);
    }

    public void save(OperationResult result) {
        cache.put(result.getId(), result);
    }

    @VisibleForTesting
    void setCache(Cache<Long, OperationResult> cache) {
        this.cache = cache;
    }
}
