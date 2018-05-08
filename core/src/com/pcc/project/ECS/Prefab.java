package com.pcc.project.ECS;

public abstract class Prefab<T extends Entity> {
    public abstract T instantiate ();
}
