package com.pcc.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Prefabs.GameWorld;

import java.util.concurrent.locks.ReentrantLock;

public class PccProject extends ApplicationAdapter {
    Entity gameWorld;

    Camera camera;

    ReentrantLock gameWorldLock = new ReentrantLock(  );

    @Override
    public void create () {
        this.gameWorld = new GameWorld().instantiate();

//        float w = Gdx.graphics.getWidth();
//        float h = Gdx.graphics.getHeight();

        // Constructs a new OrthographicCamera, using the given viewport width and height
        // Height is multiplied by aspect ratio.
//        camera = new OrthographicCamera(30, 30 * (h / w));
//
//        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
//        camera.update();
    }

    @Override
    public void resize ( int width, int height ) {
        super.resize( width, height );
    }

    @Override
    public void render () {
        Gdx.graphics.setTitle( "Pcc Project " + Gdx.graphics.getFramesPerSecond() );

        this.gameWorldLock.lock();

        try {
            this.gameWorld.onAwake();
            this.gameWorld.onUpdate();
            this.gameWorld.onDraw();
        } finally {
            this.gameWorldLock.unlock();
        }
    }

    @Override
    public void dispose () {
        this.gameWorldLock.lock();

        try {
            this.gameWorld.onDestroy();
        } finally {
            this.gameWorldLock.unlock();
        }
    }
}
