package main.java.ZhenyaShvyrkov.javacore.jdbc.view;

import main.java.ZhenyaShvyrkov.javacore.jdbc.controller.Controller;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Account;

import java.util.List;

public abstract class View<T> {
    protected Controller<T> controller;

    public void setController(Controller<T> controller){
        this.controller = controller;
    }

    public abstract void getRequest();

    public abstract void separateData(String[] accountData);

    public void response(T t){
        System.out.println(t);
    }
    public void response(List<T> list){
        list.forEach(account -> System.out.println(account));
    }

    public void response(String message){
        System.out.println(message);
    }

    protected Account.AccountStatus findOutStatus(String status){
        Account.AccountStatus accountStatus;
        if (status.equalsIgnoreCase("active")){
            accountStatus = Account.AccountStatus.ACTIVE;
        } else if (status.equalsIgnoreCase("banned")){
            accountStatus = Account.AccountStatus.BANNED;
        } else if (status.equalsIgnoreCase("deleted")){
            accountStatus = Account.AccountStatus.DELETED;
        } else throw new IllegalArgumentException();
        return accountStatus;
    }
}
