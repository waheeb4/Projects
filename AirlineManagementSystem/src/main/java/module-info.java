module com.airlinemanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.airlinemanagementsystem.classes;
    opens com.airlinemanagementsystem.classes to javafx.fxml;
    exports com.airlinemanagementsystem.controllers;
    opens com.airlinemanagementsystem.controllers to javafx.fxml;

}