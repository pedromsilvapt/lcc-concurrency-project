package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.pcc.project.ECS.Components.AssetsLoader.AssetsLoader;
import com.pcc.project.ECS.Entity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TexturedComponent extends VisualComponent {
    protected String texturePath;

    protected Texture texture;

    protected Texture.TextureWrap verticalWrap;

    protected Texture.TextureWrap horizontalWrap;

    protected AssetsLoader assetsLoader;

    protected CompletableFuture<Pixmap> futureTexture;

    public TexturedComponent ( Entity entity, String name ) {
        super( entity, name );
    }

    public String getTexturePath () {
        return this.texturePath;
    }

    public void reloadTextureFromPath () {
        if ( this.futureTexture != null ) {
            return;
        }

        if ( this.assetsLoader != null ) {
            this.futureTexture = this.assetsLoader.loadTexture( this.texturePath );
        } else {
            Texture texture = new Texture( this.texturePath );

            this.setTextureInternal( texture );
        }
    }

    private void setTextureInternal ( Texture texture ) {
        this.texture = texture;

        if ( this.texture != null ) {
            if ( this.horizontalWrap != null && this.verticalWrap != null ) {
                this.texture.setWrap( this.horizontalWrap, this.verticalWrap );
            }

            this.texture.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );

            this.onTextureLoad();
        }
    }

    public Texture getTexture () {
        if ( this.texture == null && texturePath != null ) {
            this.reloadTextureFromPath();
        }

        return texture;
    }

    public Texture getTextureSync () {
        Texture texture = this.texture;

        if ( texture == null && this.futureTexture != null ) {
            try {
                this.setTextureInternal( new Texture( this.futureTexture.get() ) );
            } catch ( InterruptedException | ExecutionException e ) {
                e.printStackTrace();
            }

            this.futureTexture = null;
        }

        return this.texture;
    }

    public TexturedComponent setTexture ( Texture texture ) {
        // If we alread have a texture loaded, but it was provided by a path
        // That means we (this instance of the class) loaded it, and therefore we own it
        // Which means we are responsible for disposing it
        if ( this.texturePath != null && this.texture != null ) {
            this.texture.dispose();
        }

        if ( this.futureTexture != null ) {
//            this.futureTexture.cancel( true );
            this.futureTexture = null;
        }

        this.texturePath = null;

        this.setTextureInternal( texture );

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

    @Override
    public void onAwake () {
        super.onAwake();

        this.assetsLoader = this.entity.getComponentInParent( AssetsLoader.class );
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        if ( this.futureTexture != null && this.futureTexture.isDone() ) {
            this.getTextureSync();
        }
    }
}
