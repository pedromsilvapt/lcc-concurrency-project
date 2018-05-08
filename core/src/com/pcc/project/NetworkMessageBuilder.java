package com.pcc.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkMessageBuilder {
    protected List< Map< String, String > > entities = new ArrayList<>(  );

    public NetworkMessageBuilder addEntity () {
        entities.add( new HashMap<>(  ) );

        return this;
    }

    public NetworkMessageBuilder addKey ( String key, String value ) {
        entities.get( entities.size() - 1 ).put( key, value );

        return this;
    }

    public NetworkMessageBuilder concat ( NetworkMessageBuilder builder ) {
        return this.concat( builder.get() );
    }

    public NetworkMessageBuilder concat ( List<Map<String, String>> entities ) {
        for ( Map<String, String> props : entities ) {
            this.addEntity();

            for ( Map.Entry<String, String> entry : props.entrySet() ) {
                this.addKey( entry.getKey(), entry.getValue() );
            }
        }

        return this;
    }

    public List<Map<String, String>> get () {
        return this.entities;
    }
}
