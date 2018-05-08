package com.pcc.project.ECS.Components.GameLogic;

import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

/**
 * The state for the ship will be store in this component.
 * Based on the current state, it will calculate the next state
 * When a state update arrives from the network manager, it replaces the current state, whatever it may be.
 * The component should continue the simulation without issues
 * */
public class Ship extends Component {
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

    protected Thruster RightThrusterState = Thruster.Idle;

    public Ship ( Entity entity, String name ) {
        super( entity, name );
    }

    @Override
    public void onUpdate () {
        super.onUpdate();
    }
}
