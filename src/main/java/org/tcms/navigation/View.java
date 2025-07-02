package org.tcms.navigation;

public enum View {
    TOOLBAR("/org/tcms/view/SystemScenes/ToolbarView.fxml"),
    LOGIN("/org/tcms/view/SystemScenes/LoginView.fxml"),
    EDIT_PROFILE("/org/tcms/view/SystemScenes/EditProfile.fxml"),
    ADMIN_DASHBOARD("/org/tcms/view/AdminScenes/AdminDashboardView.fxml"),
    TUTOR_DASHBOARD("/org/tcms/view/TutorScenes/TutorDashboardView.fxml"),
    STUDENT_DASHBOARD("/org/tcms/view/StudentScenes/StudentDashboardView.fxml"),
    RECEP_DASHBOARD("/org/tcms/view/ReceptionistScenes/ReceptionistDashboardView.fxml"),
    ADMIN_SIDE_MENU("/org/tcms/view/AdminScenes/AdminSideMenu.fxml"),
    TUTOR_SIDE_MENU("/org/tcms/view/TutorScenes/TutorSideMenu.fxml"),
    TUTOR_UPDATE_CLASS("/org/tcms/view/TutorScenes/UpdateClassDetails.fxml"),
    STUDENT_SIDE_MENU("/org/tcms/view/StudentSideMenu.fxml"),
    RECEP_SIDE_MENU("/org/tcms/view/ReceptionistScenes/ReceptionistSideMenu.fxml"),
    COMMUNICATION("/org/tcms/view/ReceptionistScenes/CommunicationView.fxml"),
    UPD_DEL_STUDENT("/org/tcms/view/ReceptionistScenes/UpdDelStudentView.fxml"),
    REGISTER_ENROLL_STUDENT("/org/tcms/view/ReceptionistScenes/RegEnrolStudentView.fxml"),
    UPD_ENROLMENT("/org/tcms/view/ReceptionistScenes/UpdEnrollmentView.fxml"),
    ACCEPT_PAYMENT("/org/tcms/view/ReceptionistScenes/AcceptPaymentView.fxml"),
    GENERATE_RECEIPT("/org/tcms/view/ReceptionistScenes/GenerateReceiptView.fxml"),
    MONTHLY_STATS("/org/tcms/view/AdminScenes/MonthlyStats.fxml"),
    REG_STAFF("/org/tcms/view/AdminScenes/RegStaff.fxml"),
    UPD_STAFF("/org/tcms/view/AdminScenes/UpdateStaffDetails.fxml");

    private final String fxmlPath;

    View(String fxmlPath) { this.fxmlPath = fxmlPath; }
    public String getPath() { return fxmlPath; }
}

