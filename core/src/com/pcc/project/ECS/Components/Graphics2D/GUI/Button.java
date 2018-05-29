package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Prefabs.GUI.BaseStylesheet;
import com.pcc.project.Prefabs.GameObject;

import java.util.function.Consumer;

public class Button extends InteractiveControl {
    public static String defaultName = "button";

    public enum ButtonState {
        Normal, Hovered, Pressed
    }

    protected String value = "Button";

    protected Color color = Color.WHITE;

    protected Theme theme = Theme.Grey;

    protected boolean shiny = true;

    protected Consumer<Button> action;

    /* SPRITE STATES */

    protected ButtonState state = ButtonState.Normal;

    protected Sprite spriteStateNormal;

    protected Sprite spriteStateHovered;

    protected Sprite spriteStatePressed;

    protected Entity buttonLabel;

    protected Text buttonLabelText;

    public Button ( Entity entity, String name ) {
        super( entity, name );
    }

    public ButtonState getState () {
        return this.state;
    }

    public Consumer< Button > getAction () {
        return action;
    }

    public Button setAction ( Consumer< Button > action ) {
        this.action = action;

        return this;
    }

    public String getValue () {
        return this.value;
    }

    public Button setValue ( String value ) {
        this.value = value;

        if ( this.buttonLabelText != null ) {
            this.buttonLabelText.setValue( value );
        }

        return this;
    }

    public Color getColor () {
        return color;
    }

    public Button setColor ( Color color ) {
        this.color = color;

        if ( this.buttonLabelText != null ) {
            this.buttonLabelText.setColor( this.color );
        }

        return this;
    }

    public Button setState ( ButtonState state ) {
        if ( this.state == state ) {
            return this;
        }

        this.state = state;

        if ( this.spriteStateNormal != null ) {
            this.spriteStateNormal.setEnabled( state == ButtonState.Normal );
            this.spriteStateHovered.setEnabled( state == ButtonState.Hovered );
            this.spriteStatePressed.setEnabled( state == ButtonState.Pressed );
        }

        return this;
    }

    @Override
    public Button setAlign ( int align ) {
        super.setAlign( align );

        if ( this.spriteStateNormal != null ) {
            this.spriteStateNormal.setAlign( align );
            this.spriteStateHovered.setAlign( align );
            this.spriteStatePressed.setAlign( align );
        }

        return this;
    }

    public Theme getTheme () {
        return this.theme;
    }

    public Button setTheme ( Theme theme ) {
        this.theme = theme;

        this.updateSprites();

        return this;
    }

    public boolean isShiny () {
        return this.shiny;
    }

    public Button setShiny ( boolean shiny ) {
        this.shiny = shiny;

        this.updateSprites();

        return this;
    }

    protected void updateSprites () {
        if ( this.spriteStateNormal != null ) {
            this.spriteStateNormal.setTexturePath( this.getButtonAssetName( ButtonState.Normal ) );
            this.spriteStateHovered.setTexturePath( this.getButtonAssetName( ButtonState.Hovered ) );
            this.spriteStatePressed.setTexturePath( this.getButtonAssetName( ButtonState.Pressed ) );
        }
    }

    public String pad ( int number, int length, String c ) {
        return String.format( "%" + c + length + "d", number );
    }

    public String getButtonStateCode ( ButtonState state ) {
        int offset = this.theme == Theme.Grey ? 1 : 0;

        int code = 0;

        if ( this.shiny ) {
            if ( state == ButtonState.Normal ) {
                code = 07;
            } else if ( state == ButtonState.Hovered ) {
                code = 11;
            } else {
                code = 8;
            }
        } else {
            if ( state == ButtonState.Normal ) {
                code = 9;
            } else if ( state == ButtonState.Hovered ) {
                code = 11;
            } else {
                code = 10;
            }
        }

        return this.pad( code + offset, 2, "0" );
    }

    public String getButtonAssetName ( ButtonState state ) {
        String code = this.getButtonStateCode( state );

        return String.format( "%s_button%s", Theme.getString( this.theme ), code );
    }

    public void preloadTextures () {
        this.spriteStateNormal.getTexture();
        this.spriteStatePressed.getTexture();
        this.spriteStateHovered.getTexture();
    }


    @Override
    public void onResize () {
        super.onResize();

        if ( this.spriteStateNormal != null ) {
            this.spriteStateNormal.setSize( this.getSize() );
            this.spriteStateHovered.setSize( this.getSize() );
            this.spriteStatePressed.setSize( this.getSize() );

            this.buttonLabelText.setSize( this.getSize() );
        }
    }

    @Override
    public void onAwake () {
        super.onAwake();

        Sprite.PatchConfig buttonPatch = new Sprite.PatchConfig( 12, 12, 23, 21 );

        this.spriteStateNormal = this.entity.addComponent( Sprite.class, "button_normal" );
        this.spriteStateNormal
                .setNinePatchConfig( buttonPatch )
                .setTexturePath( String.format( "uipack/PNG/%s.png", this.getButtonAssetName( ButtonState.Normal ) ) )
                .setAlign( this.getAlign() )
                .setSize( this.size )
                .setEnabled( this.state == ButtonState.Normal );

        this.spriteStateHovered = this.entity.addComponent( Sprite.class, "button_hovered" );
        this.spriteStateHovered
                .setNinePatchConfig( buttonPatch )
                .setTexturePath( String.format( "uipack/PNG/%s.png", this.getButtonAssetName( ButtonState.Hovered ) ) )
                .setAlign( this.getAlign() )
                .setSize( this.size )
                .setEnabled( this.state == ButtonState.Hovered );

        this.spriteStatePressed = this.entity.addComponent( Sprite.class, "button_pressed" );
        this.spriteStatePressed
                .setNinePatchConfig( buttonPatch )
                .setTexturePath( String.format( "uipack/PNG/%s.png", this.getButtonAssetName( ButtonState.Pressed ) ) )
                .setAlign( this.getAlign() )
                .setSize( this.size )
                .setEnabled( this.state == ButtonState.Pressed );

        this.buttonLabel = this.entity.instantiate( new GameObject( "windowLabel" ) );

        this.buttonLabel.getComponent( Transform.class )
                .setPosition( this.getSize().width / 2, this.getSize().height / 2 );

        this.buttonLabelText = this.buttonLabel.addComponent( Text.class );
        this.buttonLabelText
                .setBitmapFont( BaseStylesheet.font )
                .setValue( this.getValue() )
                .setAutoSize( false )
                .setWrap( false )
                .setTruncateText( "..." )
                .setColor( this.getColor() )
                .setAlign( Align.center )
                .setSize( this.getSize() );

        this.preloadTextures();
    }

    public void onMousePress () {
        if ( this.action != null ) {
            this.action.accept( this );
        }
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        if ( this.isMouseClicking ) {
            this.setState( Button.ButtonState.Pressed );
        } else if ( this.isMouseOver ) {
            this.setState( Button.ButtonState.Hovered );
        } else {
            this.setState( Button.ButtonState.Normal );
        }

        if ( this.isFocused && Gdx.input.isKeyJustPressed( Input.Keys.ENTER ) ) {
            this.onMousePress();
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        if ( this.spriteStateNormal != null ) {
            this.spriteStateNormal.destroy();
            this.spriteStateHovered.destroy();
            this.spriteStatePressed.destroy();

            this.buttonLabel.destroy();

            this.spriteStateNormal = null;
            this.spriteStateHovered = null;
            this.spriteStatePressed = null;
        }
    }
}
