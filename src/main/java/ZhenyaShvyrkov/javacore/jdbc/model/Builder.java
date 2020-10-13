package main.java.ZhenyaShvyrkov.javacore.jdbc.model;

public interface Builder {
    Builder buildAccount();
    Builder buildFirstName(String firstName);
    Builder buildLastName(String lastName);
    Builder buildAge(int age);
    Builder buildStatus(Account.AccountStatus status);
}
