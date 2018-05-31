package com.pcc.project.ECS.Components.Network;

import com.badlogic.gdx.Gdx;
import com.pcc.project.ECS.Component;
import com.pcc.project.ECS.Entity;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkManager extends Component {
    protected String serverAddress;

    protected int serverPort;

    protected LinkedBlockingQueue< List< Map< String, String > > > outgoingMessages;

    protected LinkedBlockingQueue< List< Map< String, String > > > incomingMessages;

    protected NetworkSender networkSender;

    protected NetworkReceiver networkReceiver;

    public NetworkManager ( Entity entity, String name ) {
        super( entity, name );

        this.outgoingMessages = new LinkedBlockingQueue<>( 10 );
        this.incomingMessages = new LinkedBlockingQueue<>( 10 );
    }

    public NetworkManager setHost ( String address, int port ) {
        this.serverAddress = address;
        this.serverPort = port;

        return this;
    }

    public void send ( List< Map< String, String > > message ) {
        // Offer() returns true or false depending on whether the queue had capacity to fit the item
        // If for any reason the queue is overflowing, we don't want the game to stop
        // We also do not want to introduce a memory leak
        // Thus, the less evil approach is to silently ignore the messages and hope that
        // the server and client eventually reach consistency, even if it means
        // that some commands were not ignored

        // The alternative, the put() method, would block the process until able to store the message
        this.outgoingMessages.offer( message );
    }

    public boolean hasMail () {
        return !this.incomingMessages.isEmpty();
    }

    public List< Map< String, String > > receive () {
        // Poll() returns the next item in the queue, if available, or null, if the queue is empty.
        // Once again, we do not want the rendering thread to block until a message is received,
        // and thus we use poll() instead of take(), which would block
        return this.incomingMessages.poll();
    }

    @Override
    public void onAwake () {
        super.onAwake();

        if ( this.serverAddress != null ) {
            try {
                Socket socket = new Socket( this.serverAddress, this.serverPort );

                // Creates two threads (one for sending, another for receiving messages)
                this.networkSender = new NetworkSender( this.outgoingMessages, socket );
                this.networkReceiver = new NetworkReceiver( this.incomingMessages, socket );

                // And starts both of them
                this.networkSender.start();
                this.networkReceiver.start();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } else {
            Gdx.app.error( "NetworkManager", "No server address and/or port set." );
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        if ( this.networkReceiver.isAlive() ) {
            this.networkReceiver.interrupt();
        }

        if ( this.networkSender.isAlive() ) {
            this.networkSender.interrupt();
        }
    }
}
