package com.pcc.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetworkMessages {
    static List<Map<String, String>> parse ( String message ) {
        List<Map<String, String> > entities = new ArrayList<>();

        String[] entitiesMessage = message.split( ";" );

        for ( String entityMessage : entitiesMessage ) {
            Map<String, String> properties = new HashMap<>(  );
            String[] propertiesMessages = entityMessage.split( "," );

            for ( String property : propertiesMessages ) {
                String[] parts = property.split( "=", 2 );

                if ( parts.length == 2 ) {
                    properties.put( parts[ 0 ], parts[ 1 ] );
                }
            }

            entities.add( properties );
        }

        return entities;
    }

    protected static String stringifySingleProperty ( Map.Entry<String, String> entry ) {
        return String.format( "%s=%s", entry.getKey(), entry.getValue() );
    }

    protected static String stringifyProperties ( Map<String, String> properties ) {
        return properties.entrySet().stream().map( NetworkMessages::stringifySingleProperty ).collect( Collectors.joining( "," ) );
    }

    static String stringify ( List<Map<String, String>> entities ) {
        return entities.stream().map( props -> NetworkMessages.stringifyProperties( props ) ).collect( Collectors.joining( ";" ) );
    }
}
