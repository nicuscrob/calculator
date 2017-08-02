package com.example.calculator.service;

import com.example.calculator.model.OperationResult;
import com.google.common.cache.Cache;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * Class that contains the functionality for the operation result
 */
@Service
public class ResultsService {

    @Resource(name = "resultsCache")
    private Cache<String, OperationResult> cache;

    /**
     * Lists all cached results and invalidates cache
     * @return Collection of results
     */
    public Collection<OperationResult> list() {
        final Collection<OperationResult> results = cache.asMap().values();
        cache.invalidateAll();
        return results;
    }

    /**
     * Retrieve a result from cache by id, and invalidate cache
     * @return Operation result from cache
     */
    public OperationResult get(String id) {
        final OperationResult result = cache.getIfPresent(id);
        cache.invalidate(id);
        return result;
    }

    /**
     * Acknowledgement of an operation id, it removes the result from cache
     * @param id for which to acknowledge
     */
    public void ack(String id) {
        cache.invalidate(id);
    }

    /**
     * Save a new operation result in the cache
     * @param result The result to be saved
     */
    public void save(OperationResult result) {
        cache.put(result.getId(), result);
    }

    @VisibleForTesting
    void setCache(Cache<String, OperationResult> cache) {
        this.cache = cache;
    }
}
