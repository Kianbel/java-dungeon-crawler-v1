module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens gui to javafx.fxml;
    exports gui;
}