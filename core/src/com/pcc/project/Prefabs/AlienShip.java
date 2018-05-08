package com.pcc.project.Prefabs;

import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Components.Graphics2D.SetToMouse;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;

public class AlienShip extends Prefab<Entity> {
    public enum ShipColor {
        Blue, Green, Red, Yellow
    }

    protected String name;

    protected ShipColor color;

    public AlienShip ( String name, ShipColor color ) {
        this.name = name;
        this.color = color;
    }

    public String getShipColorCode () {
        switch ( this.color ) {
            case Green: return "Green";
            case Blue: return "Blue";
            case Red: return "Red";
            case Yellow: return "Yellow";
            default: return "Red";
        }
    }

    public String getShipAssetName () {
        return String.format( "ufo%s", this.getShipColorCode() );
    }

    @Override
    public Entity instantiate () {
        Entity ship = new GameObject( this.name ).instantiate();
        ship.addComponent( SetToMouse.class, "setToMouse" ).setEnabled( false );

        /* HULL */
        Entity hullEntity = ship.instantiate( new GameObject( "sprite" ) );
        hullEntity.getComponent( Transform.class )
                .setPosition( 0, -5 );

        hullEntity.addComponent( Sprite.class, "sprite" )
                .setTexturePath( String.format( "spaceshooter/PNG/%s.png", this.getShipAssetName() ) )
                .setAnchor( Sprite.Anchor.Center );

        return ship;
    }
}
