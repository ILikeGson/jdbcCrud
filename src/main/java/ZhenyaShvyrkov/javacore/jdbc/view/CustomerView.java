package main.java.ZhenyaShvyrkov.javacore.jdbc.view;

import main.java.ZhenyaShvyrkov.javacore.jdbc.controller.CustomerController;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Account;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.AccountBuilder;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Customer;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Specialty;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class CustomerView extends View<Customer> {
    private static CustomerView customerView;
    private static CustomerController customerController;

    private CustomerView() {}

    public static synchronized CustomerView getCustomerView() {
        if (customerView == null) {
            customerView = new CustomerView();
            customerController = CustomerController.getCustomerController();
        }
        return customerView;
    }
    @Override
    public void getRequest() {
        Scanner scanner = new Scanner(System.in);
        String[] customerData;
        String accountInfo;
        System.out.print("Create Read Update Delete:\n--------------------------\n" +
                "* to create a new account: -c, firstName, lastName, age, accountStatus, specialty1, specialty2...\n" +
                "* to read all accounts: -r\n" +
                "* to read the account by id: -r, id\n" +
                "* to update the account: -u, id, firstName, lastName, age, accountStatus, specialty1, specialty2...\n" +
                "* to delete the account: -d, firstName, lastName, age, accountStatus, specialty1, specialty2...\n" +
                "* to delete account by id: -d, id\n->: ");
        while (((accountInfo = scanner.nextLine()) != null) && accountInfo.length() != 0) {
            customerData = customerController.handleRequest(accountInfo);
            separateData(customerData);
            System.out.print("->: ");
        }
        scanner.close();
    }

    @Override
    public void separateData(String[] customerData) {
        String command = customerData[0];
        Set<Specialty> specialties = new HashSet<>();
        if (command.equals("-c")) {
            response(customerController.create(toCustomer(customerData, specialties)));
        } else if (command.startsWith("-r")) {
            if (customerData.length > 1) {
                long id = Long.parseLong(customerData[1]);
                response(customerController.readById(id));
            } else {
                response(customerController.read());
            }
        } else if (command.equals("-u")) {
            long id = Long.parseLong(customerData[1]);
            String firstName = customerData[2];
            String lastName = customerData[3];
            int age = Integer.parseInt(customerData[4]);
            Account.AccountStatus status = findOutStatus(customerData[5]);
            for (int i = 6; i < customerData.length; i++) {
                specialties.add(new Specialty(customerData[i]));
            }
            Account account = new AccountBuilder()
                    .buildAccount()
                    .buildFirstName(firstName)
                    .buildLastName(lastName)
                    .buildAge(age)
                    .buildStatus(status)
                    .build();
            response(customerController.update(id, new Customer(account, specialties)));
        } else if (command.equals("-d")) {
            if (customerData.length > 2) {
                customerController.delete(toCustomer(customerData, specialties));
            } else {
                customerController.deleteByID(Long.parseLong(customerData[1]));
            }
            response("Successfully deleted");
        }
    }

    private Customer toCustomer(String[] customerData, Set<Specialty> specialties) {
        String firstName = customerData[1];
        String lastName = customerData[2];
        int age = Integer.parseInt(customerData[3]);
        Account.AccountStatus status = findOutStatus(customerData[4]);
        for (int i = 5; i < customerData.length; i++) {
            specialties.add(new Specialty(customerData[i]));
        }
        Account account = new AccountBuilder()
                .buildAccount()
                .buildFirstName(firstName)
                .buildLastName(lastName)
                .buildAge(age)
                .buildStatus(status)
                .build();
        return new Customer(account, specialties);
    }
}
