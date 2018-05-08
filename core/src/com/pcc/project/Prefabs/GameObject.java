package com.pcc.project.Prefabs;

import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Components.Graphics2D.Camera;
import com.pcc.project.ECS.Components.Graphics2D.Renderer2D;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;

public class GameObject extends Prefab< Entity > {
    protected String name;

    protected Vector2 position;

    protected Vector2 scale;

    protected float rotation = 0;

    public GameObject () {
        this( null );
    }

    public GameObject ( String name ) {
        this( name, null, null, 0 );
    }

    public GameObject ( String name, Vector2 position, Vector2 scale, float rotation ) {
        this.name = name;
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    @Override
    public Entity instantiate () {
        Entity gameObject = new Entity( null, this.name );

        Transform transform = gameObject.addComponent( Transform.class, "transform" );

        if ( this.position != null ) {
            transform.setPosition( this.position.cpy() );
        }

        if ( this.scale != null ) {
            transform.setScale( this.scale.cpy() );
        }

        if ( this.rotation != 0 ) {
            transform.setRotation( this.rotation );
        }

        return gameObject;
    }
}
