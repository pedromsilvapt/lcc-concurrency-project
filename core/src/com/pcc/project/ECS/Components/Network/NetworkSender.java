package com.pcc.project.ECS.Components.Network;

import com.pcc.project.NetworkMessages;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class NetworkSender extends Thread {
    protected BlockingQueue< List< Map< String, String > > > queue;

    protected Socket socket;

    public NetworkSender ( BlockingQueue< List< Map< String, String > > > queue, Socket socket ) {
        this.queue = queue;
        this.socket = socket;
    }

    public void run () {
        try {
            PrintWriter out = new PrintWriter( this.socket.getOutputStream() );

            List< Map< String, String > > message;

            while ( true ) {
                message = this.queue.take();

                out.println( NetworkMessages.stringify( message ) );
                out.flush();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 0 );
        }
    }
}
