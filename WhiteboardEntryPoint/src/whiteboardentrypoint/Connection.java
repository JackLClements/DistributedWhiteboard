//Idea is to decouple network code from program logic
package whiteboardentrypoint;

import tcpconnections.WBSuperpeer;
import tcpconnections.Message;
import tcpconnections.SuperpeerMessage;
import tcpconnections.DisconnectMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For each individual connection, allows program logic to run independent of
 * network logic. Acts as facade to network-specific code, allowing other
 * classes to send/receive data without being concerned with the actual
 * network operations being performed. Increases cohesion and lowers coupling.
 *
 * @author Jack L. Clements
 */
public class Connection implements Runnable {

    //private String name;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean active;
    //Table object, set by connection manager, has references to connection objects a-la doubly linked list, 
    
    
    /**
     * Default constructor
     */
    public Connection() {
    }

    /**
     * Constructor that takes socket, sets up all other fields.
     * @param socket connection socket
     * @throws IOException if not connected
     */
    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream()); //can also use buffered reader
        in = new ObjectInputStream(socket.getInputStream());
        active = true;
    }
    
    /**
     * Accessor, setter for outputstream
     * @param out 
     */
    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }
    
    
    /**
     * Attaches socket to connection, creates new out/in streams
     * @param socket socket to be set
     * @throws IOException if not found
     */
    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        active = true;
    }
    
    //TODO SEND MESSAGE
    
    public void send(ArrayList<WBSuperpeer> list){
        try {
            out.writeObject(list);
            out.reset();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
   
    /**
     * Set new inputstream, accessor method
     * @param in 
     */
    public void setIn(ObjectInputStream in) {
        this.in = in;
    }
    
    /**
     * Closes connection alongside all relevant streams
     */
    public void close() {
        try {
            active = false;
            out.flush();
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error closing connection object");
            e.printStackTrace();
        }

    }
    
    public void processMessage(Message msg){
        if(msg instanceof SuperpeerMessage){
            String name = ((SuperpeerMessage) msg).getName();
            InetAddress ip = ((SuperpeerMessage) msg).getIP();
            WBSuperpeer peer1 = new WBSuperpeer(name, ip);
            ConnectionManager.addNewPeer(peer1);
        }
    }

    /**
     * Sends superpeer list to node, waits for response then takes action
     * Remember - once socket disconnects you no longer needed thread but
     * weird looping flow of control gave you some problems
     * 
     * May want to program in some security at some point for
     * when a irregular message is sent, but TCP is generally better
     * for that sort of thing, thus why you're using it
     */
    @Override
    public void run() {
        send(ConnectionManager.getSuperpeers());
        
        Message msg;
        try {
            msg = (Message) in.readObject();
            processMessage(msg);
        } 
        catch (Exception e){
            e.printStackTrace();
        }
        
        close();
    }

}
