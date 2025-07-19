package org.tcms.controller.AdminController;

import org.tcms.controller.SystemController.BaseDashController;
import org.tcms.utils.Session;

public class AdminDashController extends BaseDashController {

    @Override
    protected String formatGreeting() { return "Welcome Back " + "Admin, " + Session.getCurrentUserName() + "!"; }
}

