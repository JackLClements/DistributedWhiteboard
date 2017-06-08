//Idea is to decouple network code from program logic
package tcpconnections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import simplewhiteboard.SimpleWhiteboardDemo;

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
    private SimpleWhiteboardDemo demo;
    //Table object, set by connection manager, has references to connection objects a-la doubly linked list, 
    
    /**
     * Default constructor, sets up ID only.
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
    
    public void setDemo(SimpleWhiteboardDemo demo){
        this.demo = demo;
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
    public void send(Message msg){
        try{
            out.writeObject(msg);
            out.reset();
        }
        catch(Exception e){
            e.printStackTrace();
        } 
    }
    
    //TODO CAST UNPACK TO SOMETHING SENSIBLE (BYTESTREAM)
    /**
     * Cast/Upack this
     * @return Input cast to Message
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public Object recieve() throws IOException, ClassNotFoundException {
        return  in.readObject();
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
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error closing connection object");
            e.printStackTrace();
        }

    }
    


    /**
     * Thread that recieves messages, extracts it as a "message" object and
     * then passes onto the table. It is not concerned with the specific
     * object type nor its contents.
     */
    @Override
    public void run() {
        try {
            
            while (active) {
                
                ArrayList<WBSuperpeer> peers;
                
                peers = (ArrayList<WBSuperpeer>) in.readObject();
                //pass into GUI thread
                //for now, use print loop
                System.out.println(peers.size());
                for(WBSuperpeer peer : peers){
                    System.out.println(peer.getName() + " " + peer.getAddress());
                }
                
                //System.out.println("LET'S SEND THE TIME WARP AGAIN");
                Scanner scan = new Scanner(System.in);
                int selection = scan.nextInt();
                if(selection == 0){
                    //start new server
                    //set superpeer class to true
                    demo.startSPeer();
                    send(new SuperpeerMessage("Become Superpeer", "Channel Name", socket.getLocalAddress()));
                }
                else{
                    //join P2P network listed
                    //implement methods
                    demo.startPeer(peers.get(selection-1).getAddress());
                    send(new DisconnectMessage("Disconnected."));
                }
                //Reads in message, processes from stream, etc.

                //should pass out
                
                close();
            }

        } catch (Exception e) {
            close();
            e.printStackTrace();
            System.out.println("Error: Unexpected server disconnect.");
        }
    }

}
