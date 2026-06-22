package entity;

public abstract class MonsterFSM<T extends Enum<T>> {
    protected final Monster owner;
    protected T currentState;
    protected Player player;

    public MonsterFSM(Monster owner) {
        this.owner = owner;
    }

    public abstract void setupInitialState();

    public abstract void update();

    public abstract void switchState(T newState);

    public T getCurrentState() {
        return currentState;
    }
    }
