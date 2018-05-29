package com.pcc.project.ECS.Components.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.Graphics2D.Renderer2D;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Utils.Resource;
import com.pcc.project.Utils.Velocity;

/**
 * The state for the ship will be store in this component.
 * Based on the current state, it will calculate the next state
 * When a state update arrives from the network manager, it replaces the current state, whatever it may be.
 * The component should continue the simulation without issues
 */
public class Ship extends Component {
    public enum Thruster {
        Idle( "idle" ), On( "on" );

        protected String state;

        private Thruster ( String state ) {
            this.state = state;
        }

        public String getState () {
            return this.state;
        }

        public static Thruster fromState ( String state ) {
            switch ( state ) {
                case "idle":
                    return Thruster.Idle;
                case "on":
                    return Thruster.On;
                default:
                    return Thruster.Idle;
            }
        }
    }

    protected Thruster mainThrusterState = Thruster.Idle;

    protected Thruster leftThrusterState = Thruster.Idle;

    protected Thruster rightThrusterState = Thruster.Idle;

    protected Velocity mainThrusterVelocity = new Velocity( 1000, 50f, 1500 );

    protected Velocity leftThrusterVelocity = new Velocity( 600, 600f, 800 );

    protected Velocity rightThrusterVelocity = new Velocity( 600, 600f, 800 );

    protected Resource energy = new Resource( 100, 100, 1, 1 );

    public Ship ( Entity entity, String name ) {
        super( entity, name );
    }

    public Sprite mainEngineSprite;

    public Resource getEnergy () {
        return energy;
    }

    public Thruster getMainThrusterState () {
        return mainThrusterState;
    }

    public Ship setMainThrusterState ( Thruster state ) {
        this.mainThrusterState = state;

        return this;
    }

    public Thruster getLeftThrusterState () {
        return leftThrusterState;
    }

    public Ship setLeftThrusterState ( Thruster state ) {
        this.leftThrusterState = state;

        return this;
    }

    public Thruster getRightThrusterState () {
        return rightThrusterState;
    }

    public Ship setRightThrusterState ( Thruster state ) {
        this.rightThrusterState = state;

        return this;
    }

    public Ship setEnergyCapacity ( float energyCapacity ) {
        this.energy.setCapacity( energyCapacity );

        return this;
    }

    public Ship setEnergyAmount ( float energyAmount ) {
        this.energy.setAmount( energyAmount );

        return this;
    }

    public Ship setAcceleration ( float acceleration ) {
        this.mainThrusterVelocity.setAcceleration( acceleration );

        return this;
    }

    public Velocity getMainThrusterVelocity () {
        return mainThrusterVelocity;
    }

    @Override
    public void onAwake () {
        super.onAwake();
    }

    private boolean running = false;

    @Override
    public void onUpdate () {
        super.onUpdate();

        float d = Gdx.graphics.getDeltaTime();

        Transform  t        = this.entity.getComponent( Transform.class );
        Renderer2D renderer = this.entity.getComponentInParent( Renderer2D.class );

        if ( this.mainThrusterState == Thruster.On && !this.energy.isEmpty() ) {
            this.mainThrusterVelocity.accelerate( d, t.getForward().rotate( 90 ) );

            this.energy.drain( d );

            this.mainEngineSprite.setOpacity( Math.min( this.mainEngineSprite.getOpacity() + d * 5, 1 ) );
        } else {
            this.mainThrusterVelocity.deaccelerate( d );

            this.energy.recharge( d );

            this.mainEngineSprite.setOpacity( Math.max( this.mainEngineSprite.getOpacity() - d * 5, 0 ) );
        }

        if ( this.leftThrusterState == Thruster.On ) {
            this.leftThrusterVelocity.accelerate( d, Vector2.X );
        } else {
            this.leftThrusterVelocity.deaccelerate( d );
        }

        if ( this.rightThrusterState == Thruster.On ) {
            this.rightThrusterVelocity.accelerate( d, Vector2.X );
        } else {
            this.rightThrusterVelocity.deaccelerate( d );
        }

        renderer.debugRenderer.drawVector( Color.RED, t.getParentTransform().globalToLocalPoint( t.localToGlobalPoint( new Vector2() ) ), this.leftThrusterVelocity.getVelocity().cpy().sub( this.rightThrusterVelocity.getVelocity() ), t.getParentTransform() );
        renderer.debugRenderer.drawVector( Color.RED, t.getParentTransform().globalToLocalPoint( t.localToGlobalPoint( new Vector2() ) ), this.mainThrusterVelocity.getVelocity().cpy(), t.getParentTransform() );

        if ( this.rightThrusterVelocity.isMoving() || this.leftThrusterVelocity.isMoving() ) {
            t.setRotation( t.getRotation() - this.leftThrusterVelocity.getVelocity().x * d + this.rightThrusterVelocity.getVelocity().x * d );
        }

        if ( this.mainThrusterVelocity.isMoving() ) {
            t.getPosition().add( this.mainThrusterVelocity.getVelocity().cpy().scl( d ) );

            t.invalidate();
        }

    }
}
