package com.pcc.project.Prefabs.GUI;

import com.pcc.project.ECS.Components.GameLogic.Ship;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.LifecycleHooks;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GameObject;

public class PlayerHud extends Prefab<Entity> {
    protected Ship playerShip;

    public PlayerHud ( Ship playerShip ) {
        this.playerShip = playerShip;
    }

    public float getEnergyBarWidth () {
        return playerShip.getEnergy().getAmount() * 2 + 18;
    }

    @Override
    public Entity instantiate () {
        Entity hud = new GameObject( "hud" ).instantiate();

        Entity energySprite = hud.instantiate( new GameObject( "energySprite" ) );

        energySprite.addComponent( Sprite.class )
                .setTexturePath( "spaceshooter/PNG/Power-ups/bolt_gold.png" );

        energySprite.getComponent( Transform.class )
                .setPosition( 20, 20 );

        Entity energyBar = hud.instantiate( new GameObject( "energyBar" ) );

        energyBar.addComponent( Sprite.class )
                .setNinePatchConfig( 9, 9, 13, 13 )
                .setTexturePath( "uipack-space/PNG/squareYellow.png" )
                .setSize( this.getEnergyBarWidth(), 26 );

        energyBar.getComponent( Transform.class )
                .setPosition( 50, 23 );

        energyBar.addComponent( LifecycleHooks.class )
            .setOnUpdate( e -> energyBar.getComponent( Sprite.class ).setSize( this.getEnergyBarWidth(), 26 ) );

        return hud;
    }
}
