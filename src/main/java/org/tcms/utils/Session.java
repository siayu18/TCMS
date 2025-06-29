package org.tcms.utils;

import org.tcms.model.User;

import lombok.Getter;
import lombok.Setter;

public class Session {
    @Getter
    @Setter
    private static User currentUser;

    public static String getCurrentUserId() {
        return (currentUser == null) ? null : currentUser.getAccountId();
    }

    public static String getCurrentUserName() {
        return (currentUser == null) ? null : currentUser.getUsername();
    }

    public static String getCurrentUserPassword() {
        return (currentUser == null) ? null : currentUser.getPassword();
    }

    public static String getCurrentUserRole() {
        return (currentUser == null) ? null : currentUser.getRole();
    }
}