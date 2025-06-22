package org.tcms.navigation;

public enum View {
    TOOLBAR("/org/tcms/view/ToolbarView.fxml"),
    LOGIN("/org/tcms/view/LoginView.fxml"),
    ADMIN_DASHBOARD("/org/tcms/view/AdminDashboardView.fxml"),
    TUTOR_DASHBOARD("/org/tcms/view/TutorDashboardView.fxml"),
    STUDENT_DASHBOARD("/org/tcms/view/StudentDashboardView.fxml"),
    RECEP_DASHBOARD("/org/tcms/view/ReceptionistDashboardView.fxml"),
    ADMIN_SIDE_MENU("/org/tcms/view/AdminSideMenu.fxml"),
    TUTOR_SIDE_MENU("/org/tcms/view/TutorSideMenu.fxml"),
    STUDENT_SIDE_MENU("/org/tcms/view/StudentSideMenu.fxml"),
    RECEP_SIDE_MENU("/org/tcms/view/ReceptionistSideMenu.fxml");

    private final String fxmlPath;
    View(String fxmlPath) { this.fxmlPath = fxmlPath; }
    public String getPath() { return fxmlPath; }
}
