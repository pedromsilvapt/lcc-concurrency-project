package com.pcc.project.Prefabs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.Behavior.ScaleToSpriteSize;
import com.pcc.project.ECS.Components.Graphics2D.Behavior.SetToMouse;
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

    protected Vector2 size;

    public AlienShip ( String name, ShipColor color ) {
        this( name, color, null );
    }

    public AlienShip ( String name, ShipColor color, Vector2 size ) {
        this.name = name;
        this.color = color;
        this.size = size;
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
                .setAlign( Align.center );

        if ( this.size != null ) {
            hullEntity.addComponent( ScaleToSpriteSize.class )
                    .setTargetSize( this.size );
        }

        return ship;
    }
}
