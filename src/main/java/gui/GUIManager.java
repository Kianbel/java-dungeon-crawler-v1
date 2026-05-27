package gui;

import core.EntityRoomManager;
import entity.Entity;
import util.TILE;
import world.Room;

import java.util.List;

public class GUIManager {
    public static final GUIManager instance = new GUIManager();

    public static final int SCREEN_WIDTH_PX = 640;
    public static final int SCREEN_HEIGHT_PX = 360;
    public static final int FONT_SIZE_PX = 20;

    private GUIManager() {}
    public GUIManager getInstance() {
        return instance;
    }

    public void drawScreen() {
    }
}
