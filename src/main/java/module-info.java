module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;

    opens gui to javafx.fxml;
    exports gui;
}