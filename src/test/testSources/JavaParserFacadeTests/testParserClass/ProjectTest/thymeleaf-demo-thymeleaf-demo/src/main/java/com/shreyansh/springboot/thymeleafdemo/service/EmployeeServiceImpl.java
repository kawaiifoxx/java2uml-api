package com.shreyansh.springboot.thymeleafdemo.service;

import com.shreyansh.springboot.thymeleafdemo.dao.EmployeeRepository;
import com.shreyansh.springboot.thymeleafdemo.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    final EmployeeRepository employeeDAO;

    public EmployeeServiceImpl(EmployeeRepository employeeDAO) {
        this.employeeDAO = employeeDAO;
    }


    @Override
    public List<Employee> findAll() {
        return employeeDAO.findAllByOrderByLastNameAsc();
    }

    @Override
    public Employee findById(Integer id) {

        Employee employee = null;
        Optional<Employee> result = employeeDAO.findById(id);
        if (result.isPresent()) {
            employee = result.get();
        } else {
            throw new RuntimeException("Employee with specified id " + id + " not found.");
        }

        return employee;
    }

    @Override
    public void save(Employee employee) {
        employeeDAO.save(employee);
    }

    @Override
    public void deleteById(Integer id) {
        employeeDAO.deleteById(id);
    }
}
