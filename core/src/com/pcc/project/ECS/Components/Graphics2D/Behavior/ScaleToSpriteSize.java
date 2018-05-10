package com.pcc.project.ECS.Components.Graphics2D.Behavior;

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

    /* Component Dependencies */

    protected Transform transform;

    @Override
    public void onAwake () {
        super.onAwake();

        this.transform = this.entity.getComponent( Transform.class );
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        if ( targetSprite != null && this.targetSize != null ) {
            Texture texture = targetSprite.getTexture();

            if ( texture != null ) {
                Vector2 scale =  this.targetSize.cpy().scl( texture.getWidth(), texture.getHeight() );

                this.transform.setScale( scale );
            }
        }
    }
}
