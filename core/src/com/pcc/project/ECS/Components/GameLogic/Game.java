package com.pcc.project.ECS.Components.GameLogic;

import com.badlogic.gdx.Gdx;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

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
    }
}
