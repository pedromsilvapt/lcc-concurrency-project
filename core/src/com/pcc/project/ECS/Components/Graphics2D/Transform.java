package com.pcc.project.ECS.Components.Graphics2D;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class Transform extends Component {
    public static String defaultName = "transform";

    protected Matrix3 localToWorldMatrix = new Matrix3();

    protected Matrix3 worldToLocalMatrix = new Matrix3();

    protected boolean isMatrixValid = false;


    protected Vector2 position = new Vector2(  );

    protected Vector2 scale = new Vector2( 1, 1 );

    protected float rotation = 0;


    protected Vector2 globalPosition = new Vector2();

    protected float globalRotation = 0;

    protected Vector2 globalScale = new Vector2(1, 1);

    protected Transform parentTransform = null;

    public Transform ( Entity entity, String name ) {
        super( entity, name );
    }

    public Transform getParentTransform () {
        return this.parentTransform;
    }

    @Override
    public void onAwake () {
        super.onAwake();

        if ( this.entity.parent != null ) {
            this.parentTransform = this.entity.parent.getComponentInParent( Transform.class );
        }
    }

    public Matrix3 getLocalToWorldMatrix () {
        if ( !this.isMatrixValid ) {
            this.calculateMatrix();
        }

        return this.localToWorldMatrix;
    }

    public Matrix3 getWorldToLocalMatrix () {
        if ( !this.isMatrixValid ) {
            this.calculateMatrix();
        }

        return this.worldToLocalMatrix;
    }

    public Vector2 getPosition () {
        return this.position;
    }

    public Transform setPosition ( Vector2 position ) {
        this.invalidate();

        this.position = position;

        return this;
    }

    public Transform setPosition ( float x, float y ) {
        return this.setPosition( new Vector2( x, y ) );
    }

    public Vector2 getScale () {
        return this.scale;
    }

    public Transform setScale ( Vector2 scale ) {
        this.invalidate();

        this.scale = scale;

        return this;
    }

    public Transform setScale ( float x, float y ) {
        return this.setScale( new Vector2( x, y ) );
    }

    public float getRotation () {
        return this.rotation;
    }

    public Transform setRotation ( float rotation ) {
        this.invalidate();

        this.rotation = rotation;

        return this;
    }

    public Vector2 getGlobalPosition () {
        if ( !this.isMatrixValid ) {
            this.calculateMatrix();
        }

        return this.globalPosition;
    }

    public void setGlobalPosition ( Vector2 position ) {
        this.invalidate();

        if ( this.parentTransform != null ) {
            this.position = this.parentTransform.inverseTransformPoint( position );
        } else {
            this.position = position;
        }
    }

    public float getGlobalRotation () {
        if ( !this.isMatrixValid ) {
            this.calculateMatrix();
        }

        return this.globalRotation;
    }

    public void setGlobalRotation ( float rotation ) {
        this.invalidate();

        if ( this.parentTransform != null ) {
            this.rotation = this.parentTransform.inverseTransformRotation( rotation );
        } else {
            this.rotation = rotation;
        }
    }

    public Vector2 getGlobalScale () {
        if ( !this.isMatrixValid ) {
            this.calculateMatrix();
        }

        return this.globalScale;
    }

    public void invalidate () {
        this.entity.getComponentsInChildren( Transform.class ).forEach( transform -> transform.isMatrixValid = false );
    }

    protected void calculateMatrix () {
        if ( this.parentTransform != null ) {
            this.localToWorldMatrix.set( this.parentTransform.getLocalToWorldMatrix() );
        } else {
            this.localToWorldMatrix.idt();
        }

        this.localToWorldMatrix.translate( this.position );

        this.localToWorldMatrix.scale( this.scale );

        this.localToWorldMatrix.rotate( this.rotation );

        this.worldToLocalMatrix = new Matrix3( this.localToWorldMatrix ).inv();

        // Update global Position, Scale and Rotation
        this.localToWorldMatrix.getTranslation( this.globalPosition );

        this.globalRotation = this.localToWorldMatrix.getRotation();

        this.localToWorldMatrix.getScale( this.globalScale );

        this.isMatrixValid = true;
    }


    public Vector2 transformPoint ( Vector2 point ) {
        Matrix3 mat = new Matrix3( this.getLocalToWorldMatrix() ).mul( new Matrix3(  ).setToTranslation( point ) );

        return mat.getTranslation( new Vector2() );
    }

    /**
     * Transforms a local direction into a global direction
     * Directions are not affected by scale: the new direction will have the exact same length as the original one
     */
    public Vector2 transformDirection ( Vector2 direction ) {
        float degrees = this.localToWorldMatrix.getRotation();

        return new Vector2( direction ).rotate( degrees );
    }

    /**
     * Transforms a local vector into a global one
     * Vectors are not affected by positions: they are however affected by scales, and may have
     * a different length from the original one
     * @param vector
     */
    public Vector2 transformVector ( Vector2 vector ) {
        Matrix3 mat = this.getLocalToWorldMatrix();

        return new Vector2( vector ).scl( mat.getScale( new Vector2() ) );
    }

    public Vector2 inverseTransformPoint ( Vector2 vector ) {
        Matrix3 mat = this.getWorldToLocalMatrix();

        return new Matrix3( mat ).translate( vector ).getTranslation( new Vector2(  ) );
    }

    public Vector2 inverseTransformDirection ( Vector2 direction ) {
        return direction.cpy().setAngle( this.inverseTransformRotation( direction.angle() ) );
    }

    public float inverseTransformRotation ( float rotation ) {
        Matrix3 mat = this.getWorldToLocalMatrix();

        return new Matrix3( mat ).rotate( rotation ).getRotation();
    }
}
