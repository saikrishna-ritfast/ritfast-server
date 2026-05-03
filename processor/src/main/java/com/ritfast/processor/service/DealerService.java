package com.ritfast.processor.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ritfast.common.dto.PageResponse;
import com.ritfast.common.enums.StatusCode;
import com.ritfast.common.enums.Tags;
import com.ritfast.processor.dto.dealer.dealerRequest;
import com.ritfast.processor.dto.dealer.dealerResponse;
import com.ritfast.processor.entity.Client;
import com.ritfast.processor.entity.Dealer;
import com.ritfast.processor.repository.DealerRepository;
import com.ritfast.processor.repository.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealerService {
    private final DealerRepository dealerRepository;
    private final LoggedInService loggedInService;

    @Transactional
    public dealerResponse createDealer(dealerRequest request) {

        Dealer dealer = new Dealer();
        Client client = loggedInService.getAuthenticatedUser().getClient();

        if (request.getName().isBlank() || request.getCompanyName().isBlank() || request.getPhoneNumber().isBlank()
                || request.getAlternativeNumber().isBlank() || request.getLocation().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required.");
        }

        dealer.setName(request.getName());
        dealer.setCompanyName(request.getCompanyName());
        dealer.setClient(client);
        dealer.setPhoneNumber(request.getPhoneNumber());
        dealer.setAlternativeNumber(request.getAlternativeNumber());
        dealer.setLocation(request.getLocation());
        dealer.setStatusCode(StatusCode.ACTIVE);
        dealer.setTag(Tags.COLD);
        dealer.setCreateBy(loggedInService.getAuthenticatedUser().getId());
        dealer.setUpdateBy(loggedInService.getAuthenticatedUser().getId());
        dealer.setCreateAt(java.time.LocalDateTime.now().toString());
        dealer.setUpdateAt(java.time.LocalDateTime.now().toString());

        dealer = dealerRepository.save(dealer);

        dealerResponse response = dealerResponse.builder()
                .id(dealer.getId())
                .clientId(dealer.getClient().getId())
                .name(dealer.getName())
                .companyName(dealer.getCompanyName())
                .phoneNumber(dealer.getPhoneNumber())
                .alternativeNumber(dealer.getAlternativeNumber())
                .tag(dealer.getTag())
                .location(dealer.getLocation())
                .statusCode(StatusCode.ACTIVE.name())
                .build();

        response.setCreateAt(dealer.getCreateAt());
        response.setCreateBy(dealer.getCreateBy());
        response.setUpdateAt(dealer.getUpdateAt());
        response.setUpdateBy(dealer.getUpdateBy());

        return response;
    }

    public PageResponse<dealerResponse> getAllDealers(Pageable pageable, String search) {
        Long clientId = loggedInService.getAuthenticatedUser().getClient().getId();
        Specification<Dealer> spec = BaseSpecification.filterByClientAndSearch(
                clientId, search, List.of("name", "companyName", "phoneNumber", "alternativeNumber", "location"));
        Page<Dealer> dealerPage = dealerRepository.findAll(spec, pageable);

        List<dealerResponse> dealerResponses = dealerPage.getContent().stream().map(dealer -> {
            dealerResponse response = dealerResponse.builder()
                    .id(dealer.getId())
                    .clientId(dealer.getClient().getId())
                    .name(dealer.getName())
                    .companyName(dealer.getCompanyName())
                    .phoneNumber(dealer.getPhoneNumber())
                    .alternativeNumber(dealer.getAlternativeNumber())
                    .tag(dealer.getTag())
                    .location(dealer.getLocation())
                    .statusCode(StatusCode.ACTIVE.name())
                    .build();

            response.setCreateAt(dealer.getCreateAt());
            response.setCreateBy(dealer.getCreateBy());
            response.setUpdateAt(dealer.getUpdateAt());
            response.setUpdateBy(dealer.getUpdateBy());

            return response;
        }).collect(Collectors.toList());

        return PageResponse.<dealerResponse>builder()
                .content(dealerResponses)
                .pageNumber(dealerPage.getNumber())
                .pageSize(dealerPage.getSize())
                .totalElements(dealerPage.getTotalElements())
                .totalPages(dealerPage.getTotalPages())
                .build();

    }
}
