package simplewhiteboard;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author dha13jyu
 * 
 * NOTE PEER CLASS DOES SIMILAR TO WHAT P2P CLIENT DOES
 * I SUGGEST SEPARATING THE TWO AND SPLITTING INTO LOGIC AND SOMETHING ELSE
 * A-LA THE BLACKJACK PROGRAM
 */
public class Peer implements Runnable{
    //Logical clock
    //Something else
    private ArrayList<InetAddress> peers;
    
    
    public Peer(){
        peers = new ArrayList<InetAddress>();
    }
    
    public Peer(InetAddress myAddress){
        peers = new ArrayList<InetAddress>();
        peers.add(myAddress);
    }
    
    /**
     * Recieve Type Message
     * Propagated via gossip - remove peer when done
     */
    public void removePeer(InetAddress peer){
        for(int i = 0; i < peers.size(); i++){
            if(peers.get(i).equals(peer)){
                peers.remove(i);
            }
        }
    }
    
    /**
     * Recieve type message
     * Propagated via gossip - update peer list
     */
    public void recievePeer(){
        
    }
    
    /**
     * Send message
     * Propagated via gossip - send a gossip of peers
     */
    public void gossipPeer(){
        //select random peer?
        //when recieving a packet set the buffer to highest expected value - 1
        //https://stackoverflow.com/questions/13494323/udp-packet-separation
        
        //inetaddress is 4 bytes long in an array
        //DatagramPacket packetSend = new DatagramPacket(data, data.length, peers.get(i), 0000);
        //socket.send(packetSend);
    }
    
    /**
     * Send message
     * Propagated via gossip - send leave, then leave
     */
    public void leave(){
        
    }

    @Override
    public void run() {
        //read incoming msgs
        //unpack according to type
    }
    
    public void connectToPeer(InetAddress peer){
        //send packet saying hello, pls gossip nodes
        
    }
    
    public void peerConnection(){
        //unpack packet, add to list
        //gossip to specific
    }
    
    
    
}
