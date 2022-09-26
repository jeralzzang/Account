package com.example.account.controller;

import com.example.account.aop.AccountLock;
import com.example.account.dto.CancelBalance;
import com.example.account.dto.QueryTransactionResponse;
import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 1. 잔액사용
 * 2. 잔액 사용취소
 * 3. 거래 확인
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    @AccountLock
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) throws InterruptedException {
      try{
          return UseBalance.Response.from(
                  transactionService.useBalance(request.getUserId(),
                          request.getAccountNumber(),
                          request.getAmount()));
      }catch(AccountException e){
          log.error("Faild to use balance.");
          transactionService.saveFailedUseTransaction(
                  request.getAccountNumber(),
                  request.getAmount()
          );

          throw e;
      }
    }

    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request
    ){
        try{
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId(),
                            request.getAccountNumber(),
                            request.getAmount()));
        }catch(AccountException e){
            log.error("Faild to cancel balance.");
            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }
    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId
    ){
        return QueryTransactionResponse.from(
                transactionService.queryTransaction(transactionId));
    }


}
