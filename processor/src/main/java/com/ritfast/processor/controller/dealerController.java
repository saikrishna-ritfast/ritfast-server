package com.ritfast.processor.controller;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import com.ritfast.common.dto.PageResponse;
import com.ritfast.processor.dto.dealer.dealerRequest;
import com.ritfast.processor.dto.dealer.dealerResponse;
import com.ritfast.processor.service.DealerService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/dealer")
@RestController
@RequiredArgsConstructor
public class dealerController {

    private final DealerService dealerService;

    @PostMapping
    public ResponseEntity<dealerResponse> createDealer(@RequestBody dealerRequest request) {
        dealerResponse response = dealerService.createDealer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<dealerResponse>> getAllDealers(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 15) Pageable pageable) {
        PageResponse<dealerResponse> response = dealerService.getAllDealers(pageable, search);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}