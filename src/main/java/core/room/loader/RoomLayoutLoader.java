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

                for(int y = 0; y < imageHeight; y++) {
                    for(int x = 0; x < imageWidth; x++) {
                        final int pixel = image.getRGB(x, y);
                        final int alpha = (pixel >> 24) & 0xff;

                        if(alpha <= 50) continue;
                        layout[y][x] = pixelToTile(pixel);
                    }
                }

                String fileName = file.getName();
                int i;
                for(i = 0; i < fileName.length(); i++) {
                    if(Character.isDigit(fileName.charAt(i))) break;
                }
                String classString = fileName.substring(0, i);
                Class<? extends Room> roomClass;
                switch(classString) {
                    case "boss" -> roomClass = BossRoom.class;
                    case "clear" -> roomClass = ClearRoom.class;
                    case "infested" -> roomClass = InfestedRoom.class;
                    case "spawn" -> roomClass = SpawnRoom.class;
                    case "treasure" -> roomClass = TreasureRoom.class;
                    default -> {
                        throw new RuntimeException("Invalid class string: " + classString + ", filename: " + fileName);
                    }
                }

                RoomLayoutRegistry.getInstance().addLayout(roomClass, layout);
            }
            catch (IOException _) {}
        }
    }

    private TILE pixelToTile(int pixel) {
        /* room tiles
            WALL, = 0x000000
            FLOOR, = 0x808080
            DOOR, = 0x682700 // NOT USED
            GRASS, = 0x00FF00
            WATER, = 0x00FFFF
            SOLID_OBSTACLE, = 0xFF00FF
            PASSABLE_OBSTACLE, = 0xffff00

            BOOKSHELF = 0x7f1dff
         */

        switch (pixel & 0x00FFFFFF) {
            case 0x000000 -> { return TILE.WALL; }
            case 0x682700 -> { return TILE.DOOR; }
            case 0x00FF00 -> { return TILE.GRASS; }
            case 0x00FFFF -> { return TILE.WATER; }
            case 0xFF00FF -> { return TILE.SOLID_OBSTACLE; }
            case 0xffff00 -> { return TILE.PASSABLE_OBSTACLE; }
            case 0x7f1dff -> { return TILE.BOOKSHELF; }
            default -> { return TILE.FLOOR; }
        }
    }
}
