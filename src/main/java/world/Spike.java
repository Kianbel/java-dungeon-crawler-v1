package world;

import entity.Entity;
import entity.MoveAfterPlayer;
import gui.dataclass.UITheme;
import util.Position;

import java.util.Random;

public class Spike extends InteractableTile implements MoveAfterPlayer {
    public boolean isActivated = true;
    private int tick = 0;
    private Random random = new Random();

    public Spike(Position roomLayoutPosition) {
        super(roomLayoutPosition, false);
        tick = random.nextInt(0,2);
    }

    @Override
    public void onEntityEnter(Entity entity) {
        if(isActivated) {
            entity.hurt(random.nextInt(1,5));
        }
    }

    @Override
    public void onEntityStay(Entity entity) {
        if(isActivated) {
            entity.hurt(random.nextInt(1,5));
        }
    }

    @Override
    public void makeMove() {
        if(tick == 2) {
            isActivated = !isActivated;
            if(!isActivated) overrideColor(UITheme.TILE_FLOOR);
            else resetColor();
            tick = 0;
            return;
        }
        tick++;
    }
}
