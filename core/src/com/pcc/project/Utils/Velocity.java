package com.pcc.project.Utils;

import com.badlogic.gdx.math.Vector2;

public class Velocity {
    protected float acceleration;

    protected float deacceleration;

    protected float maxVelocity;

    protected Vector2 velocity = new Vector2();

    public Velocity ( float acceleration, float deacceleration, float maxVelocity ) {
        this.acceleration = acceleration;
        this.deacceleration = deacceleration;
        this.maxVelocity = maxVelocity;
    }

    public void setAcceleration ( float acceleration ) {
        this.acceleration = acceleration;
    }

    public void setMaxVelocity ( float maxVelocity ) {
        this.maxVelocity = maxVelocity;
    }

    public void setVelocity ( Vector2 velocity ) {
        this.velocity = velocity;
    }

    public void setDeacceleration ( float deacceleration ) {
        this.deacceleration = deacceleration;
    }

    public boolean isMoving () {
        return this.velocity.len() > 0;
    }

    public Vector2 getVelocity () {
        return velocity;
    }

    public void accelerate ( float time, Vector2 direction ) {
        Vector2 forward = direction.cpy().scl( time * acceleration );

        this.velocity.add( forward );

        float vel = this.velocity.len();

        if ( vel > this.maxVelocity ) {
            this.velocity.scl( this.maxVelocity / vel );
        }
    }

    public void deaccelerate ( float time ) {
        if ( this.isMoving() ) {
            Vector2 force = this.velocity.cpy().nor().scl( time * deacceleration );

            if ( force.len() >= this.velocity.len() ) {
                this.velocity.set( 0, 0 );
            } else {
                this.velocity.sub( force );
            }
        }
    }
}
