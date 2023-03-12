package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printBalance(BigDecimal balance) {
        System.out.println("You have $" + balance + " in your account.");
    }

    public void printTransferHistory(Transfer[] transfers) {
        System.out.println("TRANSFER HISTORY");
        printHistory(transfers);
    }

    public void printPendingTransactions(Transfer[] transfers) {
        System.out.println("PENDING TRANSFERS");
        printHistory(transfers);
    }

    private void printHistory(Transfer[] transfers) {
        System.out.println("ID\t\tStatus\t\tFrom/To\t\tAmount\n");
        for (Transfer transfer : transfers) {
            System.out.print(transfer.getTransferId() + "\t");
            System.out.print(transfer.getTransferStatus() + "\t");
            if (!transfer.getUserFrom().equals("You")) {
                System.out.print("From: " + transfer.getUserFrom());
            } else if (!transfer.getUserTo().equals("You")) {
                System.out.print("To:   " + transfer.getUserTo());
            }
            System.out.print("     $" + transfer.getAmount() + "\n");
        }
    }

    public int selectTransfer(Transfer[] transfers) {
        int transferId = promptForInt("Please select a transfer ID.");
        for (Transfer transfer : transfers) {
            if (transferId == transfer.getTransferId()) {
                return transferId;
            }
        }
        System.out.println("Transfer ID not found.  Please enter correct Transfer ID.");
        return -1;
    }

    public void printSpecificTransfer(Transfer transfer) {
        System.out.println("Transfer Details\nId: " + transfer.getTransferId() + "\nFrom: " + transfer.getUserFrom() + "\nTo: " + transfer.getUserTo() + "\nType: " + transfer.getTransferType() + "\nStatus: " + transfer.getTransferStatus() + "\nAmount : $" + transfer.getAmount());
    }
    public void printSuccess(){
        System.out.println("The transfer was successful.");
    }

    public int approveOrReject() {
        int printApproveOrReject = promptForInt("1: Approve\n2: Reject \n0: Don't approve or reject\nPlease choose an option:\n");
        if (printApproveOrReject >= 0 && printApproveOrReject <= 2) {
            return printApproveOrReject;
        }
        System.out.println("Please enter an amount between 0 and 2.");
        return -1;
    }

    public String printAndSelectUser(User[] users) {
        System.out.println("Users\nID\tname");
        for (User user : users) {
            System.out.println(user.getId() + "\t" + user.getUsername());
        }
        int idSelected = promptForInt("Enter ID of user (0 to cancel):");
        for (User user : users) {
            if (user.getId() == idSelected) {
                return user.getUsername();
            }
        }
        System.out.println("This id is not in user list.");
        return null;
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public void sendSuccessful() {
        System.out.println("The money was successfully sent.");
    }

    public void sendUnsuccessful() {
        System.out.println("You do not have enough money in your account to send that amount.");
    }

    public void requestSuccessOrFail(boolean requested) {
        if (requested) {
            System.out.println("The request was successfully sent.");
        } else {
            System.out.println("Error occurred in sending request.  Please try again.");
        }
    }

    public BigDecimal enterAmount() {
        BigDecimal amount = promptForBigDecimal("Enter the amount:");
        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            return amount;
        }
        System.out.println("Amount must be greater than zero.");
        return null;
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
