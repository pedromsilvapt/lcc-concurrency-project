package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Prefabs.GameObject;
import com.pcc.project.Utils.TimeStateMachine;

public class Textbox extends InteractiveControl {
    public static String defaultName = "textbox";

    public enum TextboxState {
        Normal, Hovered, Pressed
    }

    protected TimeStateMachine blinkingCursorState;

    protected String value = "";

    protected TextboxState state = TextboxState.Normal;

    protected Sprite spriteStateNormal;

    protected Sprite spriteStateHovered;

    protected Sprite spriteStatePressed;

    protected Entity buttonLabel;

    protected Text textboxLabelText;

    public Textbox ( Entity entity, String name ) {
        super( entity, name );

        this.blinkingCursorState = new TimeStateMachine( true );

        this.blinkingCursorState.addState( "off", 0.5f );
        this.blinkingCursorState.addState( "on", 0.5f );
    }

    public String getValue () {
        return this.value;
    }

    public Textbox setValue ( String value ) {
        this.value = value;

        return this;
    }

    public String getTextboxAssetName ( TextboxState state ) {
        return String.format( "grey_button05" );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        Sprite.PatchConfig buttonPatch = new Sprite.PatchConfig( 12, 12, 23, 21 );

        this.spriteStateNormal = this.entity.addComponent( Sprite.class, "textbox_normal" );
        this.spriteStateNormal
                .setNinePatchConfig( buttonPatch )
                .setTexturePath( String.format( "uipack/PNG/%s.png", this.getTextboxAssetName( TextboxState.Normal ) ) )
                .setAlign( this.getAlign() )
                .setSize( this.size )
                .setEnabled( this.state == TextboxState.Normal );

        this.spriteStateHovered = this.entity.addComponent( Sprite.class, "textbox_hovered" );
        this.spriteStateHovered
                .setNinePatchConfig( buttonPatch )
                .setTexturePath( String.format( "uipack/PNG/%s.png", this.getTextboxAssetName( TextboxState.Hovered ) ) )
                .setAlign( this.getAlign() )
                .setSize( this.size )
                .setEnabled( this.state == TextboxState.Hovered );

        this.spriteStatePressed = this.entity.addComponent( Sprite.class, "textbox_pressed" );
        this.spriteStatePressed
                .setNinePatchConfig( buttonPatch )
                .setTexturePath( String.format( "uipack/PNG/%s.png", this.getTextboxAssetName( TextboxState.Pressed ) ) )
                .setAlign( this.getAlign() )
                .setSize( this.size )
                .setEnabled( this.state == TextboxState.Pressed );

        this.buttonLabel = this.entity.instantiate( new GameObject( "buttonLabel" ) );

        this.buttonLabel.getComponent( Transform.class )
                .setPosition( this.getSize().width / 2, this.getSize().height / 2 );

        this.textboxLabelText = this.buttonLabel.addComponent( Text.class );
        this.textboxLabelText
                .setBitmapFont( "fonts/KenVector_Future_16_white.fnt" )
                .setValue( "" )
                .setAutoSize( false )
                .setWrap( false )
                .setTruncateText( "" )
                .setColor( Color.BLACK )
                .setTextAlign( Align.left )
                .setAlign( Align.center )
                .setSize( new Size( this.getSize().width - 35, this.getSize().height - 20 ) );
    }

    protected char getCharFromKey ( int key ) {
        return (char)(key - Input.Keys.A + 'a');
    }

    protected void refreshLabel () {
        if ( this.blinkingCursorState.getState().equals( "on" ) ) {
            this.textboxLabelText.setValue( this.value + '|' );
        } else {
            this.textboxLabelText.setValue( this.value );
        }
    }

    public void onFocus () {
        this.blinkingCursorState.start();
    }

    public void onBlur () {
        this.blinkingCursorState.start();

        this.refreshLabel();
    }

    protected void addChar ( char c ) {
        if ( value == null ) {
            this.value = "" + c;
        } else {
            this.value += c;
        }
    }

    protected void removeChar () {
        if ( value != null && value.length() > 0 ) {
            value = value.substring( 0, value.length() - 1 );
        }
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        if ( this.isFocused ) {
            for ( int i = Input.Keys.A; i < Input.Keys.Z; i++ ) {
                if ( Gdx.input.isKeyJustPressed( i ) ) {
                    this.addChar( this.getCharFromKey( i ) );
                }
            }

            if ( Gdx.input.isKeyJustPressed( Input.Keys.SPACE ) ) {
                this.addChar( ' ' );
            }

            if ( Gdx.input.isKeyJustPressed( Input.Keys.BACKSPACE ) ) {
                this.removeChar();
            }

            this.blinkingCursorState.update( Gdx.graphics.getDeltaTime() );

            this.refreshLabel();
        }
    }
}
