package core.room.loader;

import core.room.*;
import util.TILE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoomLoader {
    private final RoomTileTranslator translator = new RoomTileTranslator();
    private Map<Class<? extends Room>, RoomData> roomRegistry = new HashMap<>();

    public void loadRawRoomLayouts(String rawLayoutDirectoryPath) {
        final File directory = new File(rawLayoutDirectoryPath);


        int ctr = 0;
        for(File roomLayoutFile : Objects.requireNonNull(directory.listFiles())) {
            System.out.println(++ctr);

            try {
                List<String> lines = Files.readAllLines(roomLayoutFile.toPath());

                final int ROOM_HEIGHT = lines.size()-1;
                final int ROOM_LENGTH = lines.getFirst().length();

                TILE[][] roomLayout = new TILE[ROOM_HEIGHT][ROOM_LENGTH];
                Class<? extends Room> roomClass = stringToRoomClass(lines.getFirst());

                for(int y = 1; y < lines.size(); y++) {
                    String row = lines.get(y);
                    for(int x = 0; x < row.length(); x++) {
                        roomLayout[y-1][x] = translator.translate(row.charAt(x));
                    }
                }

                RoomData roomData = new RoomData(roomLayout, roomClass);
                roomRegistry.put(roomClass, roomData);
            }
            catch (IOException _) {}
        }
    }

    private Class<? extends Room> stringToRoomClass(String str) {
        switch (str) {
            case "infested" -> { return InfestedRoom.class; }
            case "clear" -> { return ClearRoom.class; }
            case "boss" -> { return BossRoom.class; }
            case "spawn" -> { return SpawnRoom.class; }
            case "treasure" -> { return TreasureRoom.class; }
            default -> throw new RuntimeException("Invalid room type: " + str);
        }
    }
}
