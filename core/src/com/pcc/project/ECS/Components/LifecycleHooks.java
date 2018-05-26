package com.pcc.project.ECS.Components;

import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

import java.util.function.Consumer;

public class LifecycleHooks extends Component {
    protected Consumer<Entity> hookOnAwake;

    protected Consumer<Entity> hookOnEnable;

    protected Consumer<Entity> hookOnDisable;

    protected Consumer<Entity> hookOnUpdate;

    protected Consumer<Entity> hookOnAfterUpdate;

    protected Consumer<Entity> hookOnDraw;

    protected Consumer<Entity> hookOnAfterDraw;

    protected Consumer<Entity> hookOnDestroy;

    public LifecycleHooks ( Entity entity, String name ) {
        super( entity, name );
    }

    public LifecycleHooks setOnAwake ( Consumer< Entity > hookOnAwake ) {
        this.hookOnAwake = hookOnAwake;

        return this;
    }

    public LifecycleHooks setOnEnable ( Consumer< Entity > hookOnEnable ) {
        this.hookOnEnable = hookOnEnable;

        return this;
    }

    public LifecycleHooks setOnDisable ( Consumer< Entity > hookOnDisable ) {
        this.hookOnDisable = hookOnDisable;

        return this;
    }

    public LifecycleHooks setOnUpdate ( Consumer<Entity> hookOnUpdate ) {
        this.hookOnUpdate = hookOnUpdate;

        return this;
    }

    public LifecycleHooks setOnAfterUpdate ( Consumer< Entity > hookOnAfterUpdate ) {
        this.hookOnAfterUpdate = hookOnAfterUpdate;

        return this;
    }

    public LifecycleHooks setOnDraw ( Consumer<Entity> hookOnDraw ) {
        this.hookOnDraw = hookOnDraw;

        return this;
    }

    public LifecycleHooks setOnAfterDraw ( Consumer< Entity > hookOnAfterDraw ) {
        this.hookOnAfterDraw = hookOnAfterDraw;

        return this;
    }

    public LifecycleHooks setOnDestroy ( Consumer< Entity > hookOnDestroy ) {
        this.hookOnDestroy = hookOnDestroy;

        return this;
    }

    @Override
    public void onAwake () {
        super.onAwake();

        if ( this.hookOnAwake != null ) {
            this.hookOnAwake.accept( this.entity );
        }
    }

    @Override
    public void onEnable () {
        super.onEnable();

        if ( this.hookOnEnable != null ) {
            this.hookOnEnable.accept( this.entity );
        }
    }

    @Override
    public void onDisable () {
        super.onDisable();

        if ( this.hookOnDisable != null ) {
            this.hookOnDisable.accept( this.entity );
        }
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        if ( this.hookOnUpdate != null ) {
            this.hookOnUpdate.accept( this.entity );
        }
    }

    @Override
    public void onAfterUpdate () {
        super.onAfterUpdate();

        if ( this.hookOnAfterUpdate != null ) {
            this.hookOnAfterUpdate.accept( this.entity );
        }
    }

    @Override
    public void onDraw () {
        super.onDraw();

        if ( this.hookOnDraw != null ) {
            this.hookOnDraw.accept( this.entity );
        }
    }

    @Override
    public void onAfterDraw () {
        super.onAfterDraw();

        if ( this.hookOnAfterDraw != null ) {
            this.hookOnAfterDraw.accept( this.entity );
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        if ( this.hookOnDestroy != null ) {
            this.hookOnDestroy.accept( this.entity );
        }
    }
}
