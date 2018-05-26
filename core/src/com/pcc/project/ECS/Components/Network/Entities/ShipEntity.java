package com.pcc.project.ECS.Components.Network.Entities;

import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;

import java.util.Map;

public class ShipEntity extends NetworkEntity {
    public static Prefab<Entity> createPrefab ( Map<String, String> message ) {
        return null;
    }

    public ShipEntity ( com.pcc.project.ECS.Entity entity, String name ) {
        super( entity, name );
    }

    @Override
    public void obey ( Map< String, String > entity ) {

    }
}
