package core.room.type;

import core.GameManager;
import core.room.loader.RoomLayoutRegistry;
import item.armor.*;
import item.armor.special.FlamenCloak;
import item.armor.special.IlluminiteCloak;
import item.armor.special.PrecisionThingamabob;
import item.armor.special.Shield;
import item.heal.HealingPotion;
import item.key.TreasureRoomKey;
import item.weapon.*;
import util.Position;
import util.Randomizer;
import util.TILE;
import world.Chest;
import world.DroppedItem;
import world.InteractableTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureRoom extends Room {
    private Position treasureChestPos;

    public TreasureRoom(Position minimapPosition) {
        TILE[][] layout = RoomLayoutRegistry.getInstance().getRandomLayoutFromRoomClass(TreasureRoom.class);
        super(layout, minimapPosition);

        handleKeySpawn();
        handleTreasureChestLoot();
    }

    private void handleTreasureChestLoot() {
        int currentFloor = GameManager.getInstance().getCurrentFloor();
        InteractableTile chestDrop = null;
        switch(currentFloor) {
            case 1 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(treasureChestPos, new Mace());
                    case 2 -> chestDrop = new DroppedItem(treasureChestPos, new ChainHauberk());
                    case 3 -> chestDrop = new DroppedItem(treasureChestPos, new HealingPotion(3));
                }
            }
            case 2 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(treasureChestPos, new GreatSword());
                    case 2 -> chestDrop = new DroppedItem(treasureChestPos, new IronMail());
                    case 3 -> chestDrop = new DroppedItem(treasureChestPos, new Shield());
                }
            }
            case 3 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(treasureChestPos, new GreatClub());
                    case 2 -> chestDrop = new DroppedItem(treasureChestPos, new ReinforcedIronBreastplate());
                    case 3 -> chestDrop = new DroppedItem(treasureChestPos, new PrecisionThingamabob());
                }
            }
            case 4 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(treasureChestPos, new Halberd());
                    case 2 -> chestDrop = new DroppedItem(treasureChestPos, new OdrilMail());
                    case 3 -> chestDrop = new DroppedItem(treasureChestPos, new IlluminiteCloak());
                }
            }
            case 5 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(treasureChestPos, new Claymore());
                    case 2 -> chestDrop = new DroppedItem(treasureChestPos, new CobaltChestplate());
                    case 3 -> chestDrop = new DroppedItem(treasureChestPos, new FlamenCloak());
                }
            }
        }
        InteractableTile treasureChest = new Chest(treasureChestPos, chestDrop);
        addInteractableTile(treasureChest);
    }

    public void handleKeySpawn() {
        List<Position> possibleKeysPos = new ArrayList<>();
        for(int y = 0; y < layout.length; y++) {
            for(int x = 0; x < layout[0].length; x++) {
                if(layout[y][x] == TILE.POSSIBLE_KEY) possibleKeysPos.add(new Position(x,y));
                if(layout[y][x] == TILE.CHEST) treasureChestPos = new Position(x,y);
            }
        }
        for(Position p : possibleKeysPos) {
            this.layout[p.y][p.x] = TILE.FLOOR;
        }

        Position keyPos = possibleKeysPos.remove(new Random().nextInt(possibleKeysPos.size()));
        InteractableTile key = new DroppedItem(keyPos, new TreasureRoomKey());
        addInteractableTile(key);
    }
}
