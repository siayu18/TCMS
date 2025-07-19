package org.tcms.controller.ReceptionistController;

import org.tcms.controller.SystemController.BaseDashController;
import org.tcms.utils.Session;


public class RecepDashController extends BaseDashController {

    @Override
    protected String formatGreeting() { return "Welcome Back " + "Receptionist, " + Session.getCurrentUserName() + "!"; }
}
