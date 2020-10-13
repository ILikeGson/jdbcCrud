package main.java.ZhenyaShvyrkov.javacore.jdbc.model;

public class Account{
    private String firstName;
    private String lastName;
    private int age;
    private AccountStatus status;
    private long id;

    public Account() {
    }

    public Account(String firstName, String lastName, int age, AccountStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.status = status;
    }

    public enum AccountStatus{
        ACTIVE, BANNED, DELETED
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return firstName + ", " + lastName + ", " + age + ", " + status;
    }
}
