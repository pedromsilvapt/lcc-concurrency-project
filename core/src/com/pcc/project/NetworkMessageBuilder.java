package com.pcc.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkMessageBuilder {
    public static List<Map<String, String>>  frame ( String... args ) {
        NetworkMessageBuilder builder = new NetworkMessageBuilder().addFrame();

        int length = args.length % 2 == 0 ? args.length : args.length - 1;

        for ( int i = 0; i < length; i += 2 ) {
            builder.addKey( args[ i ], args[ i + 1 ] );
        }

        return builder.get();
    }

    protected List< Map< String, String > > frames = new ArrayList<>(  );

    public NetworkMessageBuilder addFrame () {
        frames.add( new HashMap<>(  ) );

        return this;
    }

    public NetworkMessageBuilder addKey ( String key, String value ) {
        frames.get( frames.size() - 1 ).put( key, value );

        return this;
    }

    public NetworkMessageBuilder concat ( NetworkMessageBuilder builder ) {
        return this.concat( builder.get() );
    }

    public NetworkMessageBuilder concat ( List<Map<String, String>> entities ) {
        for ( Map<String, String> props : entities ) {
            this.addFrame();

            for ( Map.Entry<String, String> entry : props.entrySet() ) {
                this.addKey( entry.getKey(), entry.getValue() );
            }
        }

        return this;
    }

    public List<Map<String, String>> get () {
        return this.frames;
    }
}
