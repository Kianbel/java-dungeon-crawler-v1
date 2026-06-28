package world;

import core.GameManager;
import item.armor.*;
import item.armor.special.IlluminiteCloak;
import item.armor.special.PrecisionThingamabob;
import item.armor.special.HeavyShield;
import item.heal.HealingPotion;
import item.weapon.*;
import util.Position;
import util.Randomizer;

public class TreasureChest extends Chest {
    public TreasureChest(Position roomLayoutPosition) {

        final int floor = GameManager.getInstance().getCurrentFloor();
        InteractableTile chestDrop = null;
        switch(floor) {
            case 1 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(roomLayoutPosition, new Mace());
                    case 2 -> chestDrop = new DroppedItem(roomLayoutPosition, new ChainHauberk());
                    case 3 -> chestDrop = new DroppedItem(roomLayoutPosition, new HealingPotion(3));
                }
            }
            case 2 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(roomLayoutPosition, new GreatSword());
                    case 2 -> chestDrop = new DroppedItem(roomLayoutPosition, new IronMail());
                    case 3 -> chestDrop = new DroppedItem(roomLayoutPosition, new HeavyShield());
                }
            }
            case 3 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(roomLayoutPosition, new GreatClub());
                    case 2 -> chestDrop = new DroppedItem(roomLayoutPosition, new ReinforcedIronBreastplate());
                    case 3 -> chestDrop = new DroppedItem(roomLayoutPosition, new PrecisionThingamabob());
                }
            }
            case 4 -> {
                switch (Randomizer.pick(1, 2, 3)) {
                    case 1 -> chestDrop = new DroppedItem(roomLayoutPosition, new Halberd());
                    case 2 -> chestDrop = new DroppedItem(roomLayoutPosition, new OdrilMail());
                    case 3 -> chestDrop = new DroppedItem(roomLayoutPosition, new IlluminiteCloak());
                }
            }
            case 5 -> {
                switch (Randomizer.pick(1, 2)) {
                    case 1 -> chestDrop = new DroppedItem(roomLayoutPosition, new Claymore());
                    case 2 -> chestDrop = new DroppedItem(roomLayoutPosition, new CobaltChestplate());
                }
            }
        }
        this(roomLayoutPosition, chestDrop);
    }

    public TreasureChest(Position roomLayoutPosition, InteractableTile chestDrop) {
        super(roomLayoutPosition, chestDrop);
    }
}
