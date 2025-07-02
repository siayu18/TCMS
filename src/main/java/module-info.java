module org.tcms {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires com.opencsv;
    requires static lombok;
    requires java.logging;
    requires com.jfoenix;
    requires java.compiler;

    opens org.tcms to javafx.fxml;
    exports org.tcms;
    exports org.tcms.component;
    opens org.tcms.component to javafx.fxml;
    exports org.tcms.controller.AdminController;
    opens org.tcms.controller.AdminController to javafx.fxml;
    exports org.tcms.controller.ReceptionistController;
    opens org.tcms.controller.ReceptionistController to javafx.fxml;
    exports org.tcms.controller.SystemController;
    opens org.tcms.controller.SystemController to javafx.fxml;
    exports org.tcms.controller.TutorController;
    opens org.tcms.controller.TutorController to javafx.fxml;
    exports org.tcms.controller.StudentController;
    opens org.tcms.controller.StudentController to javafx.fxml;
}