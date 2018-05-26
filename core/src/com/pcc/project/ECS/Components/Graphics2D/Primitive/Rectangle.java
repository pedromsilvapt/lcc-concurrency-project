package com.pcc.project.ECS.Components.Graphics2D.Primitive;

import com.pcc.project.ECS.Entity;

public class Rectangle extends Primitive {
    public Rectangle ( Entity entity, String name ) {
        super( entity, name );
    }

    public float[] getVertices () {
        Size size = this.getSize();

        return new float[]{
                0, 0,
                size.width, 0,
                size.width, size.height,
                0, size.height
        };
    }
}
