package main.java.ZhenyaShvyrkov.javacore.jdbc;

import main.java.ZhenyaShvyrkov.javacore.jdbc.view.AccountView;
import main.java.ZhenyaShvyrkov.javacore.jdbc.view.CustomerView;
import main.java.ZhenyaShvyrkov.javacore.jdbc.view.SpecialtyView;

public class CRUD {
    private static AccountView accountView = AccountView.getAccountView();
    private static CustomerView customerView = CustomerView.getCustomerView();
    private static SpecialtyView specialtyView = SpecialtyView.getSpecialtyView();

    public void workWithAccount(){
        accountView.getRequest();
    }
    public void workWithCustomer(){
        customerView.getRequest();
    }
    public void workWithSpecialty(){
        specialtyView.getRequest();
    }
}
