package com.pcc.project.Prefabs;

import com.pcc.project.ECS.Components.Graphics2D.SetToMouse;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;

public class Cursor extends Prefab< Entity > {

    @Override
    public Entity instantiate () {
        Entity cursor = new GameObject().instantiate();

        cursor.addComponent( Sprite.class, "spite" )
                .setTexturePath( "uipack-space/PNG/cursor_pointer3D.png" )
                .setAnchor( Sprite.Anchor.TopLeft );

        cursor.addComponent( SetToMouse.class, "setToMouse" )
            .setCatchMouse( true );

        return cursor;
    }
}
