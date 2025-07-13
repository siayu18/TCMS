package org.tcms.controller.ReceptionistController;

import org.tcms.controller.SystemController.BaseDashboardController;
import org.tcms.utils.Session;


public class RecepDashController extends BaseDashboardController {

    @Override
    protected String formatGreeting() {
        return "Welcome Back " + "Receptionist, " + Session.getCurrentUserName() + "!";
    }
}