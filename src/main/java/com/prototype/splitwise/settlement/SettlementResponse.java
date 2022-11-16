package com.prototype.splitwise.settlement;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
public class SettlementResponse {

    private final double totalDue;
    private final List<Settlement> pay;
    private final List<Settlement> receive;

    public SettlementResponse(List<Settlement> pay, List<Settlement> receive) {
        this.pay = pay;
        this.receive = receive;
        this.totalDue = getPendingAmount(pay) - getPendingAmount(receive);
    }


    public static SettlementResponse of(List<Settlement> debts, List<Settlement> credits) {
        return new SettlementResponse(debts, credits);
    }

    private double getPendingAmount(List<Settlement> settlements) {
        return CollectionUtils.emptyIfNull(settlements).stream()
                .map(Settlement::getData)
                .map(Settlement.SettlementData::getPendingAmount)
                .reduce(0D, Double::sum);
    }
}
