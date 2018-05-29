package com.pcc.project.Prefabs.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Button;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Layout.PositionLayout;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Theme;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Window;
import com.pcc.project.ECS.Components.Graphics2D.Primitive.Rectangle;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.LifecycleHooks;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GameObject;

import java.util.function.Consumer;

public class MessageBox extends Prefab<Entity> {
    protected String title;

    protected String message;

    protected Theme theme = Theme.Yellow;

    protected Runnable callback;


    public MessageBox ( String title, String message ) {
        this( title, message, null );
    }

    public MessageBox ( Theme theme, String title, String message, Runnable callback ) {
        this( title, message, callback );

        this.theme = theme;
    }

    public MessageBox ( String title, String message, Runnable callback ) {
        this.title = title;
        this.message = message;
        this.callback = callback;
    }

    @Override
    public Entity instantiate () {
        Entity messageBox = new GameObject( "messageBox" ).instantiate();

        Entity backdrop = messageBox.instantiate( new GameObject( "backdrop" ) );

        backdrop.addComponent( Rectangle.class )
            .setColor( new Color( 0, 0, 0, 0.3f ) );

        backdrop.addComponent( LifecycleHooks.class )
            .setOnUpdate( entity -> {
                Transform transform = backdrop.getComponent( Transform.class ).getParentTransform();

                if ( transform == null ) {
                    backdrop.getComponent( Transform.class ).getParentTransform( true );
                }

                if ( transform == null ) {
                    return;
                }

                Vector2 scale = transform.globalToLocalVector( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
                Vector2 position = transform.globalToLocalPoint( 0, 0 );

                backdrop.getComponent( Transform.class )
                        .setPosition( position.x, position.y );

                backdrop.getComponent( Rectangle.class )
                        .setSize( scale.x, scale.y );
            } );

        float bodyHeight = 100;
        float topHeight = 30;
        float width = 300;

        // Temporary Window Frame
        Entity windowFrame = messageBox.instantiate( new GameObject( "windowFrame" ) );
        windowFrame.addComponent( Window.class )
                .setTitle( this.title )
                .setTheme( this.theme )
                .setSize( width, topHeight + bodyHeight );

//        Entity frameTop = windowFrame.instantiate( new GameObject( "windowFrameTop" ) );

//        frameTop.getComponent( Transform.class )
//                .setPosition( 0, bodyHeight );
//
//        frameTop.addComponent( Rectangle.class )
//            .setColor( BaseStylesheet.red )
//            .setSize( width, topHeight );

        com.badlogic.gdx.math.Rectangle padding = new com.badlogic.gdx.math.Rectangle( 10, 10, 10, 49 + 10 + 10 );

//        Entity frameBody = windowFrame.instantiate( new GameObject( "windowFrameBody" ) );

//        frameBody.addComponent( Rectangle.class )
//            .setColor( Color.GRAY )
//            .setSize( width, bodyHeight );

        // Title Label
        Entity title = windowFrame.instantiate( new GameObject( "windowTitle" ) );

        title.getComponent( Transform.class )
                .setPosition( 0, bodyHeight );

        title.addComponent( Text.class )
                .setBitmapFont( BaseStylesheet.font )
                .setAutoSize( false )
                .setWrap( false )
                .setTruncateText( "..." )
                .setAlign( Align.bottomLeft )
                .setTextAlign( Align.center )
                .setValue( this.title )
                .setColor( BaseStylesheet.white )
                .setSize( width, topHeight );

        Entity message = windowFrame.instantiate( new GameObject( "windowMessage" ) );

        message.getComponent( Transform.class )
                .setPosition( padding.x, padding.height );

        message.addComponent( Text.class )
                .setBitmapFont( BaseStylesheet.font )
                .setAutoSize( false )
                .setWrap( false )
                .setTruncateText( "..." )
                .setAlign( Align.bottomLeft )
                .setTextAlign( Align.center )
                .setValue( this.message )
                .setColor( BaseStylesheet.dark )
                .setSize( width - padding.x - padding.width, bodyHeight - padding.y - padding.height );

        Entity closeButton = windowFrame.instantiate( new GameObject( "closeButton" ) );

        float buttonWidth = width / 2;

        closeButton.getComponent( Transform.class )
                .setPosition( ( width / 2 ) - ( buttonWidth / 2 ), 10 );

        closeButton.addComponent( Button.class )
                .setAlign( Align.bottomLeft )
                .setTheme( Theme.Grey )
                .setColor( BaseStylesheet.dark )
                .setValue( "Close" )
                .setAction( btn -> {
                    messageBox.destroy();

                    if ( this.callback != null ) {
                        this.callback.run();
                    }
                } )
                .setSize( buttonWidth, 49 );

        messageBox.addComponent( PositionLayout.class )
                .setAlign( Align.center )
                .setSize( width, bodyHeight + topHeight );


        closeButton.getComponent( Button.class ).setAutoFocus( true );

        return messageBox;
    }
}
