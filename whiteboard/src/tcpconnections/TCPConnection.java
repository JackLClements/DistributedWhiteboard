/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpconnections;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import simplewhiteboard.SimpleWhiteboardDemo;

/**
 *
 * @author Jack L. Clements
 */
public class TCPConnection implements Runnable{
    public SimpleWhiteboardDemo demo;
    
    public void setDemo(SimpleWhiteboardDemo demo){
        this.demo = demo;
    }
    
    @Override
    public void run() {
        InetAddress server;
        try {
            server = InetAddress.getByName("CMPLAB1-04"); //connect to TCP entry point
            Socket connection = new Socket(server, 55552);
            Connection con = new Connection(connection);
            con.setDemo(demo);
            new Thread(con).start();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
            
    }
    
    
}
