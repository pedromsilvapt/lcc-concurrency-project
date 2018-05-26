package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.pcc.project.ECS.Entity;

public class InteractiveControl extends Control {
    protected boolean isMouseOver = false;

    protected boolean isMouseClicking = false;

    protected boolean isFocusable = true;

    protected boolean isFocused = false;

    protected boolean autoFocus = false;

    protected InputManager inputManager;

    protected InteractiveControl tabTarget = null;

    public InteractiveControl ( Entity entity, String name ) {
        super( entity, name );
    }

    public < T extends InteractiveControl > T setTabTo ( T control ) {
        this.tabTarget = control;

        return control;
    }

    public InteractiveControl setAutoFocus ( boolean autoFocus ) {
        this.autoFocus = autoFocus;

        return this;
    }

    public void onMouseEnter () { }

    public void onMouseLeave () { }

    public void onMousePress () { }

    public void onMouseRelease () { }

    public void onFocus () {
        Gdx.app.log( "Control", "Got Focus " + this.getClass().getName() );
    }

    public void onBlur () {
        Gdx.app.log( "Control", "Lost Focus " + this.getClass().getName() );
    }

    public void focus () {
        if ( !this.isFocused && this.isFocusable ) {
            this.isFocused = true;

            this.inputManager.requestFocus( this );

            this.onFocus();
        }
    }

    public void blur () {
        if ( this.isFocused ) {
            this.isFocused = false;

            this.inputManager.requestBlur( this );

            this.onBlur();
        }
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.inputManager = this.entity.getComponentInParent( InputManager.class );

        if ( this.autoFocus ) {
            this.focus();
        }
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        boolean contains = this.inputManager.mouseFocused == this;

        if ( !this.isMouseOver && contains ) {
            this.isMouseOver = true;

            this.onMouseEnter();
        } else if ( this.isMouseOver && !contains ) {
            this.isMouseOver = false;

            if ( this.isMouseClicking ) {
                this.isMouseClicking = false;

                this.onMouseRelease();
            }

            this.onMouseLeave();
        }

        if ( this.isMouseOver ) {
            boolean pressed = this.inputManager.isMouseClick();

            if ( !this.isMouseClicking && pressed ) {
                this.isMouseClicking = true;

                this.focus();

                this.onMousePress();

                this.inputManager.releaseMouseButton();
            } else if ( this.isMouseClicking && !Gdx.input.isButtonPressed( Input.Buttons.LEFT ) ) {
                this.isMouseClicking = false;

                this.onMouseRelease();
            }
        }

        if ( this.isFocused && Gdx.input.isKeyJustPressed( Input.Keys.TAB ) && this.tabTarget != null ) {
            this.defer( () -> this.tabTarget.focus() );
        }
    }

    @Override
    public void onDraw () {
        super.onDraw();

        this.inputManager.draw( this );
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        this.blur();
    }
}
