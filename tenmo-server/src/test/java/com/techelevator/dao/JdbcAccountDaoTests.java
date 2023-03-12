package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcAccountDaoTests extends BaseDaoTests {
    protected static final Transfer TRANSACTION_1 = new Transfer(3001, "Send", "Approved",
            "user1", "You", new BigDecimal("100.00"));
    protected static final Transfer TRANSACTION_2 = new Transfer(3002, "Send", "Approved",
            "You", "user3", new BigDecimal("50.00"));
    protected static final Transfer TRANSACTION_3_FROM_USER_2 = new Transfer(3003, "Request", "Pending",
            "You", "user3", new BigDecimal("70.00"));
    protected static final Transfer TRANSACTION_3_FROM_USER_3 = new Transfer(3003, "Request", "Pending",
            "user2", "You", new BigDecimal("70.00"));
    protected static final Transfer TRANSACTION_4 = new Transfer(3004, "Request", "Pending",
            "user1", "You", new BigDecimal("100.00"));
    protected static final Transfer TRANSACTION_5 = new Transfer(3005, "Request", "Pending",
            "user1", "You", new BigDecimal("10000.00"));

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void showBalanceReturnsCorrectAmount() {
        BigDecimal expected = new BigDecimal("1000.00");
        BigDecimal actual = sut.showBalance(1001);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void showTransactionHistoryReturnsCorrectListOfTransactions() {
        List<Transfer> actual = sut.showTransactionHistory(1002);

        Assert.assertEquals(3, actual.size());
        assertTransfersMatch(TRANSACTION_1, actual.get(0));
        assertTransfersMatch(TRANSACTION_2, actual.get(1));
        assertTransfersMatch(TRANSACTION_3_FROM_USER_2, actual.get(2));
    }

    @Test
    public void showPendingTransactionsReturnsPendingTransactions() {
        List<Transfer> actual = sut.showPendingTransactions(1003);

        Assert.assertEquals(3, actual.size());
        assertTransfersMatch(TRANSACTION_3_FROM_USER_3, actual.get(0));
        assertTransfersMatch(TRANSACTION_4, actual.get(1));
        assertTransfersMatch(TRANSACTION_5, actual.get(2));
    }

    @Test
    public void showSpecificTransactionReturnsSpecificTransaction() {
        Transfer actual = sut.showSpecificTransaction(3001, 1002);
        assertTransfersMatch(TRANSACTION_1, actual);

    }

    protected static void assertTransfersMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getTransferType(), actual.getTransferType());
        Assert.assertEquals(expected.getTransferStatus(), actual.getTransferStatus());
        Assert.assertEquals(expected.getUserFrom(), actual.getUserFrom());
        Assert.assertEquals(expected.getUserTo(), actual.getUserTo());
        Assert.assertEquals(expected.getAmount(), actual.getAmount());
    }

}
