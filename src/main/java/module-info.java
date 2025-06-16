module org.tcms {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.opencsv;

    opens org.tcms to javafx.fxml;
    exports org.tcms;
}