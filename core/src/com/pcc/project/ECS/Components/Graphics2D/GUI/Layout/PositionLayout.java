package com.pcc.project.ECS.Components.Graphics2D.GUI.Layout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.Graphics2D.Camera;
import com.pcc.project.ECS.Components.Graphics2D.Renderer2D;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.Graphics2D.VisualComponent;
import com.pcc.project.ECS.Entity;

public class PositionLayout extends Component {
    protected int align;

    protected Vector2 size;

    protected Rectangle margin = new Rectangle( 0, 0, 0, 0 );

    protected VisualComponent visual;

    protected VisualComponent frame;

    protected Transform transform;

    protected Renderer2D renderer;

    protected Transform containerTransform;

    public PositionLayout ( Entity entity, String name ) {
        super( entity, name );
    }

    public Vector2 getSize () {
        return size;
    }

    public PositionLayout setSize ( Vector2 size ) {
        this.size = size;

        return this;
    }

    public Rectangle getMargin () {
        return margin;
    }

    public PositionLayout setMargin ( Rectangle margin ) {
        this.margin = margin;

        return this;
    }

    public PositionLayout setMargin ( float left, float right, float top, float bottom ) {
        return this.setMargin( new Rectangle( left, top, right, bottom ) );
    }

    public PositionLayout setMargin ( Vector2 margin ) {
        return this.setMargin( margin.x, margin.x, margin.y, margin.y );
    }

    public PositionLayout setMargin ( float horizontal, float vertical ) {
        return this.setMargin( horizontal, horizontal, vertical, vertical );
    }

    public PositionLayout setSize ( float width, float height ) {
        return this.setSize( new Vector2( width, height ) );
    }

    public VisualComponent getFrame () {
        return frame;
    }

    public PositionLayout setFrame ( VisualComponent frame ) {
        this.frame = frame;

        return this;
    }

    public int getAlign () {
        return align;
    }

    public PositionLayout setAlign ( int align ) {
        this.align = align;

        return this;
    }

    public VisualComponent getVisual () {
        return visual;
    }

    public PositionLayout setVisual ( VisualComponent visual ) {
        this.visual = visual;

        return this;
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.renderer = this.entity.getComponentInParent( Renderer2D.class );

        this.transform = this.entity.getComponentInParent( Transform.class );

        this.containerTransform = this.transform.getParentTransform();

        if ( this.visual == null ) {
            this.setVisual( this.entity.getComponent( VisualComponent.class ) );
        }
    }

    protected Vector2 getFrameSize () {
        if ( this.frame != null ) {
            Transform transform = this.frame.getTransform();

            Vector2 localSize = this.frame.getSize().toVector2();

            return transform.localToGlobalVector( localSize );
        }

        Camera camera = this.renderer.getCamera();

        Vector2 bottomRight = camera.screenToGlobal( new Vector2( Gdx.graphics.getWidth(), 0 ) );
        Vector2 topLeft     = camera.screenToGlobal( new Vector2( 0, Gdx.graphics.getHeight() ) );

        return bottomRight.sub( topLeft );
    }

    protected int getVisualAlign () {
        if ( this.size != null ) {
            return Align.bottomLeft;
        }

        if ( this.visual != null ) {
            return this.visual.getAlign();
        }

        return Align.center;
    }

    protected Vector2 getVisualSize () {
        if ( this.size != null ) {
            return this.transform.localToGlobalVector( this.size );
        }

        if ( this.visual != null ) {
            Transform transform = this.visual.entity.getComponent( Transform.class );

            VisualComponent.Size size = this.visual.getSize();

            if ( size == null ) {
                return new Vector2( 0, 0 );
            }

            if ( transform != null ) {
                return transform.localToGlobalVector( size.toVector2() );
            }
        }

        return new Vector2( 0, 0 );
    }

    public Rectangle getRectangle ( Vector2 origin, Vector2 size ) {
        return new Rectangle( origin.x, origin.y, size.x, size.y );
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        this.containerTransform.invalidate();

        Rectangle globalMargin = this.containerTransform.localToGlobalRectangle( margin );

        Vector2 size = this.getVisualSize();

        Vector2 frameSize = this.getFrameSize().sub( new Vector2( globalMargin.x + globalMargin.width, globalMargin.y + globalMargin.height ) );

        Vector2 origin = VisualComponent.getOriginPoint( this.getAlign(), size );

        Vector2 frameOrigin = VisualComponent.getOriginPoint( this.getAlign(), frameSize );

        Rectangle visual = this.getRectangle( origin.cpy().scl( -1 ), size );

        Rectangle frame = this.getRectangle( frameOrigin, frameSize );

        Vector2 pos = new Vector2( frame.x + globalMargin.x + visual.x, frame.y + globalMargin.y + visual.y );

        if ( this.frame != null ) {
            pos.add( this.frame.getTransform().localToGlobalPoint( new Vector2( 0, 0 ) ).x, 0 );
        }

        this.renderer.debugRenderer.drawPoint( Color.BLUE, this.containerTransform.globalToLocalPoint( origin ), this.containerTransform );

        this.renderer.debugRenderer.drawPoint( Color.BLUE, this.containerTransform.globalToLocalPoint( frameOrigin ), this.containerTransform );

        Vector2 localPosition = this.containerTransform.globalToLocalPoint( pos );

        transform.setPosition( localPosition );
    }
}
