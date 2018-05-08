package com.pcc.project.ECS.Components.Network;

import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class NetworkEntity extends Component {
    protected String entityId;

    protected String entityType;

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
}
