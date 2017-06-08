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

/**
 *
 * @author Jack L. Clements
 */
public class TCPConnection implements Runnable{

    @Override
    public void run() {
        InetAddress server;
        try {
            server = InetAddress.getByName("localhost"); //connect to TCP entry point
            Socket connection = new Socket(server, 55552);
            Connection con = new Connection(connection);
            new Thread(con).start();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
            
    }
    
    
}
