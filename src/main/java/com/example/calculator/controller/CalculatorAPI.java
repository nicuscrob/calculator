package com.example.calculator.controller;

import com.example.calculator.model.Operation;
import com.example.calculator.model.OperationResult;
import com.example.calculator.model.OperationResult.OperationResultBuilder;
import com.example.calculator.service.ComputationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/calculator")
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

    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public OperationResult fetch(){
        return OperationResult.builder().build();
    }

    private void sleepFor(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
