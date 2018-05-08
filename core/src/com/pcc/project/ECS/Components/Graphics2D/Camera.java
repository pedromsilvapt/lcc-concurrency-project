package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class Camera extends Component {
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

    @Override
    public void onAwake () {
        this.transform = this.entity.getComponentInParent( Transform.class );
    }

    public OrthographicCamera getCam () {
        if ( this.transform != null ) {
            Vector2 gPos = this.transform.getGlobalPosition();

            boolean update = false;

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

            if ( update ) {
                cam.update();
            }
        }

        return this.cam;
    }
}
