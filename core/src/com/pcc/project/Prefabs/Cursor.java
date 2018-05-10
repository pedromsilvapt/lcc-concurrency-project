package com.pcc.project.Prefabs;

import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.Behavior.SetToMouse;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;

public class Cursor extends Prefab< Entity > {

    @Override
    public Entity instantiate () {
        Entity cursor = new GameObject().instantiate();

        cursor.addComponent( Sprite.class, "spite" )
                .setTexturePath( "uipack-space/PNG/cursor_pointer3D.png" )
                .setAlign( Align.topLeft );

        cursor.addComponent( SetToMouse.class, "setToMouse" )
            .setCatchMouse( true );

        return cursor;
    }
}
