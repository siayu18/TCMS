package org.tcms.controller.ReceptionistController;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.tcms.model.Enrollment;
import org.tcms.model.Payment;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;
import org.tcms.service.EnrollmentService;
import org.tcms.service.PaymentService;
import org.tcms.service.StudentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.ComponentUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GenerateReceiptController {

    public Button generateBtn;
    public Label errorLabel;
    public AnchorPane receiptPane;
    public Label title;
    public ScrollPane scrollBar;
    public VBox receiptBox;
    public JFXComboBox chooseStudentBox;

    private List<Student> students;
    private List<Payment> payments;
    private Map<String, TuitionClass> classMap;
    private Map<String, Enrollment> enrollmentMap;
    private Student selectedStudent;

    private StudentService studentService;
    private TuitionClassService tuitionClassService;
    private EnrollmentService enrollmentService;
    private PaymentService paymentService;

    @FXML
    public void initialize() {
        try {
            studentService = new StudentService();
            tuitionClassService = new TuitionClassService();
            enrollmentService = new EnrollmentService();
            paymentService = new PaymentService();
        } catch (IOException e) {
            errorLabel.setText("Failed to load data.");
            errorLabel.setVisible(true);
            return;
        }

        students = studentService.getAllStudents();

        ComponentUtils.configureStudentBox(chooseStudentBox, students);
        configureActions();
    }

    private void configureActions() {
        chooseStudentBox.setOnAction(e -> {
            selectedStudent = ((Student) chooseStudentBox.getValue());
            if (selectedStudent != null) {
                receiptPane.setVisible(true);
                generateBtn.setVisible(true);
            }
        });
    }
}
