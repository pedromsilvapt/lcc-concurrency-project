package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class InputManager extends Component {
    InteractiveControl focused = null;

    public InputManager ( Entity entity, String name ) {
        super( entity, name );
    }

    public boolean hasFocus ( InteractiveControl control ) {
        return this.focused == control;
    }

    public void requestFocus ( InteractiveControl control ) {
        if ( this.focused != null ) {
            focused.blur();
        }

        this.focused = control;
    }

    public void requestBlur ( InteractiveControl control ) {
        if ( this.focused == control ) {
            this.focused = null;
        }
    }
}
