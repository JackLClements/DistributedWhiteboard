package simplewhiteboard;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dha13jyu
 *
 * NOTE PEER CLASS DOES SIMILAR TO WHAT P2P CLIENT DOES I SUGGEST SEPARATING THE
 * TWO AND SPLITTING INTO LOGIC AND SOMETHING ELSE A-LA THE BLACKJACK PROGRAM
 */
public class Peer implements Runnable {

    //Logical clock
    //Something else
    private ArrayList<InetAddress> peers;
    private DatagramSocket socket;
    private boolean isActive;
    private LogicalClock clock;
    
    public Peer() {
        peers = new ArrayList<InetAddress>();
    }

    public Peer(InetAddress myAddress) {
        peers = new ArrayList<InetAddress>();
        peers.add(myAddress);
    }

    public Peer(InetAddress myAddress, DatagramSocket socket) {
        this.socket = socket;
        peers = new ArrayList<InetAddress>();
        peers.add(myAddress);//maybe not best way to do it, set externally
        isActive = true;
    }
    
    public void setClock(LogicalClock clock){
        this.clock = clock;
    }
    
    /**
     * Packet structure - ID. 1 byte 0
     * Own IP, bytes 1-4
     * @param entryPoint 
     */
    public void sendJoin(InetAddress entryPoint){
        byte [] joinMsg = new byte[5];
        joinMsg[0] = 1;
        for(int i = 0; i < 4; i++){
            joinMsg[i+1] = peers.get(0).getAddress()[i];
        }
        send(joinMsg, entryPoint);
        System.out.println("JOINING - " + entryPoint.toString());
    }
    
    public void recieveJoin(byte [] msg){
        byte[] IP = new byte[]{msg[1], msg[2], msg[3], msg[4]};
        try {
            InetAddress joiner = InetAddress.getByAddress(IP);
            peers.add(joiner);
            gossipPeer();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        System.out.println("Join recieved!");
        
    }
    
    //When node joins system it should
    //Get total time & update clock accordingly
    //Ask nearest peer to relay all WB progress thus far inc. ordering
    //

    public void addPeer(InetAddress peer) {
        if (!peers.contains(peer)) {
            peers.add(peer);
        }
    }

    /**
     * Recieve Type Message Propagated via gossip - remove peer when done
     */
    public void removePeer(InetAddress peer) {
        for (int i = 0; i < peers.size(); i++) {
            if (peers.get(i).equals(peer)) {
                peers.remove(i);
            }
        }
    }

    /**
     * Recieve type message Propagated via gossip - update peer list
     */
    public void recievePeer(byte[] peerMessage) {
        //remember byte 0 - ID
        //byte 1 - no. of entrants
        InetAddress tempIP;
        //NOTE - THIS IMPLEMENTATION DOES NOT SCALE PAST 127 peers per network
        int noOfEntries = (int) peerMessage[1];
        for (int i = 2; i < noOfEntries; i += 4) {
            byte[] IP = new byte[]{peerMessage[i], peerMessage[i + 1], peerMessage[i + 2], peerMessage[i + 3]};
            try {
                tempIP = InetAddress.getByAddress(IP);
                addPeer(tempIP);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Gossip'd");
        for (InetAddress peer : peers) {
            System.out.println(peer.toString());
        }
    }

    /**
     * Send message Propagated via gossip - send a gossip of peers
     */
    public void gossipPeer() {
        //select random peer?
        //when recieving a packet set the buffer to highest expected value - 1
        //https://stackoverflow.com/questions/13494323/udp-packet-separation

        //inetaddress is 4 bytes long in an array
        //DatagramPacket packetSend = new DatagramPacket(data, data.length, peers.get(i), 0000);
        //socket.send(packetSend);
        //
        byte[] peerList = new byte[(peers.size() * 4) + 2];
        peerList[0] = 2;
        peerList[1] = (byte) peers.size();
        int peerIterator = 0;
        for (int i = 2; i < peers.size(); i += 4) { //iterate through positions
            byte[] peerIP = peers.get(peerIterator).getAddress();
            for (int j = 0; j < 4; j++) {
                peerList[i + j] = peerIP[j];
            }
            peerIterator++;
        }
        for (InetAddress peer : peers) {
            send(peerList, peer);
        }
        System.out.println("SENT!");
    }
    
    /**
     * Packet structure response - (4) type of msg int, response 1 true 0 false, next 8 bytes long corresponding timestamp 
     * @param sender
     * @param timestamp
     * @param response 
     */
    public void sendVote(InetAddress sender, long timestamp, boolean response){
        byte [] vote = new byte[10];
        vote[0] = 4; //message type
        if(response){
            vote[1] = 1;
        }
        else{
            vote[1] = 0;
        }
        //allocate byte to whatever
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(timestamp);
        byte [] longArray = buffer.array();
        for(int i = 0; i < longArray.length; i++){
            vote[i+2] = longArray[i];
        }
        send(vote, sender);
    }
    
    public void recieveVote(byte [] msg){
        //pass to clock
        boolean response = false;
        if(msg[1] == 1){
            response = true;
        }
        byte [] longBytes = new byte[8];
        for(int i = 0; i < 8; i++){
            longBytes[i] = msg[i+2];
        }
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(longBytes);
        buffer.flip();//need flip 
        long timestamp = buffer.getLong();
        clock.processVote(timestamp, response);
    }
    
    /**
     * Constructs vote request
     * Calls clock for next timestamp and sets up parameters for clock polling
     */
    public void sendVoteRequest(){
         byte [] voteReqByte = new byte[13];
         voteReqByte[0] = 5;
         for(int i = 0; i < 4; i++){
             voteReqByte[i+1] = peers.get(0).getAddress()[i];
         }
         //call Clock for time wanted
         long stamp = clock.requestTimestamp();
         ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
         buffer.putLong(stamp);
         byte [] longArray = buffer.array();
         for(int i = 0; i < longArray.length; i++){
             voteReqByte[i+5] = longArray[i];
         }
         for (int i = 1; i < peers.size(); i++) {
            InetAddress peer = peers.get(i);
            send(voteReqByte, peer);
         }
         System.out.println("Vote ID - " + voteReqByte[0]);
         System.out.println("IP - " + voteReqByte[1] + "." + voteReqByte[2] + "." + voteReqByte[3] + "." + voteReqByte[4]);
         System.out.println("Timestamp - " + stamp);
    }
    
    /**
     * Packet structure vote request
     * ID (decide) 5
     * 4 BYTES - IP ADDRESS
     * Long clock time wanted
     * 13 bytes long?
     * 
     */
    public void recieveVoteRequest(byte [] msg) throws UnknownHostException{
        InetAddress sender = InetAddress.getByAddress(new byte[]{msg[1], msg[2],msg[3], msg[4]});
        System.out.println("RECIEVED VOTE REQ FROM IP - " + sender.toString());
        byte [] longBytes = new byte[8];
        for(int i = 5; i < 13; i++){
            longBytes[i-5] = msg[i];
        }        
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(longBytes);
        buffer.flip();//need flip 
        long timestamp = buffer.getLong();
        
        //now we have 
        //timestamp - timestamp requested
        //sender - sender of request
        boolean response = clock.pollClock(timestamp);
        System.out.println(response);
        sendVote(sender, timestamp, response);
    }

    /**
     * Send message Propagated via gossip - send leave, then leave
     */
    public void leave() {
        byte[] leaveMsg = new byte[5];
        leaveMsg[0] = 3;
        InetAddress myInetAddress = peers.get(0);
        for (int i = 1; i < 5; i++) {
            leaveMsg[i] = myInetAddress.getAddress()[i - 1];
        }
        for (InetAddress peer : peers) {
            send(leaveMsg, peer);
        }
        //send close to UI thread
    }

    public void send(byte[] data, InetAddress reciever) {
        DatagramPacket packetSend = new DatagramPacket(data, data.length, reciever, 8888); //last one is port
        try {
            //incomingPacket.getPort()
            socket.send(packetSend);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void connectToPeer(InetAddress peer) {
        //send packet saying hello, pls gossip nodes

    }

    public void peerConnection() {
        //unpack packet, add to list
        //gossip to specific
    }

    /**
     * LIST OF PACKET TYPES 
     * 1. Connection msg - add to peer list, send list of peers 
     * 2. Gossip peer msg - update peer list 
     * 3. Leave msg - leave 
     * 4. Vote msg
     * 5. Poll msg
     *
     * @param msg
     */
    public void processMessage(byte[] msg) {
        byte msgType = msg[0];
        switch (msgType) {
            case 0:
                break;
            case 1:
                recieveJoin(msg);
                break;
            case 2:
                recievePeer(msg);
                break;
            case 3: {
                try {
                    removePeer(InetAddress.getByAddress(new byte[]{msg[1], msg[2], msg[3], msg[4]}));
                } 
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            case 4:
                recieveVote(msg);
                break;
            case 5:
        {
            try {
                recieveVoteRequest(msg);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
        }
                break;
            default:

        }
    }

    @Override
    public void run() {
        //read incoming msgs
        //unpack according to type
        //gossipPeer();
        sendVoteRequest();
        while (isActive) {
        
            try {
                byte[] buffer = new byte[256]; //length seems arbitrary, may be set once we know length of packet
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                processMessage(packet.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
