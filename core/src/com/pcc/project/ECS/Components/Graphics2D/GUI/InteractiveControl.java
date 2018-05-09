package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Entity;

public class InteractiveControl extends Control {
    protected boolean isMouseOver = false;

    protected boolean isMouseClicking = false;

    protected boolean isFocusable = false;

    protected boolean isFocused = false;

    public InteractiveControl ( Entity entity, String name ) {
        super( entity, name );
    }

    public void onMouseEnter () { }

    public void onMouseLeave () { }

    public void onMousePress () { }

    public void onMouseRelease () { }

    public void onFocus () { }

    public void onBlur () { }

    public void focus () {
        if ( !this.isFocused && this.isFocusable ) {
            this.isFocused = true;

            this.onFocus();
        }
    }

    public void blur () {
        if ( this.isFocused ) {
            this.isFocused = false;

            this.onBlur();
        }
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        int mX = Gdx.input.getX();
        int mY = Gdx.input.getY();

        Vector2 global = this.getGlobalPosition( mX, mY );

        Rect box = this.getBoundingBox();

        if ( !this.isMouseOver && box.isWithin( global ) ) {
            this.isMouseOver = true;

            this.onMouseEnter();
        } else if ( this.isMouseOver && !box.isWithin( global ) ) {
            this.isMouseOver = false;

            if ( this.isMouseClicking ) {
                this.isMouseClicking = false;

                this.onMouseRelease();
            }

            this.onMouseLeave();
        }

        if ( this.isMouseOver ) {
            boolean pressed = Gdx.input.isButtonPressed( Input.Buttons.LEFT );

            if ( !this.isMouseClicking && pressed ) {
                this.isMouseClicking = true;

                this.focus();

                this.onMousePress();
            } else if ( this.isMouseClicking && !pressed ) {
                this.isMouseClicking = false;

                this.onMouseRelease();
            }
        }
    }
}
