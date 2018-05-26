package com.pcc.project.ECS.Components.Network;

import com.badlogic.gdx.Gdx;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.GameLogic.Ship;
import com.pcc.project.ECS.Components.GameLogic.User;
import com.pcc.project.ECS.Components.Network.Entities.NetworkEntity;
import com.pcc.project.ECS.Components.Network.Entities.ShipEntity;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.NetworkMessageBuilder;
import com.pcc.project.Prefabs.GameBoard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * This class works in conjunction with the NetworkManager.
 * Methods prefixed with "obey" correspond to messages received from the server, and whose orders shall be taken immediately.
 * Methods prefixed with "report" correspond to actions taken by the client that should be relayed to the server.
 * <p>
 * When something happens in the game, the components call one method in this class to notify of the event.
 * Every update (frame) the obey() method is called, to check if there are any messages from the server in the queue
 * and to process them
 */
public class NetworkGameMaster extends Component {
    public class PlayerEngine {
        public final String Forward = "FEngine";
        public final String Left    = "RLEngine";
        public final String Right   = "RREngine";
    }

    protected List< NetworkEntity > livingEntities = new ArrayList<>();

    protected NetworkManager networkManager;

    protected Entity gameBoard = null;

    protected User session = null;

    // Callbacks
    protected BiConsumer< String, User > loginCallback;

    protected BiConsumer< String, User > registerCallback;

    public NetworkGameMaster ( Entity entity, String name ) {
        super( entity, name );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.networkManager = this.entity.getComponentInParent( NetworkManager.class );
    }

    public void commandPlayerStateChange ( String engine, Ship.Thruster state ) {
        String onOff = state == Ship.Thruster.Idle ? "Off" : "On";

        NetworkMessageBuilder message = new NetworkMessageBuilder()
                .addFrame()
                .addKey( "message", "player-change" )
                .addKey( "event", engine + onOff );

        this.networkManager.send( message.get() );
    }

    public void commandUserLogin ( String username, String password, BiConsumer< String, User > callback ) {
        this.loginCallback = callback;

        this.commandUserCredentials( "login", username, password );
    }

    public void commandUserRegister ( String username, String password, BiConsumer< String, User > callback ) {
        this.registerCallback = callback;

        this.commandUserCredentials( "register", username, password );
    }

    public void commandUserCredentials ( String message, String username, String password ) {
        NetworkMessageBuilder builder = new NetworkMessageBuilder()
                .addFrame()
                .addKey( "message", "login" )
                .addKey( "username", username )
                .addKey( "password", password );

        this.networkManager.send( builder.get() );
    }

    public void commandUserLogout () {
        NetworkMessageBuilder builder = new NetworkMessageBuilder()
                .addFrame()
                .addKey( "message", "logout" );

        this.entity.getComponent( User.class ).destroy();

        this.networkManager.send( builder.get() );
    }


    public Class< ? extends NetworkEntity > getNetworkEntityClass ( String entity ) {
        switch ( entity ) {
            case "ship":
                return ShipEntity.class;
            default:
                return null;
        }
    }

    protected < T extends NetworkEntity > NetworkEntity instantiateEntity ( Class< T > entityClass, Map< String, String > state ) {
        try {
            Method method = entityClass.getMethod( "createPrefab", Map.class );

            Object result = method.invoke( null, state );

            if ( result instanceof Prefab ) {
                Entity entity = this.gameBoard.instantiate( ( Prefab< Entity > ) result );

                NetworkEntity networkEntity = entity.addComponent( entityClass );

                networkEntity.obey( state );

                return networkEntity;
            }

            return null;
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e ) {
            e.printStackTrace();
        }

        return null;
    }


    public NetworkEntity obeyGameUpdateEntity ( Map< String, String > entity ) {
        String type = entity.get( "entity" );
        String id   = entity.get( "id" );

        Optional< NetworkEntity > entityController = this.livingEntities.stream().filter( ctrl -> ctrl.getEntityType().equals( type ) && ctrl.getEntityId().equals( id ) ).findFirst();

        if ( entityController.isPresent() ) {
            entityController.get().obey( entity );

            return entityController.get();
        } else {
            Class< ? extends NetworkEntity > networkEntityClass = this.getNetworkEntityClass( type );

            NetworkEntity networkEntity = this.instantiateEntity( networkEntityClass, entity );

            if ( networkEntity != null ) {
                this.livingEntities.add( networkEntity );

                return networkEntity;
            }

            return null;
        }
    }

    public void obeyGameSetup ( List< Map< String, String > > message ) {
        Map< String, String > header = message.get( 0 );

        int width  = Integer.parseInt( header.get( "size.width" ) );
        int height = Integer.parseInt( header.get( "size.height" ) );

        if ( this.gameBoard != null ) {
            this.gameBoard.destroy();

            this.gameBoard = null;

            this.livingEntities.clear();
        }

        User player1 = new User( null, null )
                .setUsername( header.get( "player1.name" ) )
                .setLevel( Integer.parseInt( header.get( "player1.level" ) ) );


        User player2 = new User( null, null )
                .setUsername( header.get( "player2.name" ) )
                .setLevel( Integer.parseInt( header.get( "player2.level" ) ) );

        // TODO distinguish the local player and the opponent from player1 and player2

        this.gameBoard = this.entity.instantiate( new GameBoard( width, height, player1, player2 ) );

        this.obeyGameUpdate( message );
    }

    public void obeyGameUpdate ( List< Map< String, String > > message ) {
        Set< NetworkEntity > untouched = new HashSet<>( this.livingEntities );

        NetworkEntity entity;

        Map< String, String > frame;

        // We assume that all frames after the first one (header) are either entities or events.
        for ( int i = 1; i < message.size(); i++ ) {
            frame = message.get( i );
            if ( frame.containsKey( "entity" ) ) {
                entity = this.obeyGameUpdateEntity( frame );

                if ( entity != null && untouched.contains( entity ) ) {
                    untouched.remove( entity );
                }
            }
        }

        // Any entity not present should be automatically destroyed
        for ( NetworkEntity eachEntity : untouched ) {
            eachEntity.entity.destroy();
        }
    }

    public void obeyLogin ( List< Map< String, String > > message ) {
        Map< String, String > frame = message.get( 0 );

        if ( frame.containsKey( "error" ) ) {
            if ( this.loginCallback != null ) {
                this.loginCallback.accept( frame.get( "error" ), null );
            }
        } else {
            if ( this.loginCallback != null ) {
                User user = this.entity.addComponent( User.class )
                        .setUsername( message.get( 1 ).get( "username" ) )
                        .setLevel( Integer.parseInt( message.get( 1 ).get( "level" ) ) );

                this.session = user;

                this.loginCallback.accept( null, user );
            }
        }

        this.loginCallback = null;
    }

    public void obeyRegister ( List< Map< String, String > > message ) {
        Map< String, String > frame = message.get( 0 );

        if ( frame.containsKey( "error" ) ) {
            if ( this.registerCallback != null ) {
                this.registerCallback.accept( frame.get( "error" ), null );
            }
        } else {
            if ( this.registerCallback != null ) {
                User user = this.entity.addComponent( User.class )
                        .setUsername( message.get( 1 ).get( "username" ) )
                        .setLevel( Integer.parseInt( message.get( 1 ).get( "level" ) ) );

                this.session = user;

                this.registerCallback.accept( null, user );
            }
        }

        this.registerCallback = null;
    }

    public void obey ( List< Map< String, String > > message ) {
        if ( message.size() == 0 ) {
            Gdx.app.error( "GameMaster", "Invalid message received: No header frame." );
            return;
        }

        if ( message.get( 0 ).containsKey( "message" ) ) {
            Gdx.app.error( "GameMaster", "Invalid message received: No 'message' property in header frame." );
            return;
        }

        String messageType = message.get( 0 ).get( "message" );

        if ( messageType.equals( "login" ) ) {
            this.obeyLogin( message );
        } else if ( messageType.equals( "register" ) ) {
            this.obeyRegister( message );
        } else if ( messageType.equals( "game-update" ) ) {
            this.obeyGameUpdate( message );
        } else if ( messageType.equals( "game-setup" ) ) {
            this.obeyGameSetup( message );
        }
    }

    public void obeyAll () {
        List< Map< String, String > > message;

        while ( ( message = this.networkManager.receive() ) != null ) {
            this.obey( message );
        }
    }
}
