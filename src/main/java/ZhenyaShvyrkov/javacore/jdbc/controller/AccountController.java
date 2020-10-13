package main.java.ZhenyaShvyrkov.javacore.jdbc.controller;

import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Account;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.AccountRepository;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.jdbc.JdbcAccountRepositoryImpl;

import java.util.List;

public class AccountController extends Controller<Account> {
    private static AccountController accountController;
    private static AccountRepository repository;

    private AccountController(){
    }

    public static synchronized AccountController getAccountController(){
        if (accountController == null){
            accountController = new AccountController();
            repository = JdbcAccountRepositoryImpl.getRepository();
        }
        return accountController;
    }
    @Override
    public Account create(Account account) {
        return repository.save(account);
    }

    @Override
    public List<Account> read() {
        return repository.read();
    }

    @Override
    public Account readById(Long id) {
        return repository.readById(id);
    }

    @Override
    public Account update(Long id, Account account) {
        return repository.update(account, id);
    }

    @Override
    public void delete(Account account) {
        repository.delete(account);
    }

    @Override
    public void deleteByID(Long id) {
        repository.deleteByID(id);
    }
}
