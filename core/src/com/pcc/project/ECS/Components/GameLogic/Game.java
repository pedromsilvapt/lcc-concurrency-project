package com.pcc.project.ECS.Components.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

import java.util.List;

public class Game extends Component {
    public Game ( Entity entity, String name ) {
        super( entity, name );
    }

    protected int score;

    protected float delta;

    protected User winner;

    protected User player;

    protected User opponent;

    public User getPlayer () {
        return this.player;
    }

    public User getOpponent () {
        return this.opponent;
    }

    public User getWinner () {
        return this.winner;
    }

    public int getScore () {
        return score;
    }

    public Game setPlayer ( User player ) {
        this.player = player;

        return this;
    }

    public Game setWinner ( User winner ) {
        this.winner = winner;

        return this;
    }

    public Game setOpponent ( User opponent ) {
        this.opponent = opponent;

        return this;
    }

    public Game setScore ( int score ) {
        this.score = score;

        return this;
    }

    @Override
    public void onUpdate () {
        super.onUpdate();

        delta += Gdx.graphics.getRawDeltaTime();

        if ( delta > 1 ) {
            score += Math.floor( delta );
            delta -= Math.floor( delta );
        }

        List<Ship> ships = this.entity.getComponentsInChildren( Ship.class );

        if ( ships.size() >= 2 ) {
            Ship player1 = ships.get( 0 );
            Ship player2 = ships.get( 1 );

            Vector2 player1Pos = player1.getTransform().getGlobalPosition().cpy();
            Vector2 player2Pos = player2.getTransform().getGlobalPosition().cpy();

            float dist = player1Pos.dst( player2Pos );

            float inv = 1 / ( dist * dist );

            Vector2 dir1 = player2Pos.sub( player1Pos ).scl( inv );
            Vector2 dir2 = player1Pos.sub( player2Pos ).scl( inv );

            player1.getTransform().getGlobalPosition().add( dir1 );
            player2.getTransform().getGlobalPosition().add( dir2 );
            player2.getTransform().invalidate();
            player1.getTransform().invalidate();

            this.setShieldOpacity( player1, inv );
            this.setShieldOpacity( player1, inv );
        }
    }

    public void setShieldOpacity ( Ship ship, float distance ) {
        ship.shieldSprite.setOpacity( 0.3f + ( Math.min( 0, Math.max( 0.4f, distance ) ) ) );
    }
}
