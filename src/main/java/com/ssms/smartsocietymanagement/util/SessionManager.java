package com.ssms.smartsocietymanagement.util;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Resident;

public class SessionManager {
    private static String currentUser;
    private static String userType;

    public static void setCurrentUser(String user, String usertype) {
        currentUser = user;
        userType = usertype;
    }

    public static String getCurrentUserId() {
        return currentUser;
    }
    public static String getCurrentUserType() {
        return userType;
    }

}