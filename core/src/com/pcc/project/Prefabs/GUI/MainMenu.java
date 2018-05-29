package com.pcc.project.Prefabs.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.GameLogic.User;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Button;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Layout.PositionLayout;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Theme;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Window;
import com.pcc.project.ECS.Components.Network.NetworkGameMaster;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GameBoard;
import com.pcc.project.Prefabs.GameObject;

public class MainMenu extends Prefab<Entity> {
    protected boolean inQueue = false;

    protected void logout ( Entity menu ) {
        NetworkGameMaster gameMaster = menu.getComponentInParent( NetworkGameMaster.class );

        gameMaster.commandUserLogout();

        menu.parent.instantiate( new LoginMenu() );

        menu.destroy();
    }

    protected void leaderboards ( Entity menu ) {
        NetworkGameMaster gameMaster = menu.getComponentInParent( NetworkGameMaster.class );

        // TODO create command to gather leaderboards
//        gameMaster.commandUserLogout();

        menu.parent.instantiate( new Leaderboards() );

        menu.destroy();
    }

    protected void joinGame ( Entity menu ) {
//        Entity gui = menu.root.getEntityInChildren( "gui" );
//
//        Entity gameWorld = menu.root.getEntityInChildren( "gameWorld" );
//
//        User player = new User(null, null).setUsername( "Pedro" ).setLevel( 1 );
//        User opponent = new User(null, null).setUsername( "Ezequiel" ).setLevel( 1 );
//
//        gameWorld.instantiate( new GameBoard( 2000, 2000, player, opponent ) )
//                .setBefore( gui );
//
//        menu.destroy();
    }

    protected void leaveQueue ( Entity menu ) {
        inQueue = false;

        NetworkGameMaster gameMaster = menu.getComponentInParent( NetworkGameMaster.class );

        gameMaster.commandLeaveQueue();
    }

    protected void joinQueue ( Entity menu ) {
        menu.instantiate( new MessageBox( Theme.Red,"Finding Game", "Click to leave queue", () -> {
            this.leaveQueue( menu );
        } ) );


        NetworkGameMaster gameMaster = menu.getComponentInParent( NetworkGameMaster.class );

        gameMaster.commandJoinQueue( () -> {
            menu.destroy();
        } );
    }

    @Override
    public Entity instantiate () {
        Entity menu = new GameObject( "mainMenu" ).instantiate();

        float width = 200;

        menu.addComponent( Window.class )
                .setTitle( "Main Menu" )
                .setSize( 240, 295 )
                .setAnchor( -20, -20 );

        Entity play = menu.instantiate( new GameObject( "play", 0, 177 ) );

        play.addComponent( Button.class )
                .setTheme( Theme.Yellow )
                .setColor( BaseStylesheet.golden )
                .setValue( "Find Game" )
                .setAction( btn -> this.joinQueue( menu ) )
                .setSize( width, 49 );

        Entity leaderboards = menu.instantiate( new GameObject( "leaderboards", 0, 118 ) );

        leaderboards.addComponent( Button.class )
                .setTheme( Theme.Blue )
                .setValue( "Leaderboards" )
                .setAction( btn -> this.leaderboards( menu ) )
                .setSize( width, 49 );

        Entity logout = menu.instantiate( new GameObject( "logout", 0, 58 ) );

        logout.addComponent( Button.class )
                .setTheme( Theme.Blue )
                .setValue( "Logout" )
                .setAction( btn -> this.logout( menu ) )
                .setSize( width, 49 );

        Entity quit = menu.instantiate( new GameObject( "quit" ) );

        quit.addComponent( Button.class )
                .setTheme( Theme.Blue )
                .setValue( "Quit" )
                .setAction( btn -> Gdx.app.exit() )
                .setSize( width, 49 );

        menu.addComponent( PositionLayout.class )
                .setAlign( Align.center )
                .setSize( width, 246 );


        return menu;
    }
}
