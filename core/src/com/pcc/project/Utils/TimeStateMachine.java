package com.pcc.project.Utils;

import java.util.ArrayList;
import java.util.List;

public class TimeStateMachine {
    public int current = 0;

    public float elapsed = 0;

    public boolean loop = false;

    static public class TimeState {
        public float time;
        public String name;

        public TimeState ( String name, float delta ) {
            this.time = delta;
            this.name = name;
        }
    }

    protected List< TimeState > states = new ArrayList<>();

    public TimeStateMachine () {
        this( false );
    }

    public TimeStateMachine ( boolean loop ) {
        this.loop = loop;
    }

    public TimeStateMachine addState ( String name, float delta ) {
        this.states.add( new TimeState( name, delta ) );

        return this;
    }

    public String getState () {
        if ( this.current < this.states.size() ) {
            return this.states.get( this.current ).name;
        }

        return null;
    }

    public void update ( float delta ) {
        float threshold;

        this.elapsed += delta;

        while ( current < this.states.size() ) {
            threshold = this.states.get( this.current ).time;

            if ( this.elapsed < threshold ) {
                break;
            }

            this.elapsed -= threshold;

            this.current++;

            if ( this.current >= this.states.size() && this.loop ) {
                this.current = 0;
            }
        }
    }

    public void start () {
        this.start( 0, 0 );
    }

    public void start ( int current ) {
        this.start( current, 0 );
    }

    public void start ( int current, int elapsed ) {
        this.current = current;
        this.elapsed = elapsed;
    }
}
