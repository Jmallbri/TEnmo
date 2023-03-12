package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.annotation.DirtiesContext;


import java.math.BigDecimal;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcTransferDaoTest extends BaseDaoTests {
    protected static final Transfer TRANSACTION_5 = new Transfer(3005, "Request", "Pending",
            "user1", "user2", new BigDecimal("100.00"));
    protected static final User USER_1 = new User(1001, "user1", null, null);
    protected static final User USER_2 = new User(1002, "user2", null, null);

    private JdbcTransferDao sut;
    private JdbcAccountDao accountDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
        accountDao = new JdbcAccountDao(jdbcTemplate);
    }


    @Test
    public void sendMoneyReturnsTrue() {
        Assert.assertTrue(sut.sendMoney(1001, 1002, new BigDecimal(500)));

        BigDecimal expected = new BigDecimal("500.00");
        BigDecimal actual = accountDao.showBalance(1001);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void sendMoneyReturnsFalseTooMuchMoneySent() {
        Assert.assertFalse(sut.sendMoney(1001, 1002, new BigDecimal(2000)));
        BigDecimal expected = new BigDecimal("1000.00");
        BigDecimal actual = accountDao.showBalance(1001);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void requestMoneyMakesNewPendingTransfer() {
        sut.requestMoney(1001, 1002, new BigDecimal("100.00"));
        String sql = "SELECT account_from, account_to, amount FROM transfer WHERE transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, 3006);
        result.next();
        int accountTo = result.getInt("account_to");
        int accountFrom = result.getInt("account_from");
        BigDecimal amount = result.getBigDecimal("amount");

        Assert.assertTrue(accountTo == 2001 && accountFrom == 2002 &&
                amount.compareTo(new BigDecimal("100.00")) == 0);
    }

    @Test
    public void approveTransactionWorksCorrectlyWhenMoneyLessThanBalance() {
        Assert.assertTrue(sut.approveTransaction(3003, 1002));
        BigDecimal expected = new BigDecimal("930.00");
        BigDecimal actual = accountDao.showBalance(1002);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void approveTransactionDoesntOccurWhenAmountGreaterOrEqualToBalance() {
        Assert.assertFalse(sut.approveTransaction(3005, 1001));
        BigDecimal expected = new BigDecimal("1000.00");
        BigDecimal actual = accountDao.showBalance(1001);
        Assert.assertEquals(expected, actual);
        String sql = "SELECT transfer_status_id FROM transfer WHERE transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, 3005);
        result.next();
        Assert.assertTrue(result.getInt("transfer_status_id") == 3);
    }

    @Test
    public void rejectRequestWorksCorrectly() {
        sut.rejectTransaction(3003, 1002);
        String sql = "SELECT transfer_status_id FROM transfer WHERE transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, 3003);
        result.next();
        Assert.assertTrue(result.getInt("transfer_status_id") == 3);
    }

    @Test
    public void listOfUsersReturnsEveryoneExceptSelf() {
        List<User> users = sut.listOfUsers(1003);
        Assert.assertEquals(2, users.size());
        assertUsersEqual(USER_1, users.get(0));
        assertUsersEqual(USER_2, users.get(1));
    }

    @Test
    public void getUserIdReturnsUserIdFromUserName() {
        int expected = 1001;
        int actual = sut.getUserId("user1");
        Assert.assertEquals(expected, actual);
    }

    private static void assertUsersEqual(User expected, User actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
    }


}
