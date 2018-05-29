package com.pcc.project.Prefabs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pcc.project.ECS.Components.AssetsLoader.AssetsLoader;
import com.pcc.project.ECS.Components.GameLogic.Ship;
import com.pcc.project.ECS.Components.GameLogic.User;
import com.pcc.project.ECS.Components.Graphics2D.*;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Button;
import com.pcc.project.ECS.Components.Graphics2D.GUI.InputManager;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Textbox;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Theme;
import com.pcc.project.ECS.Components.Network.NetworkGameMaster;
import com.pcc.project.ECS.Components.Network.NetworkManager;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GUI.LoginMenu;
import com.pcc.project.Prefabs.GUI.MainMenu;
import com.pcc.project.Prefabs.GUI.PlayerHud;

/**
 * This prefab is responsible for creating the global game.
 * It should set up the Network Manager
 */
public class GameWorld extends Prefab< Entity > {
    protected boolean customCursor;

    protected boolean enabledDebug;

    protected String serverAddress;

    protected int serverPort;

    public GameWorld () {
        this( false );
    }

    public GameWorld ( boolean customCursor ) {
        this.customCursor = customCursor;
    }

    public GameWorld ( boolean customCursor, String serverAddress, int serverPort ) {
        this.customCursor = customCursor;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public GameWorld setEnabledDebug ( boolean enabledDebug ) {
        this.enabledDebug = enabledDebug;

        return this;
    }

    @Override
    public Entity instantiate () {
        Entity gameWorld = new GameObject( "gameWorld" ).instantiate();

        gameWorld.addComponent( AssetsLoader.class );
        gameWorld.addComponent( NetworkManager.class ).setHost( serverAddress, serverPort );
        gameWorld.addComponent( NetworkGameMaster.class );

        gameWorld.addComponent( User.class )
            .setUsername( "Pedro" )
            .setLevel( 1 );

        /* Background */
        Entity background = gameWorld.instantiate( new GameObject( "background" ) );
        background.addComponent( Renderer2D.class, "renderer" )
            .setEnableDebug( this.enabledDebug )
            .setDebugKey( Input.Keys.F9 );
        background.instantiate( new GameObject( "camera" ), entity -> {
            entity.addComponent( Camera.class, "camera", camera -> {
                camera.setViewport( new ScreenViewport( camera.getInternalCamera() ) );
            } );
        } );
        background.addComponent( BackgroundSprite.class )
                .setTexturePath( "spaceshooter/Backgrounds/darkPurple.png" )
                .setSize( 2000, 2000 );

//        /* Board Entities */
//        Entity board = gameWorld.instantiate( new GameObject( "board" ) );
//        board.addComponent( Renderer2D.class, "renderer" )
//                .setEnableDebug( this.enabledDebug )
//                .setDebugKey( Input.Keys.F9 );
//
//        /* Camera */
//        Entity cameraEntity = board.instantiate( new GameObject( "camera" ) );
//        Camera camera = cameraEntity.addComponent( Camera.class, "camera" );
//        camera.setViewport( new FitViewport( worldWidth, worldHeight, camera.getInternalCamera() ) );


//        board.instantiate( new PlayerShip( "player", PlayerShip.ShipColor.Blue, true ) )
//            .getComponent( Transform.class ).setPosition( 100, 100 );
//
//        board.instantiate( new PlayerShip( "enemy1", PlayerShip.ShipColor.Green ) )
//            .getComponent( Transform.class ).setPosition( 300, 100 );
//
//        board.instantiate( new PlayerShip( "enemy2", PlayerShip.ShipColor.Orange ) )
//                .getComponent( Transform.class ).setPosition( 500, 100 );
//
//        board.instantiate( new PlayerShip( "enemy3", PlayerShip.ShipColor.Red ) )
//                .getComponent( Transform.class ).setPosition( 700, 100 );
//
//        board.instantiate( new AlienShip( "alien1", AlienShip.ShipColor.Green ) )
//                .getComponent( Transform.class ).setPosition( 100, 300 );
//
//        board.instantiate( new AlienShip( "alien2", AlienShip.ShipColor.Green ) )
//                .getComponent( Transform.class ).setPosition( 300, 300 );
//
//        board.instantiate( new AlienShip( "alien3", AlienShip.ShipColor.Red ) )
//                .getComponent( Transform.class ).setPosition( 500, 300 );
//
//        board.instantiate( new AlienShip( "alien4", AlienShip.ShipColor.Red ) )
//                .getComponent( Transform.class ).setPosition( 700, 300 );
//
//        Ship playerShip = board.getEntity( "player" ).getComponent( Ship.class );

        Entity gui = gameWorld.instantiate( new GameObject( "gui" ) );

        gui.addComponent( Renderer2D.class, "renderer" )
                .setEnableDebug( this.enabledDebug )
                .setDebugKey( Input.Keys.F9 );
        gui.addComponent( Camera.class, "camera", cameraGui ->  {
            cameraGui.setViewport( new ScreenViewport( cameraGui.getInternalCamera() ) );
        } );
        gui.addComponent( InputManager.class );

//        gui.instantiate( new GameObject().setPosition( 10 + 200 + 10, 10 );

        gui.instantiate( new LoginMenu() );

//          gui.instantiate( new MainMenu() );

//        gui.instantiate( new PlayerHud( playerShip ) );

        gui.instantiate( new Cursor(), cursor -> cursor.setEnabled( false ) );

        return gameWorld;
    }
}
