package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Components.Graphics2D.Camera;
import com.pcc.project.ECS.Components.Graphics2D.Renderer2D;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.Graphics2D.VisualComponent;
import com.pcc.project.ECS.Entity;

public class Control extends VisualComponent {
    protected Transform transform;

    protected Renderer2D renderer;

    public Control ( Entity entity, String name ) {
        super( entity, name );
    }

    public Control setSize ( Size size ) {
        super.setSize( size );

        this.onResize();

        return this;
    }

    public Control setSize ( float width, float height ) {
        return this.setSize( new Size( width, height ) );
    }

//    public Rect getBoundingBox () {
//        // TODO should take into account the control's anchor.
//        // Right now it assumes that the anchor is always BottomLeft
//
//        Size size = this.getSize();
//
//        Vector2 pos = this.transform.getGlobalPosition();
//        Vector2 scale = this.transform.getGlobalScale();
//
//        return new Rect( pos.x, pos.y, size.width * scale.x, size.height * scale.y );
//    }

    public Vector2 getGlobalPosition ( float x, float y ) {
        return this.getGlobalPosition( new Vector2( x, y ) );
    }

    public Vector2 getGlobalPosition ( Vector2 screen ) {
        Camera camera = this.renderer.getCamera();

        if ( camera != null ) {
            Vector2 worldPosition = camera.screenToGlobal( screen );

            return worldPosition;
        }

        return new Vector2( 0, 0 );
    }

    public void onResize () {  }

    @Override
    public void onAwake () {
        super.onAwake();

        this.transform = this.entity.getComponent( Transform.class );
        this.renderer = this.entity.getComponentInParent( Renderer2D.class );
    }
}
