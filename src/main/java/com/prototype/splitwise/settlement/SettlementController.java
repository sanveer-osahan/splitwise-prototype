package com.prototype.splitwise.settlement;

import com.prototype.splitwise.config.AuthContext;
import com.prototype.splitwise.entity.PaginationRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping(path = Settlement.SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping
    public ResponseEntity<Page<Settlement>> fetchPaginated(
            @RequestHeader(value = AuthContext.USER_ID, required = false) String currentUser,
            @RequestParam(value = "fromTime", required = false) Instant fromTime,
            @RequestParam(value = "toTime", required = false) Instant toTime,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "sortOrder", required = false) String sortOrder) {
        var paginationRequest = PaginationRequest.builder()
                .fromTime(fromTime)
                .toTime(toTime)
                .sortOrder(sortOrder)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .build();
        var paginatedResponse = settlementService.getSettlements(paginationRequest);
        return ResponseEntity.ok(paginatedResponse);
    }

}
