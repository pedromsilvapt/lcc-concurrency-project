package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.Graphics2D.VisualComponent;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Prefabs.GameObject;


public class Window extends VisualComponent {
    public static String windowName = "Vaga Vermelha";

    public enum WindowState {
        Normal, Hovered, Pressed
    }

    protected WindowState state = WindowState.Normal;

    protected Sprite spriteStateNormal;
    protected Sprite spriteStateHovered;
    protected Sprite spriteStatePressed;
    
    protected Entity buttonLabel;

    protected Text windowLabelText;
    protected String value = "";


    public Window(Entity entity, String name) {
        super(entity, name);
    }

    public Window setAlign(int align){ 
        if(this.align != align){
            super.setAlign(align);
        }
        return this;
    }

    public String getValue () {
        return this.value;
    }

    public Window setValue ( String value ) {
        if ( this.value != value ) {
            this.value = value;

            this.refreshLabel();
        }

        return this;
    }
    
    public void onAlign(){
        Vector2 anchor = this.getAnchorPosition();

        this.buttonLabel.getComponent( Transform.class )
                .setPosition( anchor.x + this.getSize().width/2, anchor.y + this.getSize().height/2);
    }

    public String getWindowAssetName ( Window.WindowState state ) {
        return String.format( "grey_button05" );
    }


    public void onAwake () {
        super.onAwake();

        Sprite.PatchConfig buttonPatch = new Sprite.PatchConfig(12, 12, 23, 21);

        this.spriteStateNormal = this.entity.addComponent(Sprite.class, "window_normal");
        this.spriteStateNormal
                .setNinePatchConfig(buttonPatch)
                .setTexturePath(String.format("uipack/PNG/%s.png", this.getWindowAssetName(Window.WindowState.Normal)))
                .setAlign(this.getAlign())
                .setSize(this.size)
                .setEnabled(this.state == Window.WindowState.Normal);

        this.spriteStateHovered = this.entity.addComponent(Sprite.class, "window_hovered");
        this.spriteStateHovered
                .setNinePatchConfig(buttonPatch)
                .setTexturePath(String.format("uipack/PNG/%s.png", this.getWindowAssetName(Window.WindowState.Hovered)))
                .setAlign(this.getAlign())
                .setSize(this.size)
                .setEnabled(this.state == Window.WindowState.Hovered);

        this.spriteStatePressed = this.entity.addComponent(Sprite.class, "window_pressed");
        this.spriteStatePressed
                .setNinePatchConfig(buttonPatch)
                .setTexturePath(String.format("uipack/PNG/%s.png", this.getWindowAssetName(Window.WindowState.Pressed)))
                .setAlign(this.getAlign())
                .setSize(this.size)
                .setEnabled(this.state == Window.WindowState.Pressed);

        this.buttonLabel = this.entity.instantiate(new GameObject("buttonLabel"));

        this.onAlign();

        this.windowLabelText = this.buttonLabel.addComponent( Text.class );
        this.windowLabelText
                .setBitmapFont( "fonts/KenVector_Future_16_white.fnt" )
                .setValue( this.getValue() )
                .setAutoSize( false )
                .setWrap( false )
                .setTruncateText( "" )
                .setColor( Color.BLACK )
                .setTextAlign( Align.left )
                .setAlign( Align.center )
                .setSize( new Size( this.getSize().width - 35, this.getSize().height - 20 ) );

        this.refreshLabel();
    }

    protected void refreshLabel () {
        if (this.windowLabelText == null) {
            return;
        }
    }
    
    
    
    
    
    
}


