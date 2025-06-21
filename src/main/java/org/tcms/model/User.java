package org.tcms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class User {
    private final String accountId;
    private final String username;
    private final String password;
    private final String role;

    public abstract String getDashboardFxml();
    public abstract String getDashboardTitle();
}
