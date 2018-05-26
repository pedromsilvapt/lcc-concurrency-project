package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.pcc.project.ECS.Components.Graphics2D.VisualComponent;
import com.pcc.project.ECS.Entity;

import javax.swing.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Window extends VisualComponent{

    public Window(Entity entity, String name) {
        super(entity, name);

        JFrame frame = new JFrame(name);
        // frame.add(entity);
        frame.pack();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Window setSize(Size size){
        super.setSize(size);

        return this;
    }

    public Window setSize (float width, float height){

        return this.setSize(new Size(width, height));
    }



}
