package com.pcc.project.ECS.Components.Network;

import com.badlogic.gdx.Gdx;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.GameLogic.Game;
import com.pcc.project.ECS.Components.GameLogic.User;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Theme;
import com.pcc.project.ECS.Components.Network.Entities.AlienEntity;
import com.pcc.project.ECS.Components.Network.Entities.NetworkEntity;
import com.pcc.project.ECS.Components.Network.Entities.ShipEntity;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.NetworkMessageBuilder;
import com.pcc.project.Prefabs.GUI.Leaderboards;
import com.pcc.project.Prefabs.GUI.MainMenu;
import com.pcc.project.Prefabs.GUI.MessageBox;
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

    protected List< Leaderboards.LeaderboardEntry > leaderboardsScores;

    protected List< Leaderboards.LeaderboardEntry > leaderboardsLevels;

    protected BiConsumer< List< Leaderboards.LeaderboardEntry >, List< Leaderboards.LeaderboardEntry > > leaderboardsCallback;

    protected Runnable queueCallback;

    public NetworkGameMaster ( Entity entity, String name ) {
        super( entity, name );
    }

    public User getSession () {
        return this.session;
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.networkManager = this.entity.getComponentInParent( NetworkManager.class );

//        this.session = new User( null, null )
//            .setUsername( "t" );
//
//        this.obey( NetworkMessages.parse( "message=game-found,player1.level=1,player1.name=t,player2.level=1,player2.name=y,size.height=100,size.width=100;energy.current=1.00000000000000000000e+01,energy.total=1.00000000000000000000e+01,engine.left=false,engine.main=false,engine.power=1.00000000000000000000e+01,engine.right=false,entity=ship,forwardVelocity.x=0.00000000000000000000e+00,forwardVelocity.y=0.00000000000000000000e+00,id=<0.8536.0>,isPlayer=t,pos.x=2.50000000000000000000e+01,pos.y=5.00000000000000000000e+01,rot=0.00000000000000000000e+00,size.height=5.00000000000000000000e+00,size.width=5.00000000000000000000e+00;energy.current=1.00000000000000000000e+01,energy.total=1.00000000000000000000e+01,engine.left=false,engine.main=false,engine.power=1.00000000000000000000e+01,engine.right=false,entity=ship,forwardVelocity.x=0.00000000000000000000e+00,forwardVelocity.y=0.00000000000000000000e+00,id=<0.4818.1>,isPlayer=y,pos.x=7.50000000000000000000e+01,pos.y=5.00000000000000000000e+01,rot=1.80000000000000000000e+02,size.height=5.00000000000000000000e+00,size.width=5.00000000000000000000e+00;entity=creature,id=0.00000000000000000000e+00,pos.x=5.00000000000000000000e+01,pos.y=5.00000000000000000000e+01,type=ally;entity=creature,id=1.00000000000000000000e+00,pos.x=7.50000000000000000000e+01,pos.y=2.50000000000000000000e+01,type=ally" ) );
    }

    public void commandPlayerStateChange ( boolean mainEngine, boolean leftEngine, boolean rightEngine ) {
        NetworkMessageBuilder message = new NetworkMessageBuilder()
                .addFrame()
                .addKey( "message", "key-update" )
                .addKey( "front-engine-key", String.valueOf( mainEngine ) )
                .addKey( "left-engine-key", String.valueOf( leftEngine ) )
                .addKey( "right-engine-key", String.valueOf( rightEngine ) );

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
                .addKey( "message", message )
                .addKey( "username", username )
                .addKey( "password", password );

        this.networkManager.send( builder.get() );
    }

    public void commandViewLeaderboards ( BiConsumer< List< Leaderboards.LeaderboardEntry >, List< Leaderboards.LeaderboardEntry > > callback ) {
        this.leaderboardsCallback = callback;

        NetworkMessageBuilder scoresBuilder = new NetworkMessageBuilder()
                .addFrame().addKey( "message", "give-score-points" );

        NetworkMessageBuilder levelsBuilder = new NetworkMessageBuilder()
                .addFrame().addKey( "message", "give-score-levels" );

        this.networkManager.send( scoresBuilder.get() );

        this.networkManager.send( levelsBuilder.get() );
    }

    public void commandUserLogout () {
        NetworkMessageBuilder builder = new NetworkMessageBuilder()
                .addFrame()
                .addKey( "message", "logout" );

        this.entity.getComponent( User.class ).destroy();

        this.session = null;

        this.networkManager.send( builder.get() );
    }

    public void commandJoinQueue ( Runnable callback ) {
        NetworkMessageBuilder builder = new NetworkMessageBuilder()
                .addFrame()
                .addKey( "message", "queue" );

        this.queueCallback = callback;

        this.networkManager.send( builder.get() );
    }

    public void commandLeaveQueue () {
        NetworkMessageBuilder builder = new NetworkMessageBuilder()
                .addFrame()
                .addKey( "message", "leave-queue" );

        this.queueCallback = null;

        this.networkManager.send( builder.get() );
    }


    public Class< ? extends NetworkEntity > getNetworkEntityClass ( String entity ) {
        switch ( entity ) {
            case "ship":
                return ShipEntity.class;
            case "creature":
                return AlienEntity.class;
            default:
                return null;
        }
    }

    protected < T extends NetworkEntity > NetworkEntity instantiateEntity ( Class< T > entityClass, Map< String, String > state ) {
        Gdx.app.log( "Entity", state.toString() );

        try {
            Method method = entityClass.getDeclaredMethod( "createPrefab", NetworkGameMaster.class, Map.class );

            Object result = method.invoke( null, this, state );

            if ( result instanceof Prefab ) {
                Entity entity = this.gameBoard.instantiate( ( Prefab< Entity > ) result );

                NetworkEntity networkEntity = entity.addComponent( entityClass )
                        .setEntityType( state.get( "entity" ) )
                        .setEntityId( state.get( "id" ) );

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

            if ( networkEntityClass != null ) {
                NetworkEntity networkEntity = this.instantiateEntity( networkEntityClass, entity );

                if ( networkEntity != null ) {
                    this.livingEntities.add( networkEntity );

                    return networkEntity;
                }
            }

            return null;
        }
    }

    public void obeyGameSetup ( List< Map< String, String > > message ) {
        if ( this.queueCallback != null ) {
            this.queueCallback.run();
        }

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

        if ( this.session.equals( player1 ) ) {
            this.gameBoard = this.entity.instantiate( new GameBoard( width, height, player1, player2 ) );
        } else {
            this.gameBoard = this.entity.instantiate( new GameBoard( width, height, player2, player1 ) );
        }

        this.gameBoard.setBefore( this.gameBoard.parent.getEntity( "gui" ) );

        this.obeyGameUpdate( message );
    }

    public void obeyGameUpdate ( List< Map< String, String > > message ) {
        Set< NetworkEntity > untouched = new HashSet<>( this.livingEntities );

        NetworkEntity entity;

        Map< String, String > frame;

        if ( message.get( 0 ).containsKey( "score" ) ) {
            this.gameBoard.getComponent( Game.class )
                    .setScore( ( int ) Math.floor( Float.parseFloat( message.get( 0 ).get( "score" ) ) ) );
        }

        // We assume that all frames after the first one (header) are either entities or events.
        for ( int i = 1; i < message.size(); i++ ) {
            frame = message.get( i );
            if ( frame.containsKey( "entity" ) ) {
                entity = this.obeyGameUpdateEntity( frame );

                if ( entity != null && untouched.contains( entity ) ) {
                    untouched.remove( entity );
                }
            } else {
                Gdx.app.error( "Entity", String.format( "Did not contain \"entity\" key: %s", frame.toString() ) );
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
                        .setUsername( message.get( 0 ).get( "username" ) )
                        .setLevel( Integer.parseInt( message.get( 0 ).get( "level" ) ) );

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
                        .setUsername( message.get( 0 ).get( "username" ) )
                        .setLevel( Integer.parseInt( message.get( 0 ).get( "level" ) ) );

                this.session = user;

                this.registerCallback.accept( null, user );
            }
        }

        this.registerCallback = null;
    }

    public void obeyGameEnd ( List< Map< String, String > > message ) {
        Gdx.app.log( "GameEnd", message.toString() );
        String end = message.get( 0 ).get( "end-msg" );

        Entity gui = gameBoard.root.getEntityInChildren( "gui" );

        this.livingEntities.clear();

        if ( end != null ) {
            Game game = this.gameBoard.getComponent( Game.class );

            if ( end.equals( "You lose" ) ) {
                game.setWinner( game.getOpponent() );
            } else {
                game.setWinner( game.getPlayer() );
            }

            gui.instantiate( new MessageBox( Theme.Blue, "Jogo Terminado", end, () -> {
                gameBoard.destroy();

                gameBoard = null;

                gui.instantiate( new MainMenu() );
            } ) );
        } else {
            gameBoard.destroy();

            gameBoard = null;

            gui.instantiate( new MainMenu() );

        }
    }

    public void obeyLeaderboardsScores ( List< Map< String, String > > message ) {
        List< Leaderboards.LeaderboardEntry > entries = new ArrayList<>();

        Map<String, String> frame;

        for ( int i = 1; i < message.size(); i++ ) {
            frame = message.get( i );

            entries.add( new Leaderboards.LeaderboardEntry( frame.get( "user" ), frame.get( "value" ) ) );
        }

        this.leaderboardsScores = entries;

        if ( this.leaderboardsLevels != null ) {
            this.leaderboardsCallback.accept( this.leaderboardsScores, this.leaderboardsLevels );

            this.leaderboardsCallback = null;
            this.leaderboardsScores = null;
            this.leaderboardsLevels = null;
        }
    }

    public void obeyLeaderboardsLevels ( List< Map< String, String > > message ) {
        List< Leaderboards.LeaderboardEntry > entries = new ArrayList<>();

        Map<String, String> frame;

        for ( int i = 1; i < message.size(); i++ ) {
            frame = message.get( i );

            entries.add( new Leaderboards.LeaderboardEntry( frame.get( "user" ), frame.get( "value" ) ) );
        }

        this.leaderboardsLevels = entries;

        if ( this.leaderboardsScores != null ) {
            this.leaderboardsCallback.accept( this.leaderboardsScores, this.leaderboardsLevels );

            this.leaderboardsCallback = null;
            this.leaderboardsScores = null;
            this.leaderboardsLevels = null;
        }
    }

    public void obey ( List< Map< String, String > > message ) {
        if ( message.size() == 0 ) {
            Gdx.app.error( "GameMaster", "Invalid message received: No header frame." );
            return;
        }

        if ( !message.get( 0 ).containsKey( "message" ) ) {
            Gdx.app.error( "GameMaster", "Invalid message received: No 'message' property in header frame." );
            return;
        }

        String messageType = message.get( 0 ).get( "message" );

        if ( messageType.equals( "login" ) ) {
            this.obeyLogin( message );
        } else if ( messageType.equals( "register" ) ) {
            this.obeyRegister( message );
        } else if ( messageType.equals( "scorescore" ) ) {
            this.obeyLeaderboardsScores( message );
        } else if ( messageType.equals( "scorelevel" ) ) {
            this.obeyLeaderboardsLevels( message );
        } else if ( messageType.equals( "update" ) ) {
            this.obeyGameUpdate( message );
        } else if ( messageType.equals( "game-found" ) ) {
            this.obeyGameSetup( message );
        } else if ( messageType.equals( "game-end" ) ) {
            this.obeyGameEnd( message );
        }
    }

    public void obeyAll () {
        List< Map< String, String > > message;

        while ( ( message = this.networkManager.receive() ) != null ) {
            this.obey( message );
        }
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        this.obeyAll();
    }

    public void onDestroy () {
        super.onDestroy();

        if ( this.session != null ) {
            this.commandUserLogout();
        }
    }
}
