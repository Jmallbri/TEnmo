package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    public BigDecimal showBalance(int userId);
    public List<Transfer> showTransactionHistory(int userId);
    public Transfer showSpecificTransaction(int transferId, int userId);
    public List<Transfer> showPendingTransactions(int userId);
    int getUserId(String userName);
}
