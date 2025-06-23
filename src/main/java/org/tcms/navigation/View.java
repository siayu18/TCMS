package org.tcms.navigation;

public enum View {
    TOOLBAR("/org/tcms/view/SystemScenes/ToolbarView.fxml"),
    LOGIN("/org/tcms/view/SystemScenes/LoginView.fxml"),
    ADMIN_DASHBOARD("/org/tcms/view/AdminScenes/AdminDashboardView.fxml"),
    TUTOR_DASHBOARD("/org/tcms/view/TutorScenes/TutorDashboardView.fxml"),
    STUDENT_DASHBOARD("/org/tcms/view/StudentScenes/StudentDashboardView.fxml"),
    RECEP_DASHBOARD("/org/tcms/view/ReceptionistScenes/ReceptionistDashboardView.fxml"),
    ADMIN_SIDE_MENU("/org/tcms/view/AdminSideMenu.fxml"),
    TUTOR_SIDE_MENU("/org/tcms/view/TutorSideMenu.fxml"),
    STUDENT_SIDE_MENU("/org/tcms/view/StudentSideMenu.fxml"),
    RECEP_SIDE_MENU("/org/tcms/view/ReceptionistScenes/ReceptionistSideMenu.fxml");

    private final String fxmlPath;
    View(String fxmlPath) { this.fxmlPath = fxmlPath; }
    public String getPath() { return fxmlPath; }
}
