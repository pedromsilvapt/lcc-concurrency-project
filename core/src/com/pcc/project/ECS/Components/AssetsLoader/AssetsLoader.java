package com.pcc.project.ECS.Components.AssetsLoader;

import com.badlogic.gdx.graphics.Pixmap;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AssetsLoader extends Component {
    protected int maxThreads = 4;

    protected ThreadPoolExecutor threadPool;

    public AssetsLoader ( Entity entity, String name ) {
        super( entity, name );

        this.threadPool = new ThreadPoolExecutor( 0, this.maxThreads, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>() );

    }

    public int getMaxThreads () {
        return this.maxThreads;
    }

    public AssetsLoader setMaxThreads ( int maxThreads ) {
        this.maxThreads = maxThreads;

        this.threadPool.setMaximumPoolSize( this.maxThreads );

        return this;
    }

    public CompletableFuture<Pixmap> loadTexture ( String path ) {
        CompletableFuture<Pixmap> future = new CompletableFuture<>();

        this.threadPool.execute( new TextureLoader( future, path ) );

        return future;
    }
}
