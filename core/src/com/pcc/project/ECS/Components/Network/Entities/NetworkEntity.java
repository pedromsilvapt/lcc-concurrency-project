package com.pcc.project.ECS.Components.Network.Entities;

import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.Network.NetworkGameMaster;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;

import java.util.Map;

public abstract class NetworkEntity extends Component {
    public static Prefab<Entity> createPrefab ( NetworkGameMaster gameMaster, Map<String, String> message ) {
        return null;
    }

    protected String entityType;

    protected String entityId;

    public NetworkEntity ( Entity entity, String name ) {
        super( entity, name );
    }

    public String getEntityId () {
        return this.entityId;
    }

    public String getEntityType () {
        return this.entityType;
    }

    public NetworkEntity setEntityId ( String id ) {
        this.entityId = id;

        return this;
    }

    public NetworkEntity setEntityType ( String type ) {
        this.entityType = type;

        return this;
    }

    public abstract void obey ( Map<String, String> entity );
}
