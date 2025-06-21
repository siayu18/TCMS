package org.tcms.service;

import org.tcms.model.Student;

public class AdminService {
    // Make sure password is strong
    public boolean isValidPassword(String password){
        return password != null && password.length() >= 8
                && password.matches(".*[A-Z.*]")
                && password.matches(".*\\d.*")
                && password.matches(".*[!@#$%^&*].*");
    }
}
