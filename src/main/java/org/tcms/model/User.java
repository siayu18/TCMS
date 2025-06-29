package org.tcms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    private String accountId;
    private String username;
    private String password;
    private String role;

    public abstract String getDashboardFxml();
    public abstract String getDashboardTitle();
}
