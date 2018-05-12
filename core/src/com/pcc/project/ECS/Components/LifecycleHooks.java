package com.pcc.project.ECS.Components;

import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

import java.util.function.Consumer;

public class LifecycleHooks extends Component {
    protected Consumer<Entity> hookOnUpdate;

    public LifecycleHooks ( Entity entity, String name ) {
        super( entity, name );
    }

    public LifecycleHooks setOnUpdate ( Consumer<Entity> hookOnUpdate ) {
        this.hookOnUpdate = hookOnUpdate;

        return this;
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        if ( this.hookOnUpdate != null ) {
            this.hookOnUpdate.accept( this.entity );
        }
    }
}
