module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires gui;


    opens gui to javafx.fxml;
    exports gui;
}