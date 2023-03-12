package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    public boolean sendMoney(int userIdSender, int userIdReceiver, BigDecimal amount);
    public void requestMoney(int userIdRequest, int userIdRequested, BigDecimal amount);
    public boolean approveTransaction(int transferId, int userId);
    public void rejectTransaction(int transferId, int userId);
    public List<User>listOfUsers (int userId);
    public int getUserId(String userName);



}
