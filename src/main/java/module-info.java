module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;

    opens gui to javafx.fxml;
    exports gui;
    exports util;
    opens util to javafx.fxml;
    exports core;
    opens core to javafx.fxml;
    exports gui.app;
    opens gui.app to javafx.fxml;
    exports gui.dataclass;
    opens gui.dataclass to javafx.fxml;
}