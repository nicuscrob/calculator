package com.example.calculator.controller;

import com.example.calculator.model.OperationResult;
import com.example.calculator.service.ResultsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/result")
@Api(value = "/result", description = "API for operation results")
public class ResultsController {

    @Autowired
    private ResultsService resultsService;

    @ApiOperation(
            value = "Retrieves the list of operation results",
            response = OperationResult.class,
            responseContainer = "Collection"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response"),
    })
    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<OperationResult>> list() {

        return ResponseEntity.ok().body(resultsService.list());
    }

    @ApiOperation(
            value = "Get the operation result for given id",
            response = OperationResult.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response"),
            @ApiResponse(code = 404, message = "No entities found for the given id")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OperationResult> get(@PathVariable("id") String id) {

        return ofNullable(resultsService.get(id))
                .map((r) -> ResponseEntity.ok().body(r))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ApiOperation(
            value = "Acknowledges a response by id",
            response = OperationResult.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response"),
    })
    @RequestMapping(value = "/ack/{id}", method = RequestMethod.POST)
    public void ack(@PathVariable("id") String id) {

        resultsService.ack(id);
    }

    void setResultsService(ResultsService resultsService) {
        this.resultsService = resultsService;
    }
}
