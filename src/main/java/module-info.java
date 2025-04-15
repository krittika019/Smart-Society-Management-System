module SmartSocietyManagement {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    exports com.ssms.smartsocietymanagement.app;
    opens com.ssms.smartsocietymanagement.app to javafx.fxml;
    exports com.ssms.smartsocietymanagement.controller;
    opens com.ssms.smartsocietymanagement.controller to javafx.fxml;
}