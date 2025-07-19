package org.tcms.controller.StudentController;

import org.tcms.controller.SystemController.BaseDashController;
import org.tcms.utils.Session;

public class StudentDashController extends BaseDashController {
    @Override
    protected String formatGreeting() {
        return "Welcome Back " + "Student, " + Session.getCurrentUserName() + "!";
    }
}
