package com.example.calculator.controller;

import com.example.calculator.model.Operation;
import com.example.calculator.model.OperationCreateDto;
import com.example.calculator.model.OperationResult;
import com.example.calculator.model.OperationResult.OperationResultBuilder;
import com.example.calculator.service.ComputationService;
import com.example.calculator.service.ResultsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.example.calculator.mapper.OperationMapper.toOperation;

@Slf4j
@RestController
public class ComputationController {

    @Autowired
    private ComputationService computationService;

    @Autowired
    private ResultsService resultsService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @MessageMapping("/compute")
    @SendTo("/topic/results")
    public OperationResult compute(Operation operation) {
        return doCompute(operation);
    }

    @ApiOperation(
            value = "Add a new Operation, method returns the resource saved," +
                    " which can be used to query the result",
            response = Operation.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operation has been added"),
    })
    @RequestMapping(value = "/compute", method = RequestMethod.POST)
    public Operation add(@RequestBody final OperationCreateDto createDto) {
        final Operation operation = toOperation(createDto);
        executorService.submit(() -> this.doCompute(operation));
        return operation;
    }

    private OperationResult doCompute(Operation operation) {
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
