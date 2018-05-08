package com.pcc.project.Prefabs;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pcc.project.ECS.Components.Graphics2D.*;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;

/**
 * This prefab is responsible for creating the global game.
 * It should set up the Network Manager
 */
public class GameWorld extends Prefab< Entity > {
    protected int worldWidth;

    protected int worldHeight;

    public GameWorld () {
        this( 1000, 1000 );
    }

    public GameWorld ( int worldWidth, int worldHeight ) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    @Override
    public Entity instantiate () {
        Entity gameWorld = new GameObject().instantiate();
        gameWorld.addComponent( Renderer2D.class, "renderer" );

        /* Camera */
        Entity cameraEntity = gameWorld.instantiate( new GameObject() );
        Camera camera = cameraEntity.addComponent( Camera.class, "camera" );
        camera.setViewport( new FitViewport( worldWidth, worldHeight, camera.getInternalCamera() ) );

        /* Board Entities */
        Entity board = gameWorld.instantiate( new GameObject() );

        board.instantiate( new PlayerShip( "player", PlayerShip.ShipColor.Blue, true ) )
            .getComponent( Transform.class ).setPosition( 100, 100 );


        board.instantiate( new PlayerShip( "enemy1", PlayerShip.ShipColor.Green ) )
            .getComponent( Transform.class ).setPosition( 300, 100 );

        board.instantiate( new PlayerShip( "enemy2", PlayerShip.ShipColor.Orange ) )
                .getComponent( Transform.class ).setPosition( 500, 100 );

        board.instantiate( new PlayerShip( "enemy3", PlayerShip.ShipColor.Red ) )
                .getComponent( Transform.class ).setPosition( 700, 100 );

        board.instantiate( new AlienShip( "alien1", AlienShip.ShipColor.Green ) )
                .getComponent( Transform.class ).setPosition( 100, 300 );

        board.instantiate( new AlienShip( "alien2", AlienShip.ShipColor.Green ) )
                .getComponent( Transform.class ).setPosition( 300, 300 );

        board.instantiate( new AlienShip( "alien3", AlienShip.ShipColor.Red ) )
                .getComponent( Transform.class ).setPosition( 500, 300 );

        board.instantiate( new AlienShip( "alien4", AlienShip.ShipColor.Red ) )
                .getComponent( Transform.class ).setPosition( 700, 300 );

        return gameWorld;
    }
}
