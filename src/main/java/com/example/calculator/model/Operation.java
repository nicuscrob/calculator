package com.example.calculator.model;

import com.example.calculator.service.Operand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@ToString
public class Operation implements Serializable {

    private String id;

    private BigDecimal left;

    private BigDecimal right;

    private Operand operand;

    private int sleep;

}
