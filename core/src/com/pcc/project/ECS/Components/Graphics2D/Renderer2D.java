package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class Renderer2D extends Component {
    public SpriteBatch spriteBatch;

    public int width = 0;
    protected int height = 0;

    protected int frameCount = 0;

    public Renderer2D ( Entity entity, String name ) {
        super( entity, name );
    }

    protected Camera getCamera () {
        return this.entity.getComponentsInChildren( Camera.class )
                .stream()
                .filter( Camera::getEnabled )
                .findFirst()
                .orElse( null );
    }

    @Override
    public void onAwake () {
        super.onAwake();

        this.spriteBatch = new SpriteBatch();
    }

    @Override
    public void onDraw () {
        super.onDraw();

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        boolean resized = width != this.width || height != this.height;

        if ( resized ) {
            this.width = width;
            this.height = height;
        }

        Camera camera = this.getCamera();

        if ( camera != null ) {
            if ( resized ) {
                camera.viewport.update( this.width, this.height, true );
            }

            this.spriteBatch.setProjectionMatrix( camera.getCam().combined );
        }

        Gdx.gl.glClearColor( 0.8f, 0.8f, 0.8f, 1 );
//        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        this.spriteBatch.begin();
    }

    @Override
    public void onAfterDraw () {
        super.onAfterDraw();

        this.spriteBatch.end();

        if ( this.frameCount++ > 60 * 10 ) {
            this.spriteBatch.dispose();

            this.spriteBatch = new SpriteBatch();

            this.frameCount = 0;
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        if ( this.spriteBatch != null ) {
            this.spriteBatch.dispose();
        }
    }
}
