package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.Graphics2D.Camera;
import com.pcc.project.ECS.Components.Graphics2D.Renderer2D;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;

public class Control extends Component {
    public static class Size {
        public int width;
        public int height;

        public Size ( int width, int height ) {
            this.width = width;
            this.height = height;
        }

        public Size () {
            this( 0, 0 );
        }

        public Vector2 toVector2 () {
            return new Vector2( this.width, this.height );
        }
    }

    public static class Rect {
        public float x, y;
        public float width, height;

        public Rect ( float x, float y, float width, float height ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Rect ( Size pos, Size size ) {
            this( pos.width, pos.height, size.width, size.height );
        }

        public Rect ( Vector2 pos, Vector2 size ) {
            this( pos.x, pos.y, size.x, size.y );
        }

        public boolean isWithin ( Vector2 point ) {
            boolean horizontal = point.x >= this.x && point.x <= this.x + this.width;
            boolean vertical = point.y >= this.y && point.y <= this.y + this.height;

            return horizontal && vertical;
        }

        public String toString () {
            return String.format( "%f %f %f %f", this.x, this.y, this.width, this.height );
        }
    }

    protected Size size = new Size( 49, 49 );

    protected Sprite.Anchor anchor = Sprite.Anchor.BottomLeft;

    protected Transform transform;

    protected Renderer2D renderer;

    public Control ( Entity entity, String name ) {
        super( entity, name );
    }

    public Size getSize () {
        return this.size;
    }

    public Control setSize ( Size size ) {
        this.size = size;

        this.onResize();

        return this;
    }

    public Sprite.Anchor getAnchor () {
        return this.anchor;
    }

    public Control setAnchor ( Sprite.Anchor anchor ) {
        this.anchor = anchor;

        return this;
    }

    public Control setSize ( int x, int y ) {
        return this.setSize( new Size( x, y ) );
    }

    public Rect getBoundingBox () {
        // TODO should take into account the control's anchor.
        // Right now it assumes that the anchor is always BottomLeft

        Size size = this.getSize();

        Vector2 pos = this.transform.getGlobalPosition();
        Vector2 scale = this.transform.getGlobalScale();

        return new Rect( pos.x, pos.y, size.width * scale.x, size.height * scale.y );
    }

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
