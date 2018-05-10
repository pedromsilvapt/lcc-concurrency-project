package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Entity;

public class Sprite extends VisualComponent {
    public static String defaultName = "sprite";

    public static class PatchConfig {
        public int left, right, top, bottom;

        public PatchConfig ( int left, int right, int top, int bottom ) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }
    }

    protected String texturePath;

    protected Texture texture;

    protected Texture.TextureWrap verticalWrap;

    protected Texture.TextureWrap horizontalWrap;

    protected Color color = new Color( 1, 1, 1, 1 );

    protected PatchConfig patchConfig = null;

    /* Component Dependencies */

    protected Renderer2D renderer;

    protected Transform transform;

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

    public Size getSize () {
        if ( this.size == null && this.texture != null ) {
            return new Size( this.texture.getWidth(), this.texture.getHeight() );
        } else {
            return super.getSize();
        }
    }

    public PatchConfig getNinePatchConfig () {
        return this.patchConfig;
    }

    public Sprite setNinePatchConfig ( PatchConfig patchConfig ) {
        this.patchConfig = patchConfig;

        return this;
    }

    public Sprite setNinePatchConfig ( int left, int right, int top, int bottom ) {
        return this.setNinePatchConfig( new PatchConfig( left, right, top, bottom ) );
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
            Vector2 pos      = this.getGlobalAnchorPosition();
            Vector2 scl      = this.transform.getGlobalScale();
            float   rotation = this.transform.getGlobalRotation();

            Color color = this.renderer.spriteBatch.getColor();

            this.renderer.spriteBatch.setColor( this.getColor() );

            int width  = this.size != null ? ( int ) this.size.width : texture.getWidth();
            int height = this.size != null ? ( int ) this.size.height : texture.getHeight();

            this.renderer.debugRenderer.draw( this.getRectangle(), this.transform );

            if ( this.patchConfig != null ) {
                NinePatch patch = new NinePatch( this.texture, this.patchConfig.left, this.patchConfig.right, this.patchConfig.top, this.patchConfig.bottom );

                patch.draw( this.renderer.spriteBatch,
                        pos.x, pos.y, 0, 0,
                        width, height,
                        scl.x, scl.y, rotation );

            } else {
                this.renderer.spriteBatch.draw( texture,
                        pos.x, pos.y, 0, 0,
                        width, height,
                        scl.x, scl.y, rotation,
                        0, 0, width, height, false, false );
            }

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
