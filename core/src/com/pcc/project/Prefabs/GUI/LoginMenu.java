package com.pcc.project.Prefabs.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Button;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Textbox;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Theme;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.Graphics2D.VisualComponent;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GameObject;

public class LoginMenu extends Prefab< Entity > {

    @Override
    public Entity instantiate () {
        Entity menu = new GameObject().instantiate();

        menu.getComponent( Transform.class )
                .setPosition( 300, 300 );

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
                text.setValue( "pedro" )
                    .setSize( 185, 49 )
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
                text.setValue( "pedro" )
                        .setPasswordChar( "*" )
                        .setSize( 185, 49 )
                        .setAlign( Align.left );
            } );
        } );

        menu.instantiate( new GameObject( "button_login" ), entity -> {
            entity.getComponent( Transform.class )
                    .setPosition( 130, -80 );

            entity.addComponent( Button.class )
                    .setValue( "Login" )
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
                    .setAction( btn -> menu.destroy() )
                    .setSize( 120, 49 );
        } );

        return menu;
    }
}
