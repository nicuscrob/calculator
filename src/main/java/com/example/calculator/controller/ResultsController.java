package com.example.calculator.controller;

import com.example.calculator.model.OperationResult;
import com.example.calculator.service.ResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/result")
public class ResultsController {

    @Autowired
    private ResultsService resultsService;

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<OperationResult> list() {
        return resultsService.list();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public OperationResult get(@PathVariable("id") Long id) {
        return resultsService.get(id);
    }

    @RequestMapping(value = "/ack/{id}", method = RequestMethod.POST)
    public void ack(@PathVariable("id") long id) {
        resultsService.ack(id);
    }

}
