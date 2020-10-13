package main.java.ZhenyaShvyrkov.javacore.jdbc.controller;

import java.util.List;

public abstract class Controller<T> {
    public String[] handleRequest(String account){
        account = account.trim();
        String[] accountData = account.split(", |,");
        for(String element: accountData){
            element = element.trim();
        }
        return accountData;
    }
    public abstract T create(T t);
    public abstract List<T> read();
    public abstract T readById(Long id);
    public abstract T update(Long id, T t);
    public abstract void delete(T t);
    public abstract void deleteByID(Long id);
}
