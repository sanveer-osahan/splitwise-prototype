package com.prototype.splitwise.settlement;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SettlementRequest {

    @NotBlank private String user;
    @NotBlank private String group;
}
