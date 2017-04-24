/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplewhiteboard;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author dha13jyu
 */
public class NetworkInput implements Runnable{

    @Override
    public void run() {
        try{
            //single unicast socket for recieving 
            DatagramSocket socket = new DatagramSocket(55555);
            byte [] buffer = new byte[25]; //length seems arbitrary, may be set once we know length of packet
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            
            //check client is as expected
            InetAddress sender = packet.getAddress();
            int clientPort = packet.getPort();
            socket.close();
            
            //multicast implementation
            MulticastSocket mSocket = new MulticastSocket(55550);
            InetAddress group = InetAddress.getByName("0.0.0.0"); //look up range of multicast addresses
            mSocket.joinGroup(group);
            byte [] mBuffer = new byte[256];
            DatagramPacket mPacket = new DatagramPacket(mBuffer, mBuffer.length);   
            mSocket.receive(mPacket);
            mSocket.leaveGroup(group);
            
            /**
             * Logical clock
             * - Numbering system that assigns unique identifier to each object created 
             * - Each object assigned and sent over
             * - You can get previous/missed 
             * - You need a way to gain consensus on an ID gained by a given node
             * - Node joining can be done via "gossip", just send to all connected nodes
             */
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
}
