package com.pcc.project.Prefabs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.GameLogic.Game;
import com.pcc.project.ECS.Components.GameLogic.Player;
import com.pcc.project.ECS.Components.GameLogic.Ship;
import com.pcc.project.ECS.Components.Graphics2D.Behavior.ScaleToSpriteSize;
import com.pcc.project.ECS.Components.Graphics2D.Behavior.SetToMouse;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.LifecycleHooks;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GUI.PlayerHud;

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

    float acceleration;

    float energyCapacity;

    Vector2 size;

    public PlayerShip ( String name, ShipColor color ) {
        this( name, color, false );
    }

    public PlayerShip ( String name, ShipColor color, boolean isPlayer ) {
        this.name = name;

        this.color = color;

        this.isPlayer = isPlayer;
    }

    public PlayerShip setAcceleration ( float acceleration ) {
        this.acceleration = acceleration;

        return this;
    }

    public PlayerShip setEnergyCapacity ( float energyCapacity ) {
        this.energyCapacity = energyCapacity;

        return this;
    }

    public PlayerShip setSize ( Vector2 size ) {
        this.size = size;

        return this;
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
                .setTexturePath( "spaceshooter/PNG/Effects/fire12.png" )
                .setAlign( Align.center );
        engineEntity.getComponent( Transform.class )
                .setPosition( 0, -55 );


        /* Shield */
        Entity shieldEntity = ship.instantiate( new GameObject( "shield" ) );
        shieldEntity.addComponent( Sprite.class, "sprite" )
                .setOpacity( 0.4f )
                .setTexturePath( "spaceshooter/PNG/Effects/shield2.png" )
                .setAlign( Align.center );


        /* HULL */
        Entity hullEntity = ship.instantiate( new GameObject( "hull" ) );
        hullEntity.getComponent( Transform.class )
                .setPosition( 0, -5 );

        hullEntity.addComponent( Sprite.class, "sprite" )
                .setTexturePath( String.format( "spaceshooter/PNG/%s.png", this.getShipAssetName() ) )
                .setAlign( Align.center );

        /* ShipState */
        Ship shipState = ship.addComponent( Ship.class, "state" );
        shipState.mainEngineSprite = engineEntity.getComponent( "sprite" );

        if ( this.energyCapacity > 0 ) shipState.setEnergyCapacity( this.energyCapacity );
        if ( this.acceleration > 0 ) shipState.setAcceleration( this.acceleration );

        if ( this.isPlayer ) {
            ship.addComponent( Player.class, "player" );

            ship.addComponent( LifecycleHooks.class )
                    .setOnAwake( entity -> {
                        Entity gui = entity.root.getEntityInChildren( "gui" );

                        Game game = entity.getComponentInParent( Game.class );

                        if ( gui != null ) {
                            gui.instantiate( new PlayerHud( shipState, game ) );
                        }
                    } );
        }

        if ( this.size != null ) {
            ship.addComponent( ScaleToSpriteSize.class )
                .setTargetSize( this.size )
                .setTargetSprite( shieldEntity.getComponent( Sprite.class ) );
        }

        return ship;
    }
}
