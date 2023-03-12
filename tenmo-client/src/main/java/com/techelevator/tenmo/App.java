package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final TransferService transferService = new TransferService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            String token = currentUser.getToken();
            transferService.setAuthToken(token);
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        BigDecimal balance = transferService.getBalance();
        consoleService.printBalance(balance);
    }

    private void viewTransferHistory() {
        Transfer[] transferHistory = transferService.getTransferHistory();
        consoleService.printTransferHistory(transferHistory);
        int transferId = consoleService.selectTransfer(transferHistory);
        if (transferId != -1) {
            Transfer transfer = transferService.getSpecificTransfer(transferId);
            consoleService.printSpecificTransfer(transfer);
        }
    }

    private void viewPendingRequests() {
        Transfer[] pendingRequests = transferService.getPendingTransfers();
        consoleService.printPendingTransactions(pendingRequests);
        int transferId = consoleService.selectTransfer(pendingRequests);
        if (transferId != -1) {
            acceptOrReject(transferId);
        }

    }

    private void acceptOrReject(int transferId) {
        Transfer transfer = transferService.getSpecificTransfer(transferId);
        consoleService.printSpecificTransfer(transfer);
        int choice = consoleService.approveOrReject();
        if (choice == 1) {
            boolean transactionOccurs = transferService.approvedTransaction(transferId);
            if (!transactionOccurs) {
                consoleService.printErrorMessage();
            } else {
                consoleService.printSuccess();
            }
        } else if (choice == 2) {
            boolean transactionRejected = transferService.rejectedTransaction(transferId);
            if (!transactionRejected) {
                consoleService.printErrorMessage();
            }
        }
    }

    private void sendBucks() {
        User[] users = transferService.getAllUsers();
        String nameSelected = consoleService.printAndSelectUser(users);
        if (nameSelected == null) {
            return;
        }
        BigDecimal amountEntered = consoleService.enterAmount();
        if (amountEntered == null) {
            return;
        }
        Transfer sendTransfer = new Transfer(nameSelected, amountEntered);
        boolean sendSuccess = transferService.sendMoney(sendTransfer);
        if (sendSuccess) {
            consoleService.sendSuccessful();
        } else {
            consoleService.sendUnsuccessful();
        }
    }

    private void requestBucks() {
        User[] users = transferService.getAllUsers();
        String nameSelected = consoleService.printAndSelectUser(users);
        if (nameSelected == null) {
            return;
        }
        BigDecimal amountEntered = consoleService.enterAmount();
        if (amountEntered == null) {
            return;
        }
        Transfer requestTransfer = new Transfer(nameSelected, amountEntered);
        boolean requested = transferService.requestMoney(requestTransfer);
        consoleService.requestSuccessOrFail(requested);
    }

}
