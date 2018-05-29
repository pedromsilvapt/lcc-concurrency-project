package com.pcc.project.ECS.Components.Graphics2D.Behavior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;

public class ScaleToSpriteSize extends Component {
    public ScaleToSpriteSize ( Entity entity, String name ) {
        super( entity, name );
    }

    protected Sprite targetSprite;

    protected Vector2 targetSize;

    protected boolean keepAspectRatio = false;

    /* Component Dependencies */

    protected Transform transform;

    public boolean getKeepAspectRation () {
        return this.keepAspectRatio;
    }

    public ScaleToSpriteSize setKeepAspectRatio ( boolean keepAspectRatio ) {
        this.keepAspectRatio = keepAspectRatio;

        return this;
    }

    public Vector2 getTargetSize () {
        return targetSize;
    }

    public ScaleToSpriteSize setTargetSize ( Vector2 targetSize ) {
        this.targetSize = targetSize;

        return this;
    }

    public Sprite getTargetSprite () {
        return targetSprite;
    }

    public ScaleToSpriteSize setTargetSprite ( Sprite targetSprite ) {
        this.targetSprite = targetSprite;

        return this;
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.transform = this.entity.getComponent( Transform.class );

        if ( this.targetSprite == null ) {
            this.targetSprite = this.entity.getComponent( Sprite.class );
        }
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        if ( targetSprite != null && this.targetSize != null ) {
            Texture texture = targetSprite.getTexture();

            if ( texture != null ) {
                Vector2 scale =  new Vector2( targetSize.x / texture.getWidth(), targetSize.y / texture.getHeight() );

                this.transform.setScale( scale );
            }
        }
    }
}
