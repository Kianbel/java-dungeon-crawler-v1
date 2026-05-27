package world;

import entity.Entity;
import util.Position;

import java.util.ArrayList;
import java.util.List;

public class InfestedRoom extends  Room {

    public InfestedRoom(int height, int length, Position minimapPosition) {
        super(height, length, minimapPosition);
    }

    @Override
    public void populateWithEntities() {
        if(layout == null) throw new RuntimeException("Room not generated");

        final int MONSTER_AMOUNT = 5;

        List<Entity> entities = new ArrayList<>();
        for(int i = 0; i < MONSTER_AMOUNT; i++) {
            // TODO: spawn monsters;
        }
    }
}
