package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class Sprite extends Component {
    public enum Anchor { Left, Center, Right, Top, Bottom, TopLeft, TopRight, BottomLeft, BottomRight };

    protected String texturePath;

    protected Texture texture;

    protected Anchor anchor = Anchor.Center;

    protected Texture.TextureWrap verticalWrap;

    protected Texture.TextureWrap horizontalWrap;

    /* Component Dependencies */

    protected Renderer2D renderer;

    protected Transform transform;

    protected Color color = new Color( 1, 1, 1, 1 );

    protected Vector2 customSize = null;

    public Color getColor () {
        return color;
    }

    public void setColor ( Color color ) {
        this.color = color;
    }

    public float getOpacity () {
        return this.color.a;
    }

    public void setOpacity ( float opacity ) {
        this.color.a = opacity;
    }

    public Vector2 getCustomSize () {
        return this.getCustomSize();
    }

    public Sprite setCustomSize ( Vector2 customSize ) {
        this.customSize = customSize;

        return this;
    }


    public Sprite setWrap ( Texture.TextureWrap vertical, Texture.TextureWrap horizontal ) {
        this.verticalWrap = vertical;
        this.horizontalWrap = horizontal;

        if ( this.texture != null ) {
            this.texture.setWrap( this.horizontalWrap, this.verticalWrap );
        }

        return this;
    }

    public String getTexturePath () {
        return this.texturePath;
    }

    public Texture getTexture () {
        if ( this.texture == null && texturePath != null ) {
            this.reloadTextureFromPath();
        }

        return texture;
    }

    public Sprite setTexture ( Texture texture ) {
        this.texturePath = null;

        this.texture = texture;

        return this;
    }

    public Sprite setTexturePath ( String texturePath ) {
        if ( this.texturePath == null || !this.texturePath.equals( texturePath ) ) {
            this.texturePath = texturePath;

            this.texture = null;
        }

        return this;
    }

    public Anchor getAnchor () {
        return this.anchor;
    }

    public Sprite setAnchor ( Anchor anchor ) {
        this.anchor = anchor;

        return this;
    }

    public Sprite ( Entity entity, String name ) {
        super( entity, name );
    }

    public void reloadTextureFromPath () {
        this.texture = new Texture( this.texturePath );

        if ( this.horizontalWrap != null && this.verticalWrap != null ) {
            this.texture.setWrap( this.horizontalWrap, this.verticalWrap );
        }

        this.texture.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
    }

    public Vector2 getAnchorPosition () {
        float w = this.getTexture().getWidth();
        float h = this.getTexture().getHeight();

        if ( this.anchor == Anchor.Center ) {
            return new Vector2( w / 2, h / 2 );
        } else {
            return new Vector2( 0, 0 );
        }
    }

    public Vector2 getGlobalAnchorPosition () {
        return this.transform.transformPoint( this.getAnchorPosition().scl( -1 ) );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.renderer = this.entity.getComponentInParent( Renderer2D.class );
        this.transform = this.entity.getComponent( Transform.class );
    }

    @Override
    public void onDraw () {
        super.onDraw();

        Texture texture = this.getTexture();

        if ( texture != null && this.getOpacity() > 0 ) {
            Vector2 pos = this.getGlobalAnchorPosition();
            Vector2 scl = this.transform.getGlobalScale();
            float rotation = this.transform.getGlobalRotation();

            Color color = this.renderer.spriteBatch.getColor();

            this.renderer.spriteBatch.setColor( this.getColor() );

            int width = this.customSize != null ? (int)this.customSize.x : texture.getWidth();
            int height = this.customSize != null ? (int)this.customSize.y : texture.getHeight();

            this.renderer.spriteBatch.draw( texture,
                    pos.x, pos.y, 0, 0,
                    width, height,
                    scl.x, scl.y, rotation,
                    0, 0, width, height, false, false );

            this.renderer.spriteBatch.setColor( color );
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        if ( this.texture != null ) {
            this.texture.dispose();
        }
    }
}
