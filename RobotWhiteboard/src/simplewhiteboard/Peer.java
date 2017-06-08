package simplewhiteboard;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
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
    private ArrayList<InetAddress> peers;
    private DatagramSocket socket;
    private boolean isActive;
    private LogicalClock clock;
    
    //whiteboard stuff
    SimpleWhiteboardControls simpleWhiteboard;
    
    //data structure for WB events
    private ArrayList<WBEvent> events;
    private TreeMap<Long, WBEvent> values;
    
    
    public Peer() {
        peers = new ArrayList<InetAddress>();
        events = new ArrayList<WBEvent>();
    }

    public Peer(InetAddress myAddress) {
        peers = new ArrayList<InetAddress>();
        events = new ArrayList<WBEvent>();
        peers.add(myAddress);
    }

    public Peer(InetAddress myAddress, DatagramSocket socket) {
        this.socket = socket;
        peers = new ArrayList<InetAddress>();
        peers.add(myAddress);//maybe not best way to do it, set externally
        events = new ArrayList<WBEvent>();
        isActive = true;
        values = new TreeMap<Long, WBEvent>();
    }
    
    public void setClock(LogicalClock clock){
        this.clock = clock;
    }
    
    public void setWhiteboard(SimpleWhiteboardControls simpleWhiteboard){
        this.simpleWhiteboard = simpleWhiteboard;
    }
    
    public void redraw(){
        System.out.println("REDRAWING");
        this.simpleWhiteboard.clearBoard();
        for(Map.Entry<Long, WBEvent> entry : values.entrySet()){
            if(entry.getValue() instanceof WBLineEvent){
                WBLineEvent line = (WBLineEvent) entry.getValue();
                simpleWhiteboard.drawOtherLine(new Point(line.getPoint()[0], line.getPoint()[1]), new Point(line.getEndpoint()[0], line.getEndpoint()[1]), entry.getValue().getColour());
            }
            if(entry.getValue() instanceof WBTextEvent){
                WBTextEvent text = (WBTextEvent) entry.getValue();
                simpleWhiteboard.drawOtherString(new Point(text.getPoint()[0], text.getPoint()[1]), text.getText(), text.getColour());
            }
        }
    }
    
    public void slowRedraw(){
        this.simpleWhiteboard.clearBoard();
        for(Map.Entry<Long, WBEvent> entry : values.entrySet()){
            if(entry.getValue() instanceof WBLineEvent){
                WBLineEvent line = (WBLineEvent) entry.getValue();
                simpleWhiteboard.drawOtherLine(new Point(line.getPoint()[0], line.getPoint()[1]), new Point(line.getEndpoint()[0], line.getEndpoint()[1]), entry.getValue().getColour());
            }
            if(entry.getValue() instanceof WBTextEvent){
                WBTextEvent text = (WBTextEvent) entry.getValue();
                simpleWhiteboard.drawOtherString(new Point(text.getPoint()[0], text.getPoint()[1]), text.getText(), text.getColour());
            }
            try {
            sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
    }
    
    /**
     * NOTE FOR STORAGE OF WB OPERATIONS
     * USE CLASSES IN ARRAYLIST FOR LOCAL COPY
     * WHEN SENDING OVER THE WIRE
     * COPY DATA INTO BYTE STREAMS IN FORMAT EVENT, X/Y, DATA
     * THEN ADD INTO ARRAYLIST ON OTHER END
     */
    
    /**
     * Packet structure for whiteboard line operation
     * 0 - Event no. (6)
     * 1, 2 - x1 y1 (int)
     * 3, 4 - x2, y2 (int)
     * colour - red, blue, green (5-8, 9-12, 13-16)
     * 17-25 - clock time
     * @param event
     * @param clocktime 
     */
    public void sendLine(WBLineEvent event, long clocktime){
        byte [] lineMsg = new byte[37];
        lineMsg[0] = 6;
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES*7);
        buffer.putInt(event.getPoint()[0]);
        buffer.putInt(event.getPoint()[1]);
        buffer.putInt(event.getEndpoint()[0]);
        buffer.putInt(event.getEndpoint()[1]); //4 bytes each
        buffer.putInt(event.getColour().getRed());
        buffer.putInt(event.getColour().getGreen());
        buffer.putInt(event.getColour().getBlue());
        byte [] intArray = buffer.array();
        for(int i = 0; i < intArray.length; i++){
            lineMsg[i+1] = intArray[i];
        }
        ByteBuffer buffer2 = ByteBuffer.allocate(Long.BYTES);
        buffer2.putLong(clocktime);
        byte [] longArray = buffer2.array();
        for(int i = 0; i < longArray.length; i++){
            lineMsg[i+29] = longArray[i];
        }
        for(InetAddress peer : peers){
            send(lineMsg, peer);
        }
    }
    
    public void recieveLine(byte [] msg){
        int x1, x2, y1, y2, red, green, blue;
        byte [] intBytes = new byte[]{msg[1], msg[2], msg[3], msg[4]};
        
        //Buffer ints in. Could automate, pressed for time.
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES*7);
        buffer.put(intBytes);
        buffer.flip();//need flip 
        x1 = buffer.getInt();
        buffer.clear();
        intBytes = new byte[]{msg[5], msg[6], msg[7], msg[8]};
        buffer.put(intBytes);
        buffer.flip();
        y1 = buffer.getInt();
        buffer.clear();
        intBytes = new byte[]{msg[9], msg[10], msg[11], msg[12]};
        buffer.put(intBytes);
        buffer.flip();
        x2 = buffer.getInt();
        buffer.clear();
        intBytes = new byte[]{msg[13], msg[14], msg[15], msg[16]};
        buffer.put(intBytes);
        buffer.flip();
        y2 = buffer.getInt();
        buffer.clear();
        
        intBytes = new byte[]{msg[17], msg[18], msg[19], msg[20]};
        buffer.put(intBytes);
        buffer.flip();
        red = buffer.getInt();
        buffer.clear();
        intBytes = new byte[]{msg[21], msg[22], msg[23], msg[24]};
        buffer.put(intBytes);
        buffer.flip();
        green = buffer.getInt();
        buffer.clear();
        intBytes = new byte[]{msg[25], msg[26], msg[27], msg[28]};
        buffer.put(intBytes);
        buffer.flip();
        blue = buffer.getInt();
        buffer.clear();
        
        
        //Buffer for long timestamp
        ByteBuffer buffer2 = ByteBuffer.allocate(Long.BYTES);
        byte [] longBytes = new byte[8];
        for(int i = 0; i < longBytes.length; i++){
            longBytes[i] = msg[i+29];
        }
        buffer2.put(longBytes);
        buffer2.flip();
        long timestamp = buffer2.getLong();
        System.out.println(timestamp);
        //temp colouring
        Color newColor = new Color(red, green, blue);
        WBLineEvent event = new WBLineEvent(x1, y1, x2, y2, newColor);
        values.put(timestamp, event);
        
        /*
        for(Map.Entry<Long, WBEvent> entry : values.entrySet()){
            System.out.println("Key - " + entry.getKey() + " Value - " + entry.getValue().toString());
        }*/
        
        if(timestamp > values.lastKey()+1 || timestamp < values.lastKey()){
            values.put(timestamp, event);
            System.out.println("REDRAWN");
            redraw();
        }
        else{
            values.put(timestamp, event);
            simpleWhiteboard.drawOtherLine(new Point(x1, y1), new Point(x2, y2), newColor);
        }
    }
    
     /**
     * Packet structure for whiteboard line operation
     * 0 - Event no. (7)
     * colour - focus on later lol
     * 1-4, 5-9 - x1 y1 (int)
     * 10-13, 14-17, 18-21 color (int)
     * 22-25 str length (int)
     * 26-33 - clock time
     * x-y - string
     * @param event
     * @param clocktime 
     */
    public void sendText(WBTextEvent event, long clocktime) throws UnsupportedEncodingException{
        
        byte [] strMsg = event.getText().getBytes("UTF-8");
        
        byte [] lineMsg = new byte[33 + strMsg.length];
        lineMsg[0] = 7;
        
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES*6);
        
        buffer.putInt(event.getPoint()[0]);
        buffer.putInt(event.getPoint()[1]);
        buffer.putInt(event.getColour().getRed());
        buffer.putInt(event.getColour().getGreen());
        buffer.putInt(event.getColour().getBlue());
        buffer.putInt(strMsg.length);
        byte [] intArray = buffer.array();
        for(int i = 0; i < intArray.length; i++){
            lineMsg[i+1] = intArray[i];
        }
        
        ByteBuffer buffer2 = ByteBuffer.allocate(Long.BYTES);
        buffer2.putLong(clocktime);
        byte [] longArray = buffer2.array();
        for(int i = 0; i < longArray.length; i++){
            lineMsg[i+25] = longArray[i];
        }
        
        for(int i = 0; i < strMsg.length; i++){
            lineMsg[i+33] = strMsg[i];
        }
        
        for(InetAddress peer : peers){
            send(lineMsg, peer);
        }
    }
    
     public void recieveText(byte [] msg){
        int x1, y1, red, green, blue, strLen;
        byte [] intBytes = new byte[]{msg[1], msg[2], msg[3], msg[4]};
        
        //Buffer ints in. Could automate, pressed for time.
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES*6);
        buffer.put(intBytes);
        buffer.flip();//need flip 
        x1 = buffer.getInt();
        buffer.clear();
        intBytes = new byte[]{msg[5], msg[6], msg[7], msg[8]};
        buffer.put(intBytes);
        buffer.flip();
        y1 = buffer.getInt();
        buffer.clear();
        
        //colours
        intBytes = new byte[]{msg[9], msg[10], msg[11], msg[12]};
        buffer.put(intBytes);
        buffer.flip();
        red = buffer.getInt();
        buffer.clear();
        intBytes = new byte[]{msg[13], msg[14], msg[15], msg[16]};
        buffer.put(intBytes);
        buffer.flip();
        green = buffer.getInt();
        buffer.clear();
        intBytes = new byte[]{msg[17], msg[18], msg[19], msg[20]};
        buffer.put(intBytes);
        buffer.flip();
        blue = buffer.getInt();
        buffer.clear();
        
        //no of char
        intBytes = new byte[]{msg[21], msg[22], msg[23], msg[24]};
        buffer.put(intBytes);
        buffer.flip();
        strLen = buffer.getInt();
        
        
        //Buffer for long timestamp
        ByteBuffer buffer2 = ByteBuffer.allocate(Long.BYTES);
        byte [] longBytes = new byte[8];
        for(int i = 0; i < longBytes.length; i++){
            longBytes[i] = msg[i+25];
        }
        buffer2.put(longBytes);
        buffer2.flip();
        long timestamp = buffer2.getLong();
        
        byte [] strMsg = new byte[strLen];
        for(int i = 0; i < strLen; i++){
            strMsg[i] = msg[i + 33];
        }
        
        String str = new String(strMsg, StandardCharsets.UTF_8);
        //temp colouring
        Color newColor = new Color(red, green, blue);
        
        WBTextEvent event = new WBTextEvent(x1, y1, newColor, str);
        events.add(event); //Note you cannot add "in" elements, you need to check timestamp continuity
        simpleWhiteboard.drawOtherString(new Point(x1, y1), str, newColor);
        //add to thing & draw
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
            clock.setNoOfPeers(peers.size());
            gossipPeer();
            //transfer list of events
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
        clock.setNoOfPeers(peers.size());
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
    
    public void recieveVote(byte [] msg) throws UnsupportedEncodingException{
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
    public void sendVoteRequest(WBEvent event) throws UnsupportedEncodingException{
         byte [] voteReqByte = new byte[13];
         voteReqByte[0] = 5;
         for(int i = 0; i < 4; i++){
             voteReqByte[i+1] = peers.get(0).getAddress()[i];
         }
         //call Clock for time wanted
         long stamp = clock.requestTimestamp(event);
         ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
         buffer.putLong(stamp);
         byte [] longArray = buffer.array();
         for(int i = 0; i < longArray.length; i++){
             voteReqByte[i+5] = longArray[i];
         }
         if(peers.size() == 1){
             if(event instanceof WBLineEvent){
                 sendLine((WBLineEvent) event, stamp);
             }
             if(event instanceof WBTextEvent){
                 sendText((WBTextEvent) event, stamp);
             }
         }
         else{
            for (int i = 1; i < peers.size(); i++) {
                InetAddress peer = peers.get(i);
                send(voteReqByte, peer);
            }
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
        {
            try {
                recieveVote(msg);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
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
            case 6:
                recieveLine(msg);
                break;
            case 7:
                recieveText(msg);
                break;
            case 8:
                break;
            default:

        }
    }

    @Override
    public void run() {
        //read incoming msgs
        //unpack according to type
        //gossipPeer();
        //sendVoteRequest();
        while (isActive) {
            Random rand = new Random();
            WBLineEvent line = new WBLineEvent(rand.nextInt(1000), rand.nextInt(1000), rand.nextInt(1000), rand.nextInt(1000), new Color(0, 0, 255));
            long potentialTimestamp = clock.requestTimestamp(line);
            if(peers.size() == 1){
             sendLine(line, potentialTimestamp);
             }
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
