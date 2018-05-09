package com.pcc.project.ECS;

public class Component {
    public static String defaultName = null;

    public Entity entity;

    public String name;

    public boolean enabled = true;

    public Component ( Entity entity, String name ) {
        this.entity = entity;
        this.name = name;
    }

    public boolean getEnabled () {
        return this.enabled;
    }

    public Component setEnabled ( boolean enabled ) {
        this.enabled = enabled;

        return this;
    }

    public void onCreate () { }

    public void onAwake () { }

    public void onDestroy () { }

    public void onEnable () { }

    public void onDisable () { }

    /**
     * Called before the children are updated
     */
    public void onUpdate () { }

    /**
     * Called after the children are updated
     */
    public void onAfterUpdate () { }

    /**
     * Called before the children are rendered
     * */
    public void onDraw () { }

    /**
     * Called after the children are drawn
     * */
    public void onAfterDraw () { }

    public void destroy () {
        this.entity.destroyComponent( this );
    }
}
