module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.media;

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
    exports entity.boss;
    opens entity.boss to javafx.fxml;
    exports entity.monster;
    opens entity.monster to javafx.fxml;
    exports entity;
    opens entity to javafx.fxml;
}