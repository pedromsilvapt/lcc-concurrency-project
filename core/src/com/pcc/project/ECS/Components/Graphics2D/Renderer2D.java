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
    class DebugRendererItem {
        public Color     color;
        public Rectangle rect;
        public Transform transform;

        public DebugRendererItem ( Color color, Rectangle rect, Transform transform ) {
            this.color = color;
            this.rect = rect;
            this.transform = transform;
        }
    }

    class DebugRenderer implements Disposable {
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

            this.items.add( new DebugRendererItem( color, rect, transform ) );
        }

        public void flushPoints ( Color color, Vector2[] points, Transform transform ) {
            Vector2 v1, v2;

            for ( int i = 1; i <= points.length; i++ ) {
                v1 = transform.localtoGlobalPoint( points[ ( i - 1 ) % points.length ] );
                v2 = transform.localtoGlobalPoint( points[ i % points.length ] );

                this.shapeRenderer.line( v1.x, v1.y, v2.x, v2.y, color, color );
            }
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
                Vector2 localPosition = item.rect.getPosition( new Vector2() );
                Vector2 localSize     = item.rect.getSize( new Vector2() );

                // Top Left
                corners[ 0 ].set( localPosition.x, localPosition.y );
                // Top Right
                corners[ 1 ].set( localPosition.x + localSize.x, localPosition.y );
                // Bottom Right
                corners[ 2 ].set( localPosition.x + localSize.x, localPosition.y + localSize.y );
                // Bottom Left
                corners[ 3 ].set( localPosition.x, localPosition.y + localSize.y );

                this.flushPoints( item.color, corners, item.transform );
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

    public SpriteBatch spriteBatch;

    public DebugRenderer debugRenderer;

    public int width = 0;

    public int height = 0;

    public Renderer2D ( Entity entity, String name ) {
        super( entity, name );
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
        this.debugRenderer = new DebugRenderer( true );
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

        this.spriteBatch.begin();
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
