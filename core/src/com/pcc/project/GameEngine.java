package com.pcc.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Prefabs.GameObject;

public class GameEngine implements Disposable {
    protected Entity root;

    protected int exceptionCount = 0;

    public GameEngine () {
        this.root = new GameObject().instantiate();
    }

    public Entity getRoot () {
        return this.root;
    }

    public void render () {
        Gdx.gl.glClearColor( 0.8f, 0.8f, 0.8f, 1 );

        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | ( Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0 ) );

        try {
            this.root.onAwake();
            this.root.onUpdate();
            this.root.onDraw();

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
        this.root.destroy();


    }
}
