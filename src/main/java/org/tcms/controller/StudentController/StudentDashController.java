package org.tcms.controller.StudentController;

import org.tcms.controller.SystemController.BaseDashboardController;
import org.tcms.utils.Session;

public class StudentDashController extends BaseDashboardController {
    @Override
    protected String formatGreeting() {
        return "Welcome Back " + "Student, " + Session.getCurrentUserName() + "!";
    }
}
