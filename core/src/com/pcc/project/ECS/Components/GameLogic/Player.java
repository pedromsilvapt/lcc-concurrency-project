package com.pcc.project.ECS.Components.GameLogic;

import com.badlogic.gdx.Gdx;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;
import com.pcc.project.NetworkMessageBuilder;

import java.util.List;
import java.util.Map;

public class Player extends Component {
    protected Ship ship;

    public Player ( Entity entity, String name ) {
        super( entity, name );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.ship = this.entity.getComponent( Ship.class );

        if ( this.ship == null ) {
            Gdx.app.error( "Player", "Attaching Player component to an entity without ship." );
        }
    }

    public List<Map<String, String> > createStatusMessage ( String engine, String state ) {
        return new NetworkMessageBuilder().addEntity().addKey( "message", "input" ).addKey( engine, state ).get();
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        if ( Gdx.input.isKeyPressed( 'w' ) && ship.mainThrusterState == Ship.Thruster.Idle ) {
            Gdx.app.log( "Player", "Main Engine On" );
            ship.mainThrusterState = Ship.Thruster.On;
        } else if ( !Gdx.input.isKeyPressed( 'w' ) && ship.mainThrusterState == Ship.Thruster.On ) {
            Gdx.app.log( "Player", "Main Engine Iddle" );
            ship.mainThrusterState = Ship.Thruster.Idle;
        }
    }
}
