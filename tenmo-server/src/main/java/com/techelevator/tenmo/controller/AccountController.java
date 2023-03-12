package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("")
public class AccountController {
    AccountDao dao;

    AccountController(AccountDao dao){
        this.dao = dao;
    }

   @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal){
        int currentUserId = dao.getUserId(principal.getName());
        BigDecimal balance = dao.showBalance(currentUserId);
        return balance;
   }

   @RequestMapping(path = "/transaction/history", method = RequestMethod.GET)
    public List<Transfer> getTransactionHistory(Principal principal){
        int currentUserID = dao.getUserId(principal.getName());
        List<Transfer> transferList = dao.showTransactionHistory(currentUserID);
        return transferList;
   }

   @RequestMapping(path = "/transaction/history/{id}", method = RequestMethod.GET)
    public Transfer getSpecificTransaction(@PathVariable int id, Principal principal){
        int currentUserId = dao.getUserId(principal.getName());
        Transfer specificTransaction = dao.showSpecificTransaction(id, currentUserId);
        return specificTransaction;
   }

    @RequestMapping(path = "/transaction/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingTransactions(Principal principal){
        int currentUserID = dao.getUserId(principal.getName());
        List<Transfer> pendingList = dao.showPendingTransactions(currentUserID);
        return pendingList;
    }
}
