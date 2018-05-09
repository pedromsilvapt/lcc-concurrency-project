package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class Text extends Component {
    protected String value;

    protected BitmapFont bitmapFont;

    protected boolean needsRecache = true;

    protected GlyphLayout glyphLayout = null;

    protected boolean autoSize = true;

    protected float width;

    protected boolean wrap = false;

    protected String truncateText = "...";

    /* Component Dependencies */
    protected Transform transform;

    protected Renderer2D renderer;

    public String getValue () {
        return this.value;
    }

    public Text setValue ( String value ) {
        if ( this.value != value ) {
            this.value = value;

            this.needsRecache = true;
        }

        return this;
    }

    public boolean getAutoSize () {
        return this.autoSize;
    }

    public Text setAutoSize ( boolean autoSize ) {
        this.autoSize = autoSize;

        return this;
    }

    public float getWidth () {
        if ( this.autoSize ) {
            this.cache();

            if ( this.glyphLayout == null ) {
                return 0;
            }

            return this.glyphLayout.width;
        } else {
            return this.width;
        }
    }

    public Text setWidth ( float width ) {
        if ( this.width != width ) {
            this.width = width;

            if ( !this.autoSize ) {
                this.needsRecache = true;
            }
        }

        return this;
    }

    public float getHeight () {
        this.cache();

        if ( this.glyphLayout == null ) {
            return 0;
        }

        return this.glyphLayout.height;
    }

    public boolean getWrap () {
        return this.wrap;
    }

    public Text setWrap ( boolean wrap ) {
        if ( this.wrap != wrap ) {
            this.wrap = wrap;

            if ( !this.autoSize ) {
                this.needsRecache = true;
            }
        }

        return this;
    }

    public String getTruncateText () {
        return this.truncateText;
    }

    public Text setTruncateText ( String truncateText ) {
        if ( this.truncateText != truncateText ) {
            this.truncateText = truncateText;

            if ( !this.autoSize ) {
                this.needsRecache = true;
            }
        }

        return this;
    }

    public BitmapFont getBitmapFont () {
        return this.bitmapFont;
    }

    public Text setBitmapFont ( String fontPath ) {
        return this.setBitmapFont( new BitmapFont( Gdx.files.internal( fontPath ) ) );
    }

    public Text setBitmapFont ( BitmapFont font ) {
        if ( this.bitmapFont != font ) {
            this.bitmapFont = font;
        }

        return this;
    }

    public Text ( Entity entity, String name ) {
        super( entity, name );
    }

    public void cache () {
        if ( !this.needsRecache ) {
            return;
        }

        if ( this.getValue() == null || this.getBitmapFont() == null ) {
            this.glyphLayout = null;

            return;
        }

        if ( this.autoSize ) {
            this.glyphLayout = new GlyphLayout( this.getBitmapFont(), this.getValue() );
            this.glyphLayout.setText( this.getBitmapFont(), this.getValue(), Color.BLACK, this.glyphLayout.width, Align.center, this.wrap );
        } else {
            this.glyphLayout = new GlyphLayout( this.getBitmapFont(), this.getValue(), 0, 0, Color.WHITE, this.width, Align.center, this.wrap, this.truncateText );
        }

        this.needsRecache = false;
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.transform = this.entity.getComponent( Transform.class );
        this.renderer = this.entity.getComponentInParent( Renderer2D.class );
    }

    @Override
    public void onDraw () {
        super.onDraw();

        if ( this.needsRecache ) {
            this.cache();
        }

        // TODO BitmapFont does not seem to support scaling and rotating the image.
        // As such, before rendering the text to the screen, a more appropriate solution
        // might be to render the Text to a Texture (using Frame Buffer Objects - FBO)
        // and then render the text as a sprite.
        // If this is to be done, it should be done once in the caching phase, and then
        // here we would only draw the texture

        if ( this.glyphLayout != null ) {
            Vector2 global = this.transform.getGlobalPosition();

            this.bitmapFont.draw( this.renderer.spriteBatch, this.glyphLayout, global.x, global.y );
        }
    }
}
