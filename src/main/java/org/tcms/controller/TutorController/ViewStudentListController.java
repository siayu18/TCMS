package org.tcms.controller.TutorController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;

public class ViewStudentListController {

    @FXML private ComboBox<TuitionClass> chooseClassBox;
    @FXML private TableColumn<Student, String> studentIDColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> classColumn;
    @FXML private TableColumn<Student, String> levelColumn;
    @FXML private TableColumn<Student, String> contactColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    private List<TuitionClass> tuitionClass;
    private TuitionClassService tuitionClassService;

    public void initialize() {
        try {
            tuitionClassService = new TuitionClassService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }


        String currentUserId = Session.getCurrentUserID();
        setupChooseClassBox();
    }

    private void setupChooseClassBox() {
        tuitionClass = tuitionClassService.getClassesFromTutor();
        chooseClassBox.getItems().setAll(tuitionClass);

        chooseClassBox.setCellFactory(cb -> new ListCell<TuitionClass>() {
                    @Override
                    protected void updateItem(TuitionClass u, boolean empty) {
                        super.updateItem(u, empty);
                        setText(empty || u == null ? null : u.getClassID() + "- " + u.getSubjectName());
                    }
                }
                );

        chooseClassBox.setButtonCell(new ListCell<TuitionClass>() {
            @Override
            protected void updateItem(TuitionClass u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getClassID() + "- " + u.getSubjectName());
            }
        });
    }
}
