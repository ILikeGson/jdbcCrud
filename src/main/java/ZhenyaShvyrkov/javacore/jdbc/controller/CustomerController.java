package main.java.ZhenyaShvyrkov.javacore.jdbc.controller;

import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Customer;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.CustomerRepository;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.jdbc.JdbcCustomerRepositoryImpl;

import java.util.List;

public class CustomerController extends Controller<Customer>{
    private static CustomerController customerController;
    private static CustomerRepository customerRepository;

    private CustomerController() {}

    public static synchronized CustomerController getCustomerController() {
        if (customerController == null) {
            customerController = new CustomerController();
            customerRepository = JdbcCustomerRepositoryImpl.getJdbcCustomerRepository();
        }
        return customerController;
    }

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> read() {
        return customerRepository.read();
    }

    @Override
    public Customer readById(Long id) {
        return customerRepository.readById(id);
    }

    @Override
    public Customer update(Long id, Customer customer) {
        return customerRepository.update(customer, id);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }

    @Override
    public void deleteByID(Long id) {
        customerRepository.deleteByID(id);
    }
}
