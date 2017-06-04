package simplewhiteboard;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
        clock = new LogicalClock();
    }

    public Peer(InetAddress myAddress) {
        peers = new ArrayList<InetAddress>();
        peers.add(myAddress);
        clock = new LogicalClock();
    }

    public Peer(InetAddress myAddress, DatagramSocket socket) {
        this.socket = socket;
        peers = new ArrayList<InetAddress>();
        peers.add(myAddress);
        clock = new LogicalClock();
        isActive = true;
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
                peers.add(tempIP);
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
     * LIST OF PACKET TYPES 1. Connection msg - add to peer list, send list of
     * peers 2. Gossip peer msg - update peer list 3. Leave msg - leave 4.
     *
     * @param msg
     */
    public void processMessage(byte[] msg) {
        byte msgType = msg[0];
        switch (msgType) {
            case 0:
                break;
            case 1:
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
            default:

        }
    }

    @Override
    public void run() {
        //read incoming msgs
        //unpack according to type
        gossipPeer();
        //while (isActive) {

        try {
            byte[] buffer = new byte[256]; //length seems arbitrary, may be set once we know length of packet
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            processMessage(packet.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
    }

}
