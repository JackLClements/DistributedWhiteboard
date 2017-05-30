/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboardentrypoint;

import tcpconnections.WBSuperpeer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Jack L. Clements
 */
public class ConnectionManager implements Runnable {

    private ServerSocket socket;
    private static ArrayList<WBSuperpeer> whiteboards = new ArrayList<>();

    public ConnectionManager() throws IOException {
        socket = new ServerSocket(55552, 1);
    }
    
    public static synchronized void addNewPeer(WBSuperpeer peer){
        whiteboards.add(peer);
        System.out.println("New Peer");
    }
    
    //Either want public static or to update and pass to threads via listener
    //Decide later yo
    public static synchronized ArrayList<WBSuperpeer> getSuperpeers(){
        return whiteboards;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //note - bad structure, come back to this and fix it later
                
                Socket connection = socket.accept();
                System.out.println("Connection found.");
                //blocks until connection
                
                //logic should now ask connection what it wants to do, spin up new superpeer if needed otherwise
                //pass to seperate thread...
                //
                Connection con = new Connection(connection);
                new Thread(con).start();
                System.out.println("Spinning up new thread");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
