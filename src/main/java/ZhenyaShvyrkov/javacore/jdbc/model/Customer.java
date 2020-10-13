package main.java.ZhenyaShvyrkov.javacore.jdbc.model;

import java.util.Set;

public class Customer{
    private Account account;
    private Set<Specialty> specialties;
    private long id;

    public Customer(Account account, Set<Specialty> specialties) {
        this.account = account;
        this.specialties = specialties;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Set<Specialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder idInfo = new StringBuilder();
        int counter = 1;
        for(Specialty x : this.getSpecialties()){
            if(counter == getSpecialties().size()) {
                idInfo.append(x.getId());
            } else {
                idInfo.append(x.getId()).append(",");
                counter++;
                }
            }
        return account.toString() + ", [" + idInfo + "]";
    }
}
