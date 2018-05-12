package com.pcc.project.ECS.Components.AssetsLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class TextureLoader implements Runnable {
    CompletableFuture< Pixmap > future;
    String                      path;

    public TextureLoader ( CompletableFuture< Pixmap > future, String path ) {
        this.future = future;
        this.path = path;
    }

    @Override
    public void run () {
        Pixmap texture = new Pixmap( Gdx.files.internal( this.path ) );

        this.future.complete( texture );
    }
}
