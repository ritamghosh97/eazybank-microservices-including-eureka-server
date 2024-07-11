package com.ritam.accounts.service.impl;

import com.ritam.accounts.dto.AccountsDto;
import com.ritam.accounts.dto.CardsDto;
import com.ritam.accounts.dto.CustomerDetailsDto;
import com.ritam.accounts.dto.LoansDto;
import com.ritam.accounts.entity.Accounts;
import com.ritam.accounts.entity.Customer;
import com.ritam.accounts.exceptions.ResourceNotFoundException;
import com.ritam.accounts.mapper.AccountsMapper;
import com.ritam.accounts.mapper.CustomerMapper;
import com.ritam.accounts.repository.AccountsRepository;
import com.ritam.accounts.repository.CustomerRepository;
import com.ritam.accounts.service.ICustomerService;
import com.ritam.accounts.service.client.CardsFeignClient;
import com.ritam.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountsRepository accountsRepository;

    private CustomerRepository customerRepository;

    private CardsFeignClient cardsFeignClient;

    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {

        Customer customer = customerRepository
                .findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Accounts accounts = accountsRepository
                .findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Accounts", "customerId", customer.getCustomerId().toString()));

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);

        if(null != loansDtoResponseEntity) {
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);

        if(null != cardsDtoResponseEntity) {
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }

        return customerDetailsDto;
    }
}
