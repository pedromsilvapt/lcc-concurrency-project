package com.pcc.project.Prefabs.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Button;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Layout.PositionLayout;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Textbox;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Theme;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Window;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.Graphics2D.VisualComponent;
import com.pcc.project.ECS.Components.Network.NetworkGameMaster;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GameObject;

public class LoginMenu extends Prefab< Entity > {
    private boolean working = false;

    private void showError ( Entity menu, String error ) {
        this.working = true;

        menu.instantiate( new MessageBox( Theme.Red,"Error", error, () -> {
            this.working = false;
        } ) );
    }

    private void goToMainMenu ( Entity menu ) {
        menu.parent.instantiate( new MainMenu() );

        menu.destroy();
    }

    private void register ( Entity menu ) {
        if ( this.working ) {
            return;
        }

        this.working = true;

        Entity textboxUsername = menu.getEntity( "textbox_username" );
        Entity textboxPassword = menu.getEntity( "textbox_password" );

        String username = textboxUsername.getComponent( Textbox.class )
                .getValue();

        String password = textboxPassword.getComponent( Textbox.class )
                .getValue();

        NetworkGameMaster gameMaster = menu.getComponentInParent( NetworkGameMaster.class );

        gameMaster.commandUserRegister( username, password, ( err, user ) -> {
            if ( err != null ) {
                this.showError( menu, err );
            } else {
                this.goToMainMenu( menu );
            }
        } );
    }

    private void login ( Entity menu ) {
        if ( this.working ) {
            return;
        }

        this.working = true;

        Entity textboxUsername = menu.getEntity( "textbox_username" );
        Entity textboxPassword = menu.getEntity( "textbox_password" );

        String username = textboxUsername.getComponent( Textbox.class )
                .getValue();

        String password = textboxPassword.getComponent( Textbox.class )
               .getValue();

        NetworkGameMaster gameMaster = menu.getComponentInParent( NetworkGameMaster.class );

        gameMaster.commandUserLogin( username, password, ( err, user ) -> {
            if ( err != null ) {
                this.showError( menu, err );
            } else {
                this.goToMainMenu( menu );
            }
        } );
    }

    @Override
    public Entity instantiate () {
        Entity menu = new GameObject().instantiate();

        menu.addComponent( Window.class )
                .setTitle( "Login" )
                .setAnchor( -20, -100 )
                .setSize( 360, 250 );

        menu.instantiate( new GameObject( "label_username" ), label -> {
            label.getComponent( Transform.class )
                    .setPosition( 0, 65 );

            label.addComponent( Text.class, text -> {
                text.setValue( "Username" )
                    .setBitmapFont( BaseStylesheet.font )
                    .setColor( BaseStylesheet.dark )
                    .setTextAlign( Align.left );
            } );
        } );

        menu.instantiate( new GameObject( "textbox_username" ), label -> {
            label.getComponent( Transform.class )
                    .setPosition( 130, 75 );

            label.addComponent( Textbox.class, text -> {
                text.setSize( 185, 49 )
                    .setAlign( Align.left );
            } );
        } );


        menu.instantiate( new GameObject( "label_username" ), label -> {
            label.getComponent( Transform.class )
                    .setPosition( 0, 0 );

            label.addComponent( Text.class, text -> {
                text.setValue( "Password" )
                    .setBitmapFont( BaseStylesheet.font )
                    .setColor( BaseStylesheet.dark )
                    .setTextAlign( Align.left );
            } );
        } );


        menu.instantiate( new GameObject( "textbox_password" ), label -> {
            label.getComponent( Transform.class )
                    .setPosition( 130, 10 );

            label.addComponent( Textbox.class, text -> {
                text.setPasswordChar( "*" )
                        .setSize( 185, 49 )
                        .setAlign( Align.left );
            } );
        } );

        menu.instantiate( new GameObject( "button_login" ), entity -> {
            entity.getComponent( Transform.class )
                    .setPosition( 130, -80 );

            entity.addComponent( Button.class )
                    .setValue( "Login" )
                    .setAction( ent -> this.login( menu ) )
                    .setTheme( Theme.Green )
                    .setShiny( true )
                    .setSize( 185, 49 );
        } );

        menu.instantiate( new GameObject( "button_register" ), entity -> {
            entity.getComponent( Transform.class )
                    .setPosition( 0, -80 );

            entity.addComponent( Button.class )
                    .setValue( "Register" )
                    .setTheme( Theme.Grey )
                    .setColor( BaseStylesheet.dark )
                    .setShiny( true )
                    .setAction( btn -> this.register( menu ) )
                    .setSize( 120, 49 );
        } );

        menu.addComponent( PositionLayout.class )
                .setAlign( Align.center )
                .setSize( 300, 0 );

        Textbox txtUsername = menu.getEntityInChildren( "textbox_username" ).getComponent( Textbox.class );
        Textbox txtPassword = menu.getEntityInChildren( "textbox_password" ).getComponent( Textbox.class );
        Button btnLogin = menu.getEntityInChildren( "button_login" ).getComponent( Button.class );

        txtUsername.setTabTo( txtPassword ).setTabTo( btnLogin );

        txtUsername.setAutoFocus( true );

        return menu;
    }
}
