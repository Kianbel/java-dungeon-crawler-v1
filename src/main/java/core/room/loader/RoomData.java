package core.room.loader;

import core.room.Room;
import util.TILE;

public class RoomData {
    private final TILE[][] layout;
    private final Class<? extends Room> type;

    public RoomData(TILE[][] layout, Class<? extends Room> type) {
        this.layout = layout;
        this.type = type;

        System.out.println("room made with type: " + type);
        print();
    }

    public TILE[][] getLayout() {
        return layout;
    }

    public Class<? extends Room> getType() {
        return type;
    }

    private void print() {
        for(int i = 0; i < layout.length; i++) {
            for(int j = 0; j < layout[0].length; j++) {
                System.out.print(layout[i][j]);
            }
            System.out.println();
        }
    }
}
