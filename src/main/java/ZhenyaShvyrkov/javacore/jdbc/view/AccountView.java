package main.java.ZhenyaShvyrkov.javacore.jdbc.view;

import main.java.ZhenyaShvyrkov.javacore.jdbc.controller.AccountController;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Account;

import java.util.Scanner;

public class AccountView extends View<Account>{
    private static AccountView accountView;

    private AccountView() {
    }

    public static synchronized AccountView getAccountView(){
        if (accountView == null){
            accountView = new AccountView();
            accountView.setController(AccountController.getAccountController());
        }
        return accountView;
    }

    public void getRequest(){
        Scanner scanner = new Scanner(System.in);
        String[] accountData;
        String accountInfo;
        System.out.print("Create Read Update Delete:\n--------------------------\n" +
                "* to create a new account: -c, firstName, lastName, age, accountStatus\n" +
                "* to read all accounts: -r\n" +
                "* to read the account by id: -r, id\n" +
                "* to update the account: -u, id, firstName, lastName, age, accountStatus\n" +
                "* to delete the account: -d, firstName, lastName, age, accountStatus\n" +
                "* to delete account by id: -d, id\n->: ");
        while (((accountInfo = scanner.nextLine()) != null) && accountInfo.length() != 0) {
            accountData = controller.handleRequest(accountInfo);
            separateData(accountData);
            System.out.print("->: ");
        }
        scanner.close();
    }

    public void separateData(String[] accountData) {
        String command = accountData[0];
        if (command.equalsIgnoreCase("-c")) {
            String firstName = accountData[1];
            String lastName = accountData[2];
            int age = Integer.parseInt(accountData[3]);
            Account.AccountStatus status = findOutStatus(accountData[4]);
            response(controller.create(new Account(firstName, lastName, age, status)));
        } else if (command.equalsIgnoreCase("-r")){
            if(accountData.length > 1){
                response(controller.readById(Long.valueOf(accountData[1])));
            } else {
                response(controller.read());
            }
        } else if (command.equalsIgnoreCase("-u")){
            Long id = Long.valueOf(accountData[1]);
            String firstName = accountData[2];
            String lastName = accountData[3];
            int age = Integer.parseInt(accountData[4]);
            Account.AccountStatus status = findOutStatus(accountData[5]);
            response(controller.update(id, new Account(firstName, lastName, age, status)));
        } else if (command.equalsIgnoreCase("-d")){
            if(accountData.length > 2){
                String fisrtName = accountData[1];
                String lastName = accountData[2];
                int age = Integer.parseInt(accountData[3]);
                Account.AccountStatus status = findOutStatus(accountData[4]);
                controller.delete(new Account(fisrtName, lastName, age, status));
            } else  {
                Long id = Long.valueOf(accountData[1]);
                controller.deleteByID(id);
            }
            response("Account succesfully deleted");
        }
    }
}
