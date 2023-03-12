package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal showBalance(int userId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        BigDecimal balance = null;
        if (results.next()) {
            balance = results.getBigDecimal("balance");
        }
        return balance;
    }

    @Override
    public List<Transfer> showTransactionHistory(int userId) {
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, account_to, account_from, amount " +
                "FROM transfer " +
                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "JOIN account as af ON transfer.account_from = af.account_id " +
                "Join account as at ON transfer.account_to = at.account_id " +
                "WHERE af.user_id = ? OR at.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        List<Transfer> history = new ArrayList<>();
        while (results.next()) {
            history.add(mapRowToTransfer(results, userId));
        }
        return history;
    }

    @Override
    public Transfer showSpecificTransaction(int transferId, int userId) {
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, account_to, account_from, amount " +
                "FROM transfer " +
                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "JOIN account as af ON transfer.account_from = af.account_id " +
                "Join account as at ON transfer.account_to = at.account_id " +
                "WHERE transfer_id = ? AND (at.user_id = ? OR af.user_id = ?); ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId, userId, userId);
        Transfer transfer = null;
        if (results.next()) {
            transfer = mapRowToTransfer(results, userId);
        }
        return transfer;
    }

    @Override
    public List<Transfer> showPendingTransactions(int userId) {
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, account_to, account_from, amount " +
                "FROM transfer " +
                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "JOIN account as af ON transfer.account_from = af.account_id " +
                "Join account as at ON transfer.account_to = at.account_id " +
                "WHERE (af.user_id = ? OR at.user_id = ?) AND transfer_status_desc = 'Pending' " +
                "ORDER BY transfer_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        List<Transfer> history = new ArrayList<>();
        while (results.next()) {
            history.add(mapRowToTransfer(results, userId));
        }
        return history;
    }

    private String getUserName(int accountId) {

        String sql = "SELECT username " +
                "From tenmo_user " +
                "Join account on tenmo_user.user_id = account.user_id " +
                "Where account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        String userName = null;
        if (results.next()) {
            userName = results.getString("username");
        }
        return userName;
    }

    @Override
    public int getUserId(String userName) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username = ?;";
        int userId = jdbcTemplate.queryForObject(sql, Integer.class, userName);
        return userId;
    }

    private boolean accountIdIsCurrentUser(int accountId, int userId) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
        int returnedAccountId = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return (returnedAccountId == accountId);
    }

    private Transfer mapRowToTransfer(SqlRowSet results, int userId) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setTransferType(results.getString("transfer_type_desc"));
        transfer.setTransferStatus(results.getString("transfer_status_desc"));
        if (accountIdIsCurrentUser(results.getInt("account_to"), userId)) {
            transfer.setUserTo("You");
        } else {
            transfer.setUserTo(getUserName(results.getInt("account_to")));
        }
        if (accountIdIsCurrentUser(results.getInt("account_from"), userId)) {
            transfer.setUserFrom("You");
        } else {
            transfer.setUserFrom(getUserName(results.getInt("account_from")));
        }
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }
}


