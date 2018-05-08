package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class SetToMouse extends Component {
    protected Transform transform;

    protected Renderer2D renderer;

    public SetToMouse ( Entity entity, String name ) {
        super( entity, name );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.transform = this.entity.getComponent( Transform.class );
        this.renderer = this.entity.getComponentInParent( Renderer2D.class );
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        Camera camera = this.renderer.getCamera();

        if ( camera != null ) {
            Vector2 worldPosition = camera.transform.transformPoint( camera.viewport.unproject( new Vector2( Gdx.input.getX(), Gdx.input.getY() ) ) );

            this.transform.setGlobalRotation( 0 );
            this.transform.setGlobalPosition( worldPosition );

//            Gdx.app.log( "SetToMouse", this.transform.getPosition().toString() );
        }
    }
}
