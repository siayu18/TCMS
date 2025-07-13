package org.tcms.controller.StudentController;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;

public class TransferRequestController {
    //FXML Components
    @FXML private TextField reasonField;
    @FXML private Label errorLabel;
    @FXML private ComboBox selectOldSub;
    @FXML private  ComboBox selectNewSub;
    @FXML private JFXButton clearBtn;
    @FXML private JFXButton submitBtn;

    //Services

    private void initialize() {
    };

    private void configureActions(){
        submitBtn.setOnAction(e -> {
            try {
                isRequiredEmpty();

                errorLabel.setVisible(false);
                AlertUtils.showInformation("Successfully Submitted Transfer Request!", "Your request to transfer to: " + selectNewSub.getValue().toString() + " has been submitted, check communication hub for updates!");
                clearAll();

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

    };

    private void isRequiredEmpty() throws EmptyFieldException {
        boolean hasEmptyFields = Helper.validateFieldNotEmpty(reasonField) ||
                Helper.validateComboBoxNotEmpty(selectOldSub) ||
                Helper.validateComboBoxNotEmpty(selectNewSub);

        if (hasEmptyFields) {
            throw new EmptyFieldException("Required field(s) with indication (*) is empty!");
        }
    }

    private void clearAll() {
        reasonField.clear();
        selectNewSub.setValue(null);
        selectOldSub.setValue(null);
        errorLabel.setVisible(false);
    }
}
