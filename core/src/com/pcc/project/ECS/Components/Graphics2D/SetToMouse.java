package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class SetToMouse extends Component {
    public static String defaultName = "setToMouse";

    protected boolean catchMouse = false;

    protected Transform transform;

    protected Renderer2D renderer;

    public boolean getCatchMouse () {
        return this.catchMouse;
    }

    public SetToMouse setCatchMouse ( boolean catchMouse ) {
        this.catchMouse = catchMouse;

        return this;
    }

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
    public void onEnable () {
        super.onEnable();

        if ( this.catchMouse ) {
            Gdx.input.setCursorCatched( true );
            Gdx.input.setCursorPosition( Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() );
        }
    }

    @Override
    public void onDisable () {
        super.onDisable();

        if ( this.catchMouse ) {
            Gdx.input.setCursorCatched( false );
        }
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        Camera camera = this.renderer.getCamera();

        if ( camera != null ) {
            Vector2 screen = new Vector2( Gdx.input.getX(), Gdx.input.getY() );

            Vector2 local = camera.screenToLocal( screen, this.entity.parent );

            this.transform.setPosition( local.x, local.y );
        }
    }
}
