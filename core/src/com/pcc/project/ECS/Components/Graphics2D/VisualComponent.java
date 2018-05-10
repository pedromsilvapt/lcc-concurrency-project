package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Control;
import com.pcc.project.ECS.Entity;

public class VisualComponent extends Component {
    public static class Size {
        public float width;
        public float height;

        public Size () {
            this( 0, 0 );
        }

        public Size ( Vector2 vector2 ) {
            this( vector2.x, vector2.y );
        }

        public Size ( float width, float height ) {
            this.width = width;
            this.height = height;
        }

        public Vector2 toVector2 () {
            return new Vector2( this.width, this.height );
        }
    }

//    public static class Rect {
//        public float x, y;
//        public float width, height;
//
//        public Rect ( float x, float y, float width, float height ) {
//            this.x = x;
//            this.y = y;
//            this.width = width;
//            this.height = height;
//        }
//
//        public Rect ( Control.Size pos, Control.Size size ) {
//            this( pos.width, pos.height, size.width, size.height );
//        }
//
//        public Rect ( Vector2 pos, Vector2 size ) {
//            this( pos.x, pos.y, size.x, size.y );
//        }
//
//        public boolean isWithin ( Vector2 point ) {
//            boolean horizontal = point.x >= this.x && point.x <= this.x + this.width;
//            boolean vertical   = point.y >= this.y && point.y <= this.y + this.height;
//
//            return horizontal && vertical;
//        }
//
//        public String toString () {
//            return String.format( "%f %f %f %f", this.x, this.y, this.width, this.height );
//        }
//    }

    protected int align = Align.bottomLeft;

    protected Size size;

    /* Component Dependencies */
    protected Transform transform;

    public VisualComponent ( Entity entity, String name ) {
        super( entity, name );
    }

    public int getAlign () {
        return this.align;
    }

    public VisualComponent setAlign ( int align ) {
        this.align = align;

        return this;
    }

    public Size getSize () {
        return this.size;
    }

    public VisualComponent setSize ( Size size ) {
        this.size = size;

        return this;
    }

    public VisualComponent setSize ( float width, float height ) {
        return this.setSize( new Size( width, height ) );
    }

    private Vector2 getAlignPosition () {
        Size size = this.getSize() == null ? new Size() : this.getSize();

        float w = size.width;
        float h = size.height;

        if ( this.align == Align.center ) {
            return new Vector2( w / 2, h / 2 );
        } else if ( this.align == Align.left ) {
            return new Vector2( 0, h / 2 );
        } else if ( this.align == Align.right ) {
            return new Vector2( w, h / 2 );
        } else if ( this.align == Align.top ) {
            return new Vector2( w / 2, h );
        } else if ( this.align == Align.bottom ) {
            return new Vector2( w / 2, 0 );
        } else if ( this.align == Align.bottomLeft ) {
            return new Vector2( 0, 0 );
        } else if ( this.align == Align.bottomLeft ) {
            return new Vector2( w, 0 );
        } else if ( this.align == Align.topLeft ) {
            return new Vector2( 0, h );
        } else if ( this.align == Align.topRight ) {
            return new Vector2( w, h );
        } else {
            return new Vector2( 0, 0 );
        }
    }

    public Vector2 getAnchorPosition () {
        return this.getAlignPosition().scl( -1 );
    }

    public Vector2 getGlobalAnchorPosition () {
        return this.transform.localtoGlobalPoint( this.getAnchorPosition() );
    }

    public Rectangle getRectangle () {
        Size size = this.getSize() == null ? new Size( 0, 0 ) : this.getSize();

        Vector2 anchor = this.getAnchorPosition();

        return new Rectangle( anchor.x, anchor.y, size.width, size.height );
    }

    public Rectangle getGlobalRectangle () {
        Vector2 size = this.transform.localToGlobalVector(
                this.getSize() == null ? new Vector2( 0, 0 ) : this.getSize().toVector2()
        );

        Vector2 anchor = this.getGlobalAnchorPosition();

        return new Rectangle( anchor.x, anchor.y, size.x, size.y );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.transform = this.entity.getComponent( Transform.class );
    }
}
