package com.example.calculator.controller;

import com.example.calculator.model.Operation;
import com.example.calculator.model.OperationResult;
import com.example.calculator.model.OperationResult.OperationResultBuilder;
import com.example.calculator.service.ComputationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class CalculatorAPI {

    @Autowired
    private ComputationService computationService;

    @MessageMapping("/compute")
    @SendTo("/topic/results")
    public OperationResult compute(Operation operation) {
        final OperationResultBuilder resultBuilder = OperationResult.builder();

        try {
            resultBuilder.value(computationService.compute(operation))
                .success(true);
        } catch (Exception e) {
            log.error("Error computing operation {}", operation, e);
            resultBuilder.errorMessage(e.getMessage())
                .success(false);
        }

        sleepFor(operation.getSleep());
        return resultBuilder.build();
    }

    private void sleepFor(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
