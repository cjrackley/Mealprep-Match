package com.CSC340.MealPrep_Match.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Customer;
import com.CSC340.MealPrep_Match.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + id));
    }

    public Customer authenticate(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(password, customer.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return customer;
    }

    public Customer create(Customer customer) {
        customer.setPasswordHash(passwordEncoder.encode(customer.getPassword()));
        customer.setPassword(null);
        return customerRepository.save(customer);
    }

    public Customer update(Long id, Customer updates) {
        Customer customer = getById(id);
        if (updates.getName() != null) {
            customer.setName(updates.getName());
        }
        if (updates.getEmail() != null) {
            customer.setEmail(updates.getEmail());
        }
        if (updates.getPassword() != null) {
            customer.setPasswordHash(passwordEncoder.encode(updates.getPassword()));
        }
        if (updates.getDietaryPreferences() != null) {
            customer.setDietaryPreferences(updates.getDietaryPreferences());
        }
        return customerRepository.save(customer);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + id);
        }
        customerRepository.deleteById(id);
    }
}
