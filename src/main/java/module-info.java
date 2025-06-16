module org.tcms {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires com.opencsv;

    opens org.tcms to javafx.fxml;
    exports org.tcms;
    exports org.tcms.controller;
    opens org.tcms.controller to javafx.fxml;
    exports org.tcms.component;
    opens org.tcms.component to javafx.fxml;
}