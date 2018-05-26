package com.pcc.project.ECS.Components.GameLogic;

import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

public class User extends Component {
    protected String username;

    protected String password;

    protected int level;

    public User ( Entity entity, String name ) {
        super( entity, name );
    }

    public String getUsername () {
        return this.username;
    }

    public String getPassword () {
        return this.password;
    }

    public int getLevel () {
        return this.level;
    }

    public User setUsername ( String username ) {
        this.username = username;

        return this;
    }

    public User setLevel ( int level ) {
        this.level = level;

        return this;
    }

    public User setPassword ( String password ) {
        this.password = password;

        return this;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( o == this ) return true;

        if ( !( o instanceof User ) ) {
            return false;
        }

        User u = (User)o;

        return u.getUsername().equals( this.getUsername() );
    }
}
