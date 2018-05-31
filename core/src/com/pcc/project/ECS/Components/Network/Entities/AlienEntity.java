package com.pcc.project.ECS.Components.Network.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Components.GameLogic.User;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.Network.NetworkGameMaster;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.AlienShip;
import com.pcc.project.Prefabs.PlayerShip;

import java.util.Map;

class AlienEntityPrefab extends Prefab< Entity > {
    boolean isAllied;

    Vector2 size;

    public AlienEntityPrefab ( Vector2 size, boolean isAllied ) {
        this.isAllied = isAllied;
        this.size = size;
    }

    public AlienShip.ShipColor getColor () {
        return this.isAllied ? AlienShip.ShipColor.Green : AlienShip.ShipColor.Red;
    }

    @Override
    public Entity instantiate () {
        return new AlienShip( null, this.getColor(), size ).instantiate();
    }
}

public class AlienEntity extends NetworkEntity {
    public static Prefab< Entity > createPrefab ( NetworkGameMaster gameMaster, Map< String, String > message ) {
//        Vector2 size = new Vector2( Float.parseFloat( message.get( "size.width" ) ), Float.parseFloat( message.get( "size.height" ) ) );
        Vector2 size = new Vector2( 10, 10 );

        boolean isAllied = message.get( "type" ).equals( "ally" );

        return new AlienEntityPrefab( size, isAllied );
    }

    protected Transform transform;

    public AlienEntity ( Entity entity, String name ) {
        super( entity, name );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.transform = this.entity.getComponent( Transform.class );
    }

    @Override
    public void obey ( Map< String, String > entity ) {
        if ( this.transform == null ) {
            return;
        }

        this.transform.setGlobalPosition( new Vector2( Float.parseFloat( entity.get( "pos.x" ) ), Float.parseFloat( entity.get( "pos.y" ) ) ) );
    }
}
