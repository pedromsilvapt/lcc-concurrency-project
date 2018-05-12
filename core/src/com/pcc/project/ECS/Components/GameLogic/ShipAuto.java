package com.pcc.project.ECS.Components.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Utils.TimeStateMachine;

/**
 * The state for the ship will be store in this component.
 * Based on the current state, it will calculate the next state
 * When a state update arrives from the network manager, it replaces the current state, whatever it may be.
 * The component should continue the simulation without issues
 * */
public class ShipAuto extends Component {
    public enum Thruster {
        Idle("idle"), On("on");

        protected String state;

        private Thruster ( String state ) {
            this.state = state;
        }

        public String getState () {
            return this.state;
        }

        public static Thruster fromState ( String state ) {
            switch ( state ) {
                case "idle": return Thruster.Idle;
                case "on": return Thruster.On;
                default:return Thruster.Idle;
            }
        }
    }

    protected Thruster mainThrusterState = Thruster.Idle;

    protected Thruster leftThrusterState = Thruster.Idle;

    protected Thruster rightThrusterState = Thruster.Idle;

    private float maxVelocity = 20;

    private Vector2 linearVelocity = new Vector2( 0, 0 );

    private TimeStateMachine machine;

    public ShipAuto ( Entity entity, String name ) {
        super( entity, name );
    }

    public Sprite mainEngineSprite;

    public ShipAuto setMainThrusterState ( Thruster state ) {
        this.mainThrusterState = state;

        return this;
    }

    public ShipAuto setLeftThrusterState ( Thruster state ) {
        this.leftThrusterState = state;

        return this;
    }

    public ShipAuto setRightThrusterState ( Thruster state ) {
        this.rightThrusterState = state;

        return this;
    }

    @Override
    public void onAwake () {
        super.onAwake();

//        this.machine = new TimeStateMachine(  );
//
//        this.machine.addState( "engineOn", 1.5f );
//        this.machine.addState( "engineOff", 0.4f );
//        this.machine.addState( "rotating", 1.2f );
//        this.machine.addState( "engineOn", 4 );
    }

    private boolean running = false;

    @Override
    public void onUpdate () {
        super.onUpdate();

        boolean test = true;

        // TODO remove test code

        if ( Gdx.input.isKeyJustPressed( Input.Keys.SPACE ) ) {
            this.running = !this.running;
        }


        if ( test && this.running ) {
            float d = Gdx.graphics.getDeltaTime() * 5;


            this.machine.update( Gdx.graphics.getDeltaTime() );

            String state = this.machine.getState();

            Transform t = this.entity.getComponent( Transform.class );

            if ( state == null ) {
                return;
            }

            this.mainEngineSprite.setOpacity( 0 );

            switch ( state ) {
                case "engineOn":
                    Vector2 forward = t.getForward().rotate( 90 ).scl( d );

                    this.linearVelocity.add( forward );

                    float vel = this.linearVelocity.len();

                    if ( vel > this.maxVelocity ) {
                        this.linearVelocity.scl( this.maxVelocity / vel );
                    }

                    t.getPosition().add( this.linearVelocity );

                    this.mainEngineSprite.setOpacity( 1 );
                    break;
                case "rotating":
                    t.setRotation( t.getRotation() - ( 25 * d ) );

                    break;
                case "engineOff":
                    if ( this.linearVelocity.len() > 0 ) {
                        this.linearVelocity.sub( this.linearVelocity.cpy().nor().scl( d ) );
                    }

                    break;
            }

            if ( this.linearVelocity.len() > 0 ) {
                this.linearVelocity.setAngle( t.getRotation() + 90 );

                t.getPosition().add( this.linearVelocity );
            }


            t.invalidate();
        }
    }
}
