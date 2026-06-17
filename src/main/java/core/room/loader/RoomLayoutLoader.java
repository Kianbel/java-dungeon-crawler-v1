package core.room.loader;

import core.room.type.*;
import util.TILE;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomLayoutLoader {
    private static final RoomLayoutLoader instance = new RoomLayoutLoader();

    private RoomLayoutLoader() {}
    public static RoomLayoutLoader getInstance() { return instance; }

    public void loadAllLayouts(String layoutDirectoryPath) {
        File folder = new File(layoutDirectoryPath);
        for(File file : Objects.requireNonNull(folder.listFiles())) {
            try {
                BufferedImage image = ImageIO.read(file);

                final int imageWidth = image.getWidth();
                final int imageHeight = image.getHeight();

                TILE[][] layout = new TILE[imageHeight][imageWidth];

                // --- EXTRACT TILE LAYOUT FROM IMAGE ---
                for(int y = 0; y < imageHeight; y++) {
                    for(int x = 0; x < imageWidth; x++) {
                        final int pixel = image.getRGB(x, y);
                        final int alpha = (pixel >> 24) & 0xff;

                        if(alpha <= 50) {
                            layout[y][x] = TILE.EMPTY;
                            continue;
                        }

                        layout[y][x] = pixelToTile(pixel);
                    }
                }
                // --- GET ROOM CLASS FROM FILENAME ---
                String fileName = file.getName();
                int i;
                for(i = 0; i < fileName.length(); i++) {
                    if(!Character.isAlphabetic(fileName.charAt(i))) break;
                }
                String classString = fileName.substring(0, i);
                Class<? extends Room> roomClass = getRoomClassFromFileName(classString);

                RoomLayoutRegistry.getInstance().addLayout(roomClass, layout);
            }
            catch (IOException _) {}
        }
    }

    private static Class<? extends Room> getRoomClassFromFileName(String classString) {
        Class<? extends Room> roomClass;
        switch(classString) {
            case "boss" -> roomClass = BossRoom.class;
            case "normal" -> roomClass = NormalRoom.class;
            case "spawn" -> roomClass = SpawnRoom.class;
            case "treasure" -> roomClass = TreasureRoom.class;
            default -> throw new RuntimeException("Invalid class string: " + classString);
        }
        return roomClass;
    }

    private TILE pixelToTile(int pixel) {
        /* room tiles
            WALL, = 0x000000
            FLOOR, = 0x808080
            DOOR, = 0x682700
            GRASS, = 0x00FF00
            WATER, = 0x00FFFF
            PASSABLE_OBSTACLE, = 0xffff00
            BOOKSHELF = 0x7f1dff
            BOX = 0xFF00FF
            WEB = 0xFFFFFF
            TORCH = 0xff8000
            CHEST = 0x56391c
            LEVEL_DOOR = 0xff6262
            LOCKED_DOOR = 0xff0000
            STAIRCASE = 0x0f0038
         */

        switch (pixel & 0x00FFFFFF) {
            case 0x000000 -> { return TILE.WALL; }
            case 0x808080 -> { return TILE.FLOOR; }
            case 0x682700 -> { return TILE.DOOR; }
            case 0x00FF00 -> { return TILE.GRASS; }
            case 0x00FFFF -> { return TILE.WATER; }
            case 0xffff00 -> { return TILE.PASSABLE_OBSTACLE; }
            case 0x7f1dff -> { return TILE.BOOKSHELF; }
            case 0xFF00FF -> { return TILE.BOX; }
            case 0xFFFFFF -> { return TILE.WEB; }
            case 0xFF8000 -> { return TILE.TORCH; }
            case 0x56391C -> { return TILE.CHEST; }

            case 0xFF6262 -> { return TILE.LEVEL_DOOR; }
            case 0xFF0000 -> { return TILE.LOCKED_DOOR; }
            case 0x0F0038 -> { return TILE.STAIRCASE; }

            default -> { return TILE.EMPTY; }
        }
    }
}
