package com.ssms.smartsocietymanagement.util;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Resident;

public class SessionManager {
    private static Object currentUser = null;
    private static boolean isAdmin = false;

    public static void setCurrentUser(Object user) {
        currentUser = user;
        isAdmin = (user instanceof Admin);
    }

    public static Object getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static String getCurrentUsername() {
        if (currentUser instanceof Admin) {
            return ((Admin) currentUser).getUsername();
        } else if (currentUser instanceof Resident) {
            return ((Resident) currentUser).getUsername();
        }
        return "User";
    }

    public static String getCurrentUserId() {
        if (currentUser instanceof Admin) {
            return ((Admin) currentUser).getId();
        } else if (currentUser instanceof Resident) {
            return ((Resident) currentUser).getId();
        }
        return null;
    }

    public static void clearSession() {
        currentUser = null;
        isAdmin = false;
    }
}