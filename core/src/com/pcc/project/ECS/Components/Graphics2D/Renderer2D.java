package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

import java.util.ArrayList;

public class Renderer2D extends Component {
    public abstract class DebugRendererItem {
        public Color     color;
        public Transform transform;

        public DebugRendererItem ( Color color, Transform transform ) {
            this.color = color;
            this.transform = transform;
        }

        public void renderShape ( ShapeRenderer renderer, Vector2[] points ) {
            Vector2 v1, v2;

            for ( int i = 1; i <= points.length; i++ ) {
                v1 = transform.localtoGlobalPoint( points[ ( i - 1 ) % points.length ] );
                v2 = transform.localtoGlobalPoint( points[ i % points.length ] );

                renderer.line( v1.x, v1.y, v2.x, v2.y, color, color );
            }
        }

        public abstract void render ( ShapeRenderer renderer );
    }

    public class DebugRendererRectangle extends DebugRendererItem {
        public Rectangle rectangle;

        public DebugRendererRectangle ( Color color, Rectangle rectangle, Transform transform ) {
            super( color, transform );

            this.rectangle = rectangle;
        }

        @Override
        public void render ( ShapeRenderer renderer ) {
            Vector2 localPosition = rectangle.getPosition( new Vector2() );
            Vector2 localSize     = rectangle.getSize( new Vector2() );

            // Top Left
            Vector2 tl = new Vector2( localPosition.x, localPosition.y );
            // Top Right
            Vector2 tr = new Vector2( localPosition.x + localSize.x, localPosition.y );
            // Bottom Right
            Vector2 br = new Vector2( localPosition.x + localSize.x, localPosition.y + localSize.y );
            // Bottom Left
            Vector2 bl = new Vector2( localPosition.x, localPosition.y + localSize.y );

            this.renderShape( renderer, new Vector2[] { tl, tr, br, bl } );
        }
    }

    class DebugRendererPoint extends DebugRendererItem {
        Vector2 point;

        public DebugRendererPoint ( Color color, Vector2 point, Transform transform ) {
            super( color, transform );

            this.point = point;
        }

        @Override
        public void render ( ShapeRenderer renderer ) {
            Vector2 scale = this.transform.getGlobalScale();

            Vector2 size = scale.cpy().scl( 10 );

            this.renderShape( renderer, new Vector2[] {
                    new Vector2( this.point.x, this.point.y - size.y ),
                    new Vector2( this.point.x, this.point.y + size.y )
            } );

            this.renderShape( renderer, new Vector2[] {
                    new Vector2( this.point.x - size.x, this.point.y ),
                    new Vector2( this.point.x + size.x, this.point.y )
            } );
        }
    }

    class DebugRendererVector extends DebugRendererItem {
        Vector2 origin;
        Vector2 vector;

        public DebugRendererVector ( Color color, Vector2 origin, Vector2 vector, Transform transform ) {
            super( color, transform );

            this.origin = origin;
            this.vector = vector;
        }

        @Override
        public void render ( ShapeRenderer renderer ) {
            Vector2 scale = this.transform.getGlobalScale();

            Vector2 tip = this.origin.cpy().add( this.vector );

            Vector2 tipLeft = tip.cpy().add(
                    tip.cpy().sub(this.origin).nor().rotate( 180 - 45 )
                            .scl( scale )
                            .scl( 20 )
            );

            Vector2 tipRight = tip.cpy().add(
                    tip.cpy().sub(this.origin).nor().rotate( 180 + 45 )
                            .scl( scale )
                            .scl( 20 )
            );

            this.renderShape( renderer, new Vector2[] {
                    this.origin.cpy(),
                    tip.cpy()
            } );

            this.renderShape( renderer, new Vector2[] {
                    tip.cpy(),
                    tipLeft
            } );

            this.renderShape( renderer, new Vector2[] {
                    tip.cpy(),
                    tipRight
            } );
        }
    }

    public class DebugRenderer implements Disposable {
        boolean enabled = false;

        ShapeRenderer shapeRenderer = new ShapeRenderer();

        ArrayList< DebugRendererItem > items = new ArrayList<>();

        public DebugRenderer ( boolean enabled ) {
            this.enabled = enabled;
        }

        public void draw ( Rectangle rect, Transform transform ) {
            this.draw( Color.GREEN, rect, transform );
        }

        public void draw ( Color color, Rectangle rect, Transform transform ) {
            if ( !enabled ) {
                return;
            }

            this.items.add( new DebugRendererRectangle( color, rect, transform ) );
        }

        public void drawPoint ( Color color, Vector2 point, Transform transform ) {
            if ( !enabled ) {
                return;
            }

            this.items.add( new DebugRendererPoint( color, point, transform ) );
        }

        public void drawVector ( Color color, Vector2 origin, Vector2 vector, Transform transform ) {
            if ( !enabled ) {
                return;
            }

            this.items.add( new DebugRendererVector( color, origin, vector, transform ) );
        }

        public void flush ( Camera camera ) {
            if ( !this.enabled && this.items.size() > 0 ) {
                this.items.clear();
            }

            if ( !this.enabled ) {
                return;
            }

            Vector2[] corners = new Vector2[ 4 ];

            for ( int i = 0; i < 4; i++ ) corners[ i ] = new Vector2();

            this.shapeRenderer.setAutoShapeType( true );
            this.shapeRenderer.begin();
            this.shapeRenderer.setProjectionMatrix( camera.getInternalCamera().combined );

            for ( DebugRendererItem item : this.items ) {
                item.render( shapeRenderer );
            }

            this.shapeRenderer.end();

            this.items.clear();
        }

        @Override
        public void dispose () {
            this.shapeRenderer.dispose();
            this.items.clear();
        }
    }

    public static String defaultName = "renderer2D";

    public int toggleDebugKey = 0;

    public SpriteBatch spriteBatch;

    public DebugRenderer debugRenderer;

    public boolean enableDebug;

    public int width = 0;

    public int height = 0;

    public Renderer2D ( Entity entity, String name ) {
        super( entity, name );
    }

    public boolean getEnableDebug () {
        return this.enableDebug;
    }

    public Renderer2D setEnableDebug ( boolean enableDebug ) {
        this.enableDebug = enableDebug;

        return this;
    }

    public int getDebugKey () {
        return this.toggleDebugKey;
    }

    public Renderer2D setDebugKey ( int key ) {
        this.toggleDebugKey = key;

        return this;
    }

    public Camera getCamera () {
        return this.entity.getComponentsInChildren( Camera.class )
                .stream()
                .filter( Camera::getEnabled )
                .findFirst()
                .orElse( null );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.spriteBatch = new SpriteBatch();
        this.debugRenderer = new DebugRenderer( this.enableDebug );
    }

    @Override
    public void onDraw () {
        super.onDraw();

        int width  = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        boolean resized = width != this.width || height != this.height;

        if ( resized ) {
            this.width = width;
            this.height = height;
        }

        Camera camera = this.getCamera();

        if ( camera != null ) {
            if ( resized ) {
                camera.viewport.update( this.width, this.height, true );
            }

            camera.viewport.apply();

            this.spriteBatch.setProjectionMatrix( camera.getCam().combined );
        }

        if ( this.spriteBatch.isDrawing() ) {
            this.spriteBatch.end();
        }

        this.spriteBatch.begin();

        if ( this.toggleDebugKey > 0 && Gdx.input.isKeyJustPressed( this.toggleDebugKey ) ) {
            this.debugRenderer.enabled = !this.debugRenderer.enabled;
        }
    }

    @Override
    public void onAfterDraw () {
        super.onAfterDraw();

        this.spriteBatch.end();
        this.spriteBatch.flush();

        this.debugRenderer.flush( this.getCamera() );
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        this.debugRenderer.dispose();

        if ( this.spriteBatch != null ) {
            this.spriteBatch.dispose();
        }
    }
}
