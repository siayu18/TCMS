package org.tcms.controller.StudentController;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.tcms.model.Enrollment;
import org.tcms.model.TuitionClass;
import org.tcms.model.User;
import org.tcms.service.EnrollmentService;
import org.tcms.service.TuitionClassService;
import org.tcms.service.UserService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ViewScheduleController {
    public VBox mondayBox;
    public VBox tuesdayBox;
    public VBox wednesdayBox;
    public VBox thursdayBox;
    public VBox fridayBox;
    public VBox saturdayBox;

    private List<TuitionClass> classes;
    private List<Enrollment> enrollments;

    private TuitionClassService tuitionClassService;
    private EnrollmentService enrollmentService;
    private UserService userService;

    public void initialize() {
        try {
            enrollmentService = new EnrollmentService();
            tuitionClassService = new TuitionClassService();
            userService = new UserService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        // get all enrollments related to current student
        enrollments = enrollmentService.getAllEnrollment().stream()
                .filter(e -> Session.getCurrentUserID().equalsIgnoreCase(e.getStudentID()))
                .collect(Collectors.toList());

        // get all classes that current student enrolled in
        classes = enrollments.stream()
                .map(row -> tuitionClassService.getClassByID(row.getClassID()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        setSchedule();
    }

    private void setSchedule() {
        for (TuitionClass tuitionClass : classes) {
            User tutor = userService.getUserByID(tuitionClass.getTutorID());
            String tutorName = tutor != null ? tutor.getUsername() : "Unknown";

            Label classInfo = new Label(
                    String.format(
                            "%s - %s\nDuration: %s - %s\nTutor: %s - %s",
                            tuitionClass.getClassID(),
                            tuitionClass.getSubjectName(),
                            tuitionClass.getStartTime(),
                            tuitionClass.getEndTime(),
                            tuitionClass.getTutorID(),
                            tutorName
                    )
            );
            classInfo.getStyleClass().add("schedule-label");

            // add to the desired VBox
            switch (tuitionClass.getDay().toLowerCase()) {
                case "monday" -> mondayBox.getChildren().add(classInfo);
                case "tuesday" -> tuesdayBox.getChildren().add(classInfo);
                case "wednesday" -> wednesdayBox.getChildren().add(classInfo);
                case "thursday" -> thursdayBox.getChildren().add(classInfo);
                case "friday" -> fridayBox.getChildren().add(classInfo);
                case "saturday" -> saturdayBox.getChildren().add(classInfo);
            }
        }
    }
}
