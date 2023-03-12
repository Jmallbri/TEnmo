package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean sendMoney(int userIdSender, int userIdReceiver, BigDecimal amount) {
        String sql = "Start Transaction; " +
                "UPDATE account SET balance = balance + ? " +
                "WHERE user_id = ?; " +
                "UPDATE account SET balance = balance - ? " +
                "WHERE user_id = ?; " +
                "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (2, 2, (SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?); " +
                "COMMIT;";
        try {
            jdbcTemplate.update(sql, amount, userIdReceiver, amount, userIdSender, userIdSender, userIdReceiver, amount);
        } catch (DataIntegrityViolationException e) {
            sql = "ROLLBACK;";
            jdbcTemplate.update(sql);
            return false;
        }
        return true;
    }


    @Override
    public void requestMoney(int userIdRequester, int userIdRequested, BigDecimal amount) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_to, " +
                "account_from, amount) VALUES (1, 1, (SELECT account_id FROM account WHERE user_id = ?), " +
                "(SELECT account_id FROM account WHERE user_id = ?), ?);";
        jdbcTemplate.update(sql, userIdRequester, userIdRequested, amount);
    }

    @Override
    public boolean approveTransaction(int transferId, int userId) {
        String sql = "SELECT account_from, account_to, amount FROM transfer " +
                "JOIN account ON transfer.account_from = account.account_id " +
                "WHERE transfer_id = ? AND user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId, userId);
        result.next();
        int accountFrom = result.getInt("account_from");
        int accountTo = result.getInt("account_to");
        BigDecimal amount = result.getBigDecimal("amount");

        sql = "START TRANSACTION; " +
                "UPDATE transfer SET transfer_status_id = 2 " +
                "WHERE transfer_id = ?; " +
                "UPDATE account SET balance = balance + ? " +
                "WHERE account_id = ?; " +
                "UPDATE account SET balance = balance - ? " +
                "WHERE account_id = ?; " +
                "COMMIT;";
        try {
            int update = jdbcTemplate.update(sql, transferId, amount, accountTo, amount, accountFrom);
        } catch (DataIntegrityViolationException e) {
            sql = "ROLLBACK; " +
                    "UPDATE transfer SET transfer_status_id = 3 WHERE transfer_id = ?";
            jdbcTemplate.update(sql, transferId);
            return false;
        }
        return true;
    }

    @Override
    public void rejectTransaction(int transferId, int userId) {
        String sql = "UPDATE transfer SET transfer_status_id = 3 " +
                "WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, transferId);
    }

    @Override
    public List<User> listOfUsers(int userId) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, username " +
                "FROM tenmo_user " +
                "where user_id != ?; ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        while (result.next()) {
            userList.add(mapRowToUser(result));
        }
        return userList;
    }

    @Override
    public int getUserId(String userName) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username = ?;";
        int userId = jdbcTemplate.queryForObject(sql, Integer.class, userName);
        return userId;
    }

    private User mapRowToUser(SqlRowSet results) {
        User user = new User();
        user.setId(results.getInt("user_id"));
        user.setUsername(results.getString("username"));
        return user;
    }


}
