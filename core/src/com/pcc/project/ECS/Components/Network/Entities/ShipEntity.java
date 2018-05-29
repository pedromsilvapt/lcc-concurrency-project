package com.pcc.project.ECS.Components.Network.Entities;

import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Components.GameLogic.Player;
import com.pcc.project.ECS.Components.GameLogic.Ship;
import com.pcc.project.ECS.Components.GameLogic.User;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.Network.NetworkGameMaster;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.PlayerShip;

import java.util.Map;

class ShipEntityPrefab extends Prefab< Entity > {
    protected String username;

    protected String entityType;

    protected String entityId;

    boolean _isPlayer;

    float acceleration;

    float energyCapacity;

    Vector2 size;

    public ShipEntityPrefab ( String entityType, String entityId, String username, boolean isPlayer, Vector2 size, float energyCapacity, float acceleration ) {
        this.username = username;
        this.entityId = entityId;
        this.entityType = entityType;
        this._isPlayer = isPlayer;
        this.acceleration = acceleration;
        this.energyCapacity = energyCapacity;
        this.size = size;
    }

    public String getName () {
        return username;
    }

    public PlayerShip.ShipColor getColor () {
        return this.isPlayer() ? PlayerShip.ShipColor.Blue : PlayerShip.ShipColor.Red;
    }

    public boolean isPlayer () {
        return this._isPlayer;
    }

    @Override
    public Entity instantiate () {
        Entity ship = new PlayerShip( this.getName(), this.getColor(), this.isPlayer() )
                .setEnergyCapacity( this.energyCapacity )
                .setAcceleration( this.acceleration )
                .setSize( this.size )
                .instantiate();

        ship.addComponent( ShipEntity.class )
                .setEntityId( this.entityId )
                .setEntityType( this.entityType );

        return ship;
    }
}

public class ShipEntity extends NetworkEntity {
    public static Prefab< Entity > createPrefab ( NetworkGameMaster gameMaster, Map< String, String > message ) {
        User user = new User( null, null )
                .setUsername( message.get( "isPlayer" ) );

        boolean isPlayer = gameMaster.getSession().equals( user );

        Vector2 size = new Vector2( Float.parseFloat( message.get( "size.width" ) ), Float.parseFloat( message.get( "size.height" ) ) );

        float energyCapacity = Float.parseFloat( message.get( "energy.total" ) );

        float acceleration = Float.parseFloat( message.get( "engine.power" ) );

        return new ShipEntityPrefab( message.get( "entity" ), message.get( "id" ), user.getUsername(), isPlayer, size, energyCapacity, acceleration );

//        return null;
    }

    public ShipEntity ( com.pcc.project.ECS.Entity entity, String name ) {
        super( entity, name );
    }

    protected boolean mainEngine  = false;
    protected boolean leftEngine  = false;
    protected boolean rightEngine = false;

    protected Transform transform;

    protected Ship ship;

    protected Player player;

    protected NetworkGameMaster gameMaster;

    @Override
    public void onAwake () {
        super.onAwake();

        this.transform = this.entity.getComponent( Transform.class );
        this.ship = this.entity.getComponent( Ship.class );
        this.player = this.entity.getComponent( Player.class );
        this.gameMaster = this.entity.getComponentInParent( NetworkGameMaster.class );
    }

    @Override
    public void onUpdate () {
        super.onAfterUpdate();

        boolean mainEngine  = this.ship.getMainThrusterState() == Ship.Thruster.On;
        boolean leftEngine  = this.ship.getLeftThrusterState() == Ship.Thruster.On;
        boolean rightEngine = this.ship.getRightThrusterState() == Ship.Thruster.On;

        if ( mainEngine != this.mainEngine || leftEngine != this.leftEngine || rightEngine != this.rightEngine ) {
            this.mainEngine = mainEngine;
            this.leftEngine = leftEngine;
            this.rightEngine = rightEngine;

            this.gameMaster.commandPlayerStateChange( mainEngine, leftEngine, rightEngine );
        }
    }

    @Override
    public void obey ( Map< String, String > entity ) {
        if ( this.transform == null ) {
            return;
        }

        this.transform.setGlobalPosition( new Vector2( Float.parseFloat( entity.get( "pos.x" ) ), Float.parseFloat( entity.get( "pos.y" ) ) ) );

        this.transform.setGlobalRotation( Float.parseFloat( entity.get( "rot" ) ) );

        this.ship.setEnergyAmount( Float.parseFloat( entity.get( "energy.current" ) ) );

        this.ship.setEnergyCapacity( Float.parseFloat( entity.get( "energy.total" ) ) );

        this.ship.getMainThrusterVelocity().setAcceleration( Float.parseFloat( entity.get( "engine.power" ) ) );

        Vector2 globalVelocity = new Vector2( Float.parseFloat( "forwardVelocity.x" ), Float.parseFloat( "forwardVelocity.y" ) );

        Vector2 velocity = this.transform.globalToLocalVector( globalVelocity );

        this.ship.getMainThrusterVelocity().setVelocity( velocity );
    }
}
