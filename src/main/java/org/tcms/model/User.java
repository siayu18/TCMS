package org.tcms.model;

public abstract class User {
    private final String accountId;
    private final String name;
    private final String password;
    private final String role;

    protected User(String accountId, String name, String password, String role) {
        this.accountId = accountId;
        this.name = name;
        this.password  = password;
        this.role = role;
    }

    public String getAccountId() { return accountId; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public abstract String getDashboardFxml();
    public abstract String getDashboardTitle();
}
