package com.example.calculator.mapper;

import com.example.calculator.model.Operation;
import com.example.calculator.model.OperationCreateDto;

import java.util.UUID;

/**
 * Converter for Operations from and to Dto
 */
public class OperationMapper {

    public static Operation toOperation(OperationCreateDto createDto){
        return Operation.builder()
            .id(UUID.randomUUID().toString())
            .left(createDto.getLeft())
            .right(createDto.getRight())
            .sleep(createDto.getSleep())
            .operand(createDto.getOperand())
            .build();

    }
}
