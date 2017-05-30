package simplewhiteboard;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Ought to handle i/o for all other clients on network
 *
 * @author dha13jyu
 */
public class P2PClient implements Runnable {

    private DatagramSocket socket;
    private boolean isRunning;
    //no sense in using hash table, but maybe just use arraylist 
    //idea is that one packet type could be is my ip list correct/
    //propogate to peers and see if you get negative response in which case update 
    //as needed
    private ArrayList<InetAddress> peers;

    public P2PClient(DatagramSocket socket) {
        this.socket = socket;
        this.isRunning = true;
        this.peers = new ArrayList<InetAddress>();
    }
        
    //Disconnect gracefully from network
    public void disconnect(){
        
    }
    
    //Need source IP to connect to
    public void connect(){
        //First connect to socket
        //Next connect to users
        //Finally populate own IPs
    }
    
    public void addConnection(InetAddress peer){
        //data structure.add ip?
        peers.add(peer);
    }
    
    /**
     * Port ought to be the same 
     * @param data 
     */
    public synchronized void  send(byte [] data){
        for(int i = 0; i < peers.size(); i++){
            try{
                //store port
                DatagramPacket packetSend = new DatagramPacket(data, data.length, peers.get(i), 0000);
                socket.send(packetSend);
            }
            catch(Exception e){
                e.printStackTrace();
            }            
        }
    }
    
    public ArrayList<InetAddress> getPeers(){
        return this.peers;
    }
    
    public void unpackPacket(DatagramPacket packet){
        byte [] data = packet.getData();
        byte msgType = data[0];
        switch(msgType){
            case 0: //draw
                break;
            case 1: //timeout
                break;
            case 2: //error 
                break;
        }
    }
    
    /**
     * Byte structure - do not serialize objects you wrote yourself
     * Header should be implicit, (maybe int)
     * Assign arbitrary identifiers in first bit
     * e.g.
     * 1 (decimal) - Whiteboard operation
     * 2 - Error msg
     * 3 - timeout msg
     * 4 - etc.
     */
    
    @Override
    public void run() {
        while (isRunning) {
            try {
                byte[] buffer = new byte[256]; //length seems arbitrary, may be set once we know length of packet
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                unpackPacket(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
