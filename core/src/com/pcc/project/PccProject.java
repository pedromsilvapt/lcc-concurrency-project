package com.pcc.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Prefabs.GameWorld;

import java.util.concurrent.locks.ReentrantLock;

public class PccProject extends ApplicationAdapter {
    Entity gameWorld;

    GameEngine engine;

    boolean customCursor = true;

    boolean enableDebug = false;

    @Override
    public void create () {
        GameWorld world;

        this.engine = new GameEngine();

        this.enableDebug = System.getProperty( "debug", "false" ).equalsIgnoreCase( "true" );

        String host = System.getProperty( "host" );

        if ( host != null ) {
            String[] parts = host.split( ":" );

            String address = parts[ 0 ];
            int port = Integer.parseInt( parts[ 1 ] );

            world = new GameWorld( this.customCursor, address, port );
        } else {
            world = new GameWorld( this.customCursor );
        }

        this.engine.getRoot().instantiate( world.setEnabledDebug( this.enableDebug ) );
    }

    @Override
    public void resize ( int width, int height ) {
        super.resize( width, height );
    }

    @Override
    public void render () {
        Gdx.graphics.setTitle( "Pcc Project " + Gdx.graphics.getFramesPerSecond() );

        this.engine.render();
    }

    @Override
    public void dispose () {
        try {
            this.engine.dispose();
        } finally {
            this.engine = null;
        }
    }
}
