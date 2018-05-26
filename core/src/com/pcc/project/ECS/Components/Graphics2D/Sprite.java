package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Components.AssetsLoader.AssetsLoader;
import com.pcc.project.ECS.Entity;

public class Sprite extends TexturedComponent {
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

    protected Color color = new Color( 1, 1, 1, 1 );

    protected PatchConfig patchConfig = null;

    /* Component Dependencies */

    protected Renderer2D renderer;

    protected AssetsLoader assetsLoader;

    public Color getColor () {
        return color;
    }

    public Sprite setColor ( Color color ) {
        this.color = color;

        return this;
    }

    public float getOpacity () {
        return this.color.a;
    }

    public Sprite setOpacity ( float opacity ) {
        this.color.a = opacity;

        return this;
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

    public Sprite ( Entity entity, String name ) {
        super( entity, name );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.renderer = this.entity.getComponentInParent( Renderer2D.class );
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

            this.renderer.debugRenderer.drawPoint( Color.BLUE, this.transform.getPosition(), this.transform );

            this.renderer.debugRenderer.drawVector( Color.RED, this.getAnchorPosition(), this.transform.getForward().scl( 30 ), this.transform );

            this.renderer.setMode( Renderer2D.RendererMode.Sprite );

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
