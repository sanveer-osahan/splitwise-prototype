package com.prototype.splitwise.settlement;

import com.prototype.splitwise.entity.Entity;
import com.prototype.splitwise.entity.IDNameReference;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@CompoundIndexes({
        @CompoundIndex(name = "unique_settlement", def = "{ 'data.expense._id' : 1, 'data.settler._id' : 1 }", unique = true),
        @CompoundIndex(name = "expense_index", def = "{ 'data.expense._id' : 1, 'meta.audit.createdOn' : -1 }"),
        @CompoundIndex(name = "settler_owner_index", def = "{ 'data.settler._id' : 1, 'data.group._id': 1, 'data.owner._id' : 1, 'meta.audit.createdOn' : -1 }")
})
@Document(Settlement.SERVICE)
@Getter
@Setter
public class Settlement extends Entity<Settlement.SettlementData> {

    public static final String ENTITY_TYPE = "settlement";
    public static final String SERVICE = ENTITY_TYPE + "s";

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SettlementData {

        private IDNameReference group;
        private IDNameReference expense;
        private IDNameReference owner;
        private IDNameReference settler;
        private double totalAmount;
        private double pendingAmount;

        private SettlementData() {}
    }
}
