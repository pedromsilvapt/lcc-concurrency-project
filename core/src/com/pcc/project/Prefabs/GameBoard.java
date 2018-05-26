package com.pcc.project.Prefabs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pcc.project.ECS.Components.AssetsLoader.AssetsLoader;
import com.pcc.project.ECS.Components.GameLogic.Game;
import com.pcc.project.ECS.Components.GameLogic.Ship;
import com.pcc.project.ECS.Components.GameLogic.User;
import com.pcc.project.ECS.Components.Graphics2D.BackgroundSprite;
import com.pcc.project.ECS.Components.Graphics2D.Camera;
import com.pcc.project.ECS.Components.Graphics2D.GUI.InputManager;
import com.pcc.project.ECS.Components.Graphics2D.Renderer2D;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.LifecycleHooks;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GUI.LoginMenu;
import com.pcc.project.Prefabs.GUI.PlayerHud;

/**
 * This prefab is responsible for creating the global game.
 * It should set up the Network Manager
 */
public class GameBoard extends Prefab< Entity > {
    protected int worldWidth;

    protected int worldHeight;

    protected User player;

    protected User opponent;

    public GameBoard () {
        this( 700, 700, null, null );
    }

    public GameBoard ( int worldWidth, int worldHeight, User player, User opponent ) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.player = player;
        this.opponent = opponent;
    }

    @Override
    public Entity instantiate () {
        /* Board Entities */
        Entity board = new GameObject( "board" ).instantiate();
        board.addComponent( Renderer2D.class, "renderer" )
                .setDebugKey( Input.Keys.F9 );

        /* Camera */
        Entity cameraEntity = board.instantiate( new GameObject( "boardCamera" ) );
        Camera camera       = cameraEntity.addComponent( Camera.class, "camera" );
        camera.setViewport( new FitViewport( worldWidth, worldHeight, camera.getInternalCamera() ) );

        Game game = board.addComponent( Game.class );

        game.setPlayer( this.player );
        game.setOpponent( this.opponent );

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

        Ship playerShip = board.getEntity( "player" ).getComponent( Ship.class );

        board.addComponent( LifecycleHooks.class )
                .setOnAwake( entity -> {
                    Entity gui = entity.root.getEntityInChildren( "gui" );

                    if ( gui != null ) {
                        gui.instantiate( new PlayerHud( playerShip, game ) );
                    }
                } );

        return board;
    }
}
