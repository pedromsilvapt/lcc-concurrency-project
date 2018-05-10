package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

import java.util.ArrayList;
import java.util.List;

public class BackgroundSprite extends TexturedComponent {
    public List<Sprite> tiles = new ArrayList<>(  );

    public BackgroundSprite ( Entity entity, String name ) {
        super( entity, name );
    }

    protected void calculateTiles () {
        if ( this.getTexture() == null ) {
            return;
        }

        for ( Sprite tile : this.tiles ) {
            tile.destroy();
        }

        this.tiles.clear();

        Texture texture = this.getTexture();

        Vector2 canvasSize = this.getSize().toVector2();
        Vector2 tileSize = new Vector2( texture.getWidth(), texture.getHeight() );

        int maxCols = (int)Math.ceil( canvasSize.x / tileSize.x );
        int maxRows = (int)Math.ceil( canvasSize.y / tileSize.y );

        for ( int col = 0; col < maxCols; col++ ) {
            for ( int row = 0; row < maxRows; row++ ) {
                Sprite sprite = this.entity.addComponent( Sprite.class, String.format( "sprite_tile_%d_%d", col, row ) );

                sprite.setTexturePath( this.getTexturePath() );

                sprite.setAnchor( col * tileSize.x, row * tileSize.y );

                this.tiles.add( sprite );
            }
        }
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.calculateTiles();
    }

    public void onTextureLoad () {
        this.calculateTiles();
    }
}
