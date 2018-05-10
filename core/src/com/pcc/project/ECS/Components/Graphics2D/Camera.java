package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class Camera extends Component {
    public static String defaultName = "camera";

    protected Transform transform;

    protected OrthographicCamera cam;

    protected Viewport viewport;

    protected float zoom = 1;

    public Camera ( Entity entity, String name ) {
        super( entity, name );

        cam = new OrthographicCamera();

        cam.zoom = this.zoom;

        this.setViewport( new FitViewport( 200, 200, cam ) );
    }

    public OrthographicCamera getInternalCamera () {
        return this.cam;
    }

    public Viewport getViewport () {
        return this.viewport;
    }

    public Camera setViewport ( Viewport viewport ) {
        this.viewport = viewport;

        this.viewport.apply();

        return this;
    }

    public Transform getTransform () {
        return this.transform;
    }

    public Vector2 screenToGlobal ( Vector2 screen ) {
        return this.getViewport().unproject( screen );
    }

    public Vector2 screenToLocal ( Vector2 screen ) {
        return this.screenToLocal( screen, this.transform );
    }

    public Vector2 screenToLocal ( Vector2 screen, Transform transform ) {
        return transform.globalToLocalPoint( this.screenToGlobal( screen ) );
    }

    public Vector2 screenToLocal ( Vector2 screen, Entity entity ) {
        Transform transform = entity.getComponentInParent( Transform.class );
        if ( transform != null ) {
            return this.screenToLocal( screen, transform );
        } else {
            return this.screenToGlobal( screen );
        }
    }

    @Override
    public void onAwake () {
        this.transform = this.entity.getComponentInParent( Transform.class );
    }

    public OrthographicCamera getCam () {
        if ( this.transform != null ) {
            // TODO figure out a way to keep LibGDX's camera in sync with the global position and rotation of
            // the transform component
            // Also, maybe scaling could be viewed as zooming


//            Vector2 gPos = this.transform.getGlobalPosition();
//
//            boolean update = false;

//            if ( gPos.x != cam.position.x || gPos.y != cam.position.y ) {
//                cam.position.set( gPos, cam.position.z );
//
//                update = true;
//            }
//
//            if ( this.zoom != cam.zoom ) {
//                cam.zoom = this.zoom;
//
//                update = true;
//            }

//            cam.rotate( new Vector2( cam.direction.x, cam.direction.y ).angle() );

//            if ( update ) {
//                cam.update();
//            }
        }

        return this.cam;
    }
}
