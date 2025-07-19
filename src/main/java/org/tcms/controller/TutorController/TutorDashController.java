package org.tcms.controller.TutorController;

import org.tcms.controller.SystemController.BaseDashController;
import org.tcms.utils.Session;


public class TutorDashController extends BaseDashController {

    @Override
    protected String formatGreeting() { return "Welcome Back " + "Tutor, " + Session.getCurrentUserName() + "!"; }
}


