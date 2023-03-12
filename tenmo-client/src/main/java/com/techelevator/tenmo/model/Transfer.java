package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int transferId;
    private String transferType;
    private String transferStatus;
    private String userFrom;
    private String userTo;
    private BigDecimal amount;

    public Transfer() {
    }

    public Transfer(String userTo, BigDecimal amount) {
        this.userTo = userTo;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }


    public String getTransferType() {
        return transferType;
    }


    public String getTransferStatus() {
        return transferStatus;
    }


    public String getUserFrom() {
        return userFrom;
    }


    public String getUserTo() {
        return userTo;
    }


    public BigDecimal getAmount() {
        return amount;
    }


}
