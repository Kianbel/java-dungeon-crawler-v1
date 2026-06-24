package core.room.loader;

import core.room.type.*;
import util.TILE;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RoomLayoutLoader {
    private static final RoomLayoutLoader instance = new RoomLayoutLoader();

    private RoomLayoutLoader() {}
    public static RoomLayoutLoader getInstance() { return instance; }

    public void loadAllLayouts(String layoutDirectoryPath) {
        File folder = new File(layoutDirectoryPath);
        for(File file : Objects.requireNonNull(folder.listFiles())) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                if(lines.isEmpty()) continue;

                final int roomHeight = lines.size();
                final int roomWidth = lines.getFirst().split(",").length;
                TILE[][] layout = new TILE[roomHeight][roomWidth];
                for(TILE[] row : layout) {
                    Arrays.fill(row, TILE.WALL);
                }

                for(int y = 0; y < lines.size(); y++) {
                    String[] tokens = lines.get(y).split(",");
                    for (int x = 0; x < tokens.length; x++) {
                        String token = tokens[x].trim(); // trim spaces just in case

                        if (!token.isEmpty() && token.matches("\\d+")) { // Check if it's a number
                            int tileIndex = Integer.parseInt(token);
                            if (tileIndex >= 0 && tileIndex < TILE.values().length) {
                                layout[y][x] = TILE.values()[tileIndex];
                            }
                        }
                    }
                }

                // --- GET ROOM CLASS FROM FILENAME ---
                String fileName = file.getName();
                int i;
                // filename format: e_{roomtype}{number}.csv
                // so start at 2nd index (disregard 'e_')
                for(i = 2; i < fileName.length(); i++) {
                    if(!Character.isAlphabetic(fileName.charAt(i))) break;
                }
                String classString = fileName.substring(2, i);
                Class<? extends Room> roomClass = getRoomClassFromFileName(classString);

                RoomLayoutRegistry.getInstance().addLayout(roomClass, layout);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
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
}
