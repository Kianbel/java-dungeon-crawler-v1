package core;

import entity.Entity;

public abstract class EntityFSM<T extends Enum<T>> {
    protected final Entity owner;
    protected T currentState;

    public EntityFSM(Entity owner) {
        this.owner = owner;
    }

    public abstract void setupInitialState();

    public abstract void update();

    public abstract void switchState(T newState);

    public T getCurrentState() {
        return currentState;
    }
}
