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

    protected int exceptionCount = 0;

    boolean customCursor = true;

    @Override
    public void create () {
        this.gameWorld = new GameWorld( this.customCursor ).instantiate();
    }

    @Override
    public void resize ( int width, int height ) {
        super.resize( width, height );
    }

    @Override
    public void render () {
        Gdx.graphics.setTitle( "Pcc Project " + Gdx.graphics.getFramesPerSecond() );

//        Gdx.input.setCursorCatched( this.customCursor );

        Gdx.gl.glClearColor( 0.8f, 0.8f, 0.8f, 1 );

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        try {
            this.gameWorld.onAwake();
            this.gameWorld.onUpdate();
            this.gameWorld.onDraw();

            exceptionCount = 0;
        } catch ( Exception e ) {
            if ( exceptionCount++ > 20 ) {
                throw e;
            } else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose () {
        this.gameWorld.onDestroy();
    }
}
