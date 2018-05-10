package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.graphics.Texture;
import com.pcc.project.ECS.Entity;

public class TexturedComponent extends VisualComponent {
    protected String texturePath;

    protected Texture texture;

    protected Texture.TextureWrap verticalWrap;

    protected Texture.TextureWrap horizontalWrap;

    public TexturedComponent ( Entity entity, String name ) {
        super( entity, name );
    }

    public String getTexturePath () {
        return this.texturePath;
    }

    public void reloadTextureFromPath () {
        this.texture = new Texture( this.texturePath );

        if ( this.horizontalWrap != null && this.verticalWrap != null ) {
            this.texture.setWrap( this.horizontalWrap, this.verticalWrap );
        }

        this.texture.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );

        if ( this.texture != null ) {
            this.onTextureLoad();
        }
    }

    public Texture getTexture () {
        if ( this.texture == null && texturePath != null ) {
            this.reloadTextureFromPath();
        }

        return texture;
    }

    public TexturedComponent setTexture ( Texture texture ) {
        // If we alread have a texture loaded, but it was provided by a path
        // That means we (this instance of the class) loaded it, and therefore we own it
        // Which means we are responsible for disposing it
        if ( this.texturePath != null && this.texture != null ) {
            this.texture.dispose();
        }

        this.texturePath = null;

        if ( this.texture != texture ) {
            this.texture = texture;

            this.onTextureLoad();
        }

        return this;
    }

    public TexturedComponent setTexturePath ( String texturePath ) {
        if ( this.texturePath == null || !this.texturePath.equals( texturePath ) ) {
            // Remove any texture we might already have loaded
            this.setTexture( null );

            this.texturePath = texturePath;
        }

        return this;
    }

    public Texture.TextureWrap getHorizontalWrap () {
        return horizontalWrap;
    }

    public Texture.TextureWrap getVerticalWrap () {
        return verticalWrap;
    }

    public TexturedComponent setWrap ( Texture.TextureWrap vertical, Texture.TextureWrap horizontal ) {
        this.verticalWrap = vertical;
        this.horizontalWrap = horizontal;

        if ( this.texture != null ) {
            this.texture.setWrap( this.horizontalWrap, this.verticalWrap );
        }

        return this;
    }

    public void onTextureLoad () {  }
}
