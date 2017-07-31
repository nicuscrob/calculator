package com.example.calculator.controller;

import com.example.calculator.model.Operation;
import com.example.calculator.model.OperationResult;
import com.example.calculator.model.OperationResult.OperationResultBuilder;
import com.example.calculator.service.ComputationService;
import com.example.calculator.service.ResultsService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;


import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class ComputationController {

    @Autowired
    private ComputationService computationService;

    @Autowired
    private ResultsService resultsService;

    @MessageMapping("/compute")
    @SendTo("/topic/results")

    public OperationResult compute(Operation operation) {
        final OperationResultBuilder resultBuilder = OperationResult.builder()
                .id(operation.getId());
        try {
            resultBuilder.value(computationService.compute(operation))
                .success(true);
        } catch (Exception e) {
            log.error("Error computing operation {}", operation, e);
            resultBuilder.errorMessage(e.getMessage())
                .success(false);
        }

        sleepFor(operation.getSleep());
        final OperationResult result = resultBuilder.build();
        resultsService.save(result);
        return result;
    }

    private void sleepFor(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @VisibleForTesting
    void setComputationService(ComputationService computationService) {
        this.computationService = computationService;
    }

    @VisibleForTesting
    void setResultsService(ResultsService resultsService) {
        this.resultsService = resultsService;
    }
}
