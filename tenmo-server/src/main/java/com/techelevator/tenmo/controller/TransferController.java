package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("")
public class TransferController {

    TransferDao dao;

    TransferController(TransferDao dao) {
        this.dao = dao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public boolean sendingMoney(@RequestBody Transfer transfer, Principal principal) {
        int currentUserId = dao.getUserId(principal.getName());
        boolean moneySent = dao.sendMoney(currentUserId, dao.getUserId(transfer.getUserTo()), transfer.getAmount());
        if (moneySent == false) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount greater than amount in account or less than 0");
        }
        return moneySent;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public boolean requestingMoney(@RequestBody Transfer transfer, Principal principal) {
        int currentUserId = dao.getUserId(principal.getName());
        BigDecimal amount = transfer.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            dao.requestMoney(currentUserId, dao.getUserId(transfer.getUserTo()), amount);
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested amount must be greater than 0");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transaction/history/{id}", method = RequestMethod.PUT)
    public boolean approveTransaction(@PathVariable int id, Principal principal) {
        int currentUserId = dao.getUserId(principal.getName());
        boolean transactionOccurs = false;
        try {
            transactionOccurs = dao.approveTransaction(id, currentUserId);
        } catch (IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized");
        }
        if (transactionOccurs) {
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer amount greater than amount in account");
        }
    }

    @RequestMapping(path = "/transaction/history/{id}", params = "reject", method = RequestMethod.PUT)
    public void rejectTransaction(@PathVariable int id, @RequestParam String reject, Principal principal) {
        int currentUserId = dao.getUserId(principal.getName());
        dao.rejectTransaction(id, currentUserId);
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> listOfUsers(Principal principal) {
        int currentUserId = dao.getUserId(principal.getName());
        List<User> userList = dao.listOfUsers(currentUserId);

        return userList;
    }


}
