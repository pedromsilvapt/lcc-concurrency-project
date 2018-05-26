package com.pcc.project.ECS.Components.Graphics2D.Primitive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Components.Graphics2D.Renderer2D;
import com.pcc.project.ECS.Components.Graphics2D.VisualComponent;
import com.pcc.project.ECS.Entity;

public abstract class Primitive extends VisualComponent {
    protected Color color = new Color( 0, 0, 0, 1 );

    protected Renderer2D renderer;

    public Primitive ( Entity entity, String name ) {
        super( entity, name );
    }

    public Color getColor () {
        return color;
    }

    public Primitive setColor ( Color color ) {
        this.color = new Color( color );

        return this;
    }

    public Primitive setColor ( Color color, float alpha ) {
        this.setColor( color );

        this.color.a = alpha;

        return this;
    }

    public abstract float[] getVertices ();

    @Override
    public void onAwake () {
        super.onAwake();

        this.renderer = this.entity.getComponentInParent( Renderer2D.class );
    }

    @Override
    public void onDraw () {
        super.onDraw();

        Vector2 origin   = this.getAnchorPosition();
        Vector2 position = this.transform.getGlobalPosition();
        Vector2 scale    = this.transform.getGlobalScale();
        float   rotation = this.transform.getGlobalRotation();

        EarClippingTriangulator triangulator = new EarClippingTriangulator();

        float[]    vertices = this.getVertices();
        short[] indexes  = triangulator.computeTriangles( vertices ).toArray();

        // Creating the color filling (but textures would work the same way)
        Pixmap pix = new Pixmap( 1, 1, Pixmap.Format.RGBA8888 );
        pix.setColor( this.getColor() ); // DE is red, AD is green and BE is blue.
        pix.fill();
        Texture textureSolid = new Texture( pix );
        PolygonRegion polyReg = new PolygonRegion( new TextureRegion( textureSolid ), vertices, indexes );

        PolygonSprite sprite = new PolygonSprite( polyReg );

        sprite.setOrigin( origin.x, origin.y );
        sprite.setRotation( rotation );
        sprite.setScale( scale.x, scale.y );

        this.renderer.setMode( Renderer2D.RendererMode.Shapes );

        this.renderer.polygonsBatch.draw( polyReg, position.x, position.y );
    }
}
