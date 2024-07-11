package com.ritam.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(
        name = "Customer",
        description = "A schema to hold Customer and Account information"
)
public class CustomerDto {

    @Schema(
            description = "Name of the customer",
            example = "Ritam Ghosh"
    )
    @NotBlank(message = "Name cannot be null or empty!")
    @Size(min=5, max=25, message = "The length of the name should be between 5 and 30")
    private String name;

    @Schema(
            description = "Email Address of the customer",
            example = "ritamghosh97@gmail.com"
    )
    @NotBlank(message = "Email address cannot be null or empty!")
    @Email(message = "Invalid email!")
    private String email;

    @Schema(
            description = "Email Address of the customer",
            example = "9933725498"
    )
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be of 10 digits!")
    @NotBlank(message = "Mobile number cannot be null or empty!")
    private String mobileNumber;

    @Schema(
            description = "Account Details of the customer"
    )
    private AccountsDto accountsDto;
}
