package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.Graphics2D.Renderer2D;
import com.pcc.project.ECS.Entity;

import java.util.ArrayList;
import java.util.List;

public class InputManager extends Component {
    protected InteractiveControl focused = null;

    protected InteractiveControl mouseFocused = null;

    protected boolean mouseReleased = false;

    protected Renderer2D renderer;

    protected List< InteractiveControl > drawnControls = new ArrayList<>();

    protected List< InteractiveControl > controls = new ArrayList<>();

    public InputManager ( Entity entity, String name ) {
        super( entity, name );
    }

    public boolean isMouseClick () {
        return !this.mouseReleased && Gdx.input.isButtonPressed( Input.Buttons.LEFT );
    }

    public void releaseMouseButton () {
        if ( Gdx.input.isButtonPressed( Input.Buttons.LEFT ) ) {
            this.mouseReleased = true;
        }
    }

    public boolean hasMouseFocus ( InteractiveControl control ) {
        return this.mouseFocused == control;
    }

    public boolean hasFocus ( InteractiveControl control ) {
        return this.focused == control;
    }

    public void requestFocus ( InteractiveControl control ) {
        if ( this.focused != null ) {
            focused.blur();
        }

        this.focused = control;
    }

    public void requestBlur ( InteractiveControl control ) {
        if ( this.focused == control ) {
            this.focused = null;
        }
    }

    public void draw ( InteractiveControl control ) {
        this.controls.add( control );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.renderer = this.entity.getComponentInParent( Renderer2D.class );
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        InteractiveControl control;

        Vector2 mouse = this.renderer.getCamera().screenToGlobal( new Vector2( Gdx.input.getX(), Gdx.input.getY() ) );

        this.mouseFocused = null;

        for ( int i = this.drawnControls.size() - 1; i >= 0; i-- ) {
            control = this.drawnControls.get( i );

            if ( control.getGlobalRectangle().contains( mouse ) ) {
                this.mouseFocused = control;

                break;
            }
        }

        if ( this.mouseReleased && !Gdx.input.isButtonPressed( Input.Buttons.LEFT ) ) {
            this.mouseReleased = false;
        }
    }

    @Override
    public void onDraw () {
        super.onDraw();

        this.controls = new ArrayList<>();
    }

    @Override
    public void onAfterDraw () {
        super.onAfterDraw();

        this.drawnControls = controls;

        this.controls = new ArrayList<>();
    }
}
