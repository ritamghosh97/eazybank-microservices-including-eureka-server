package com.ritam.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(
        name = "Accounts",
        description = "A schema to hold Account information"
)
public class AccountsDto {

    @Schema(
            description = "Account number of Ritam Ghosh's account",
            example = "8368217281"
    )
    @NotEmpty(message = "Account number cannot be null or empty!")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be of 10 digits!")
    private Long accountNumber;

    @Schema(
            description = "The type of bank account",
            example = "Savings"
    )
    @NotBlank(message = "Account Type cannot be null of empty")
    private String accountType;

    @Schema(
            description = "The address of the branch of the bank",
            example = "Street Number 167, Newtown"
    )
    @NotBlank(message = "Branch Address cannot be null of empty")
    private String branchAddress;
}
