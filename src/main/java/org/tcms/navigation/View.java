package org.tcms.navigation;

public enum View {
    TOOLBAR("/org/tcms/view/SystemScenes/Toolbar.fxml"),
    LOGIN("/org/tcms/view/SystemScenes/Login.fxml"),
    EDIT_PROFILE("/org/tcms/view/SystemScenes/EditProfile.fxml"),
    ADMIN_DASHBOARD("/org/tcms/view/AdminScenes/AdminDash.fxml"),
    TUTOR_DASHBOARD("/org/tcms/view/TutorScenes/TutorDash.fxml"),
    STUDENT_DASHBOARD("/org/tcms/view/StudentScenes/StudentDash.fxml"),
    RECEP_DASHBOARD("/org/tcms/view/ReceptionistScenes/ReceptionistDash.fxml"),
    ADMIN_SIDE_MENU("/org/tcms/view/AdminScenes/AdminSideMenu.fxml"),
    TUTOR_SIDE_MENU("/org/tcms/view/TutorScenes/TutorSideMenu.fxml"),
    TUTOR_UPDATE_CLASS("/org/tcms/view/TutorScenes/UpdateClassDetails.fxml"),
    TUTOR_VIEW_STD_LIST("/org/tcms/view/TutorScenes/ViewStudentList.fxml"),
    TUTOR_CREATE_CLASS("/org/tcms/view/TutorScenes/TutorCreateClass.fxml"),
    STUDENT_SIDE_MENU("/org/tcms/view/StudentScenes/StudentSideMenu.fxml"),
    RECEP_SIDE_MENU("/org/tcms/view/ReceptionistScenes/ReceptionistSideMenu.fxml"),
    COMMUNICATION("/org/tcms/view/ReceptionistScenes/Communication.fxml"),
    UPD_DEL_STUDENT("/org/tcms/view/ReceptionistScenes/UpdDelStudent.fxml"),
    REGISTER_ENROLL_STUDENT("/org/tcms/view/ReceptionistScenes/RegEnrolStudent.fxml"),
    UPD_ENROLMENT("/org/tcms/view/ReceptionistScenes/UpdEnrollment.fxml"),
    ACCEPT_PAYMENT("/org/tcms/view/ReceptionistScenes/AcceptPayment.fxml"),
    VIEW_PAYMENT_HISTORY("/org/tcms/view/ReceptionistScenes/ViewPayment.fxml"),
    GENERATE_RECEIPT("/org/tcms/view/ReceptionistScenes/GenerateReceipt.fxml"),
    VIEW_TRANSFER_REQUEST("/org/tcms/view/ReceptionistScenes/ViewTransferRequest.fxml"),
    MONTHLY_STATS("/org/tcms/view/AdminScenes/MonthlyStats.fxml"),
    REG_STAFF("/org/tcms/view/AdminScenes/RegStaff.fxml"),
    UPD_STAFF("/org/tcms/view/AdminScenes/UpdateStaffDetails.fxml"),
    UPD_SUB("/org/tcms/view/AdminScenes/ReassignTutor.fxml"),
    PAY("/org/tcms/view/StudentScenes/PayView.fxml"),
    VIEW_SCHEDULE("/org/tcms/view/StudentScenes/ViewSchedule.fxml"),
    VIEW_PAYMENT_STATUS("/org/tcms/view/StudentScenes/ViewPaymentStatus.fxml"),
    REQUEST_TRANSFER("/org/tcms/view/StudentScenes/TransferRequest.fxml"),
    DELETE_REQUEST("/org/tcms/view/StudentScenes/DeleteRequest.fxml"),
    VIEW_RECEIPT("/org/tcms/view/StudentScenes/ViewReceipt.fxml");

    private final String fxmlPath;

    View(String fxmlPath) { this.fxmlPath = fxmlPath; }
    public String getPath() { return fxmlPath; }
}

