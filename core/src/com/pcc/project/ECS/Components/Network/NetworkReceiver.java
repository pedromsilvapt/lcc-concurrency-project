package com.pcc.project.ECS.Components.Network;

import com.badlogic.gdx.Gdx;
import com.pcc.project.NetworkMessages;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class NetworkReceiver extends Thread {
    protected BlockingQueue< List< Map< String, String > > > queue;
    protected Socket                                         socket;

    public NetworkReceiver ( BlockingQueue< List< Map< String, String > > > queue, Socket socket ) {
        this.queue = queue;
        this.socket = socket;
    }

    public void run () {
        try {
            BufferedReader in = new BufferedReader( new InputStreamReader( this.socket.getInputStream() ) );
            String         line;

            while ( ( line = in.readLine() ) != null ) {
                Gdx.app.log( "Receiver", line );

                this.queue.put( NetworkMessages.parse( line ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 0 );
        }
    }
}
