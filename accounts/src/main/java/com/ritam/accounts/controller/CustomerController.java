package com.ritam.accounts.controller;

import com.ritam.accounts.dto.CustomerDetailsDto;
import com.ritam.accounts.dto.ErrorResponseDto;
import com.ritam.accounts.service.ICustomerService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
@Tag(
        name = "Rest Api for Customer in Eazybank",
        description = "Rest Api in Eazybank to fetch customer details",
        externalDocs = @ExternalDocumentation(
                description = "Learn more about Customer Api",
                url = "https://www.google.com"
        )
)
public class CustomerController {

    private static Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private ICustomerService iCustomerService;

    public CustomerController(ICustomerService iCustomerService) {
        this.iCustomerService = iCustomerService;
    }

    @Operation(
            summary = "Fetch Customer Details Rest Api",
            description = "A Rest Api to fetch Customer details based on mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/customers")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(
            @RequestHeader("eazybank-correlation-id") String correlationId,
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})",
                    message = "Mobile number must be of 10 digits!")
            String mobileNumber) {

        logger.debug("fetchCustomerDetails() method is invoked");

        CustomerDetailsDto customerDetailsDto = iCustomerService
                .fetchCustomerDetails(mobileNumber, correlationId);

        logger.debug("fetchCustomerDetails() method is ended");

        return ResponseEntity
                .status(HttpStatus.SC_OK)
                .body(customerDetailsDto);
    }
}
