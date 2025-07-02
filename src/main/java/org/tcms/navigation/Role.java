package org.tcms.navigation;

public enum Role {
    ADMIN (View.ADMIN_DASHBOARD, View.ADMIN_SIDE_MENU),
    TUTOR (View.TUTOR_DASHBOARD, View.TUTOR_SIDE_MENU),
    STUDENT (View.STUDENT_DASHBOARD, View.STUDENT_SIDE_MENU),
    RECEPTIONIST(View.RECEP_DASHBOARD, View.RECEP_SIDE_MENU);

    private final View dashboard, sideMenu;

    Role(View dashboard, View sideMenu) {
        this.dashboard = dashboard;
        this.sideMenu  = sideMenu;
    }

    public View getDashboard() { return dashboard; }
    public View getSideMenu()  { return sideMenu;  }

    public static Role fromString(String s) {
        return Role.valueOf(s.trim().toUpperCase());
    }
}
