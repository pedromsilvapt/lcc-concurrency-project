package com.pcc.project.Prefabs;

import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.GameLogic.Player;
import com.pcc.project.ECS.Components.GameLogic.Ship;
import com.pcc.project.ECS.Components.Graphics2D.SetToMouse;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;

import java.awt.*;

public class PlayerShip extends Prefab< Entity > {
    public enum ShipColor {
        Blue,
        Green,
        Orange,
        Red
    }

    protected String name;

    protected ShipColor color;

    protected boolean isPlayer;

    public PlayerShip ( String name, ShipColor color ) {
        this( name, color, false );
    }

    public PlayerShip ( String name, ShipColor color, boolean isPlayer ) {
        this.name = name;

        this.color = color;

        this.isPlayer = isPlayer;
    }

    protected String getShipColorCode () {
        switch ( this.color ) {
            case Red: return "red";
            case Blue: return "blue";
            case Green: return "green";
            case Orange: return "orange";
            default: return "red";
        }
    }

    protected int getShipTypeCode () {
        return 1;
    }



    protected String getShipAssetName () {
        int shipType = this.getShipTypeCode();
        String shipColor = this.getShipColorCode();

        return String.format( "playerShip%d_%s", shipType, shipColor );
    }

    @Override
    public Entity instantiate () {
        Entity ship = new GameObject( this.name ).instantiate();
        ship.addComponent( SetToMouse.class, "setToMouse" ).setEnabled( false );


        /* Main Engine */
        Entity engineEntity = ship.instantiate( new GameObject( "mainEngine" ) );
        engineEntity.addComponent( Sprite.class, "sprite" )
                .setTexturePath( "spaceshooter/PNG/Effects/fire03.png" )
                .setAlign( Align.center );
        engineEntity.getComponent( Transform.class )
                .setPosition( 0, -55 );


        /* Shield */
        Entity shieldEntity = ship.instantiate( new GameObject( "shield" ) );
        shieldEntity.addComponent( Sprite.class, "sprite" )
                .setTexturePath( "spaceshooter/PNG/Effects/shield3.png" )
                .setAlign( Align.center );


        /* HULL */
        Entity hullEntity = ship.instantiate( new GameObject( "hull" ) );
        hullEntity.getComponent( Transform.class )
                .setPosition( 0, -5 );

        hullEntity.addComponent( Sprite.class, "sprite" )
                .setTexturePath( String.format( "spaceshooter/PNG/%s.png", this.getShipAssetName() ) )
                .setAlign( Align.center );

        /* ShipState */
        ship.addComponent( Ship.class, "state" );

        if ( this.isPlayer ) {
            ship.addComponent( Player.class, "player" );
        }

        return ship;
    }
}
