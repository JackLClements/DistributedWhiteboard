/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpconnections;

import java.net.InetAddress;

/**
 *
 * @author Jack L. Clements
 */
public class DisconnectMessage extends Message {
    static final long serialVersionUID = 103;
    private String name;
    private InetAddress ip;
    public DisconnectMessage(String header) {
        super(header);
    }
    
    public DisconnectMessage(String header, String name, InetAddress ip){
        super(header);
        this.name = name;
        this.ip = ip;
    }
    
    public String getName(){
        return this.name;
    }
    
    public InetAddress getIP(){
        return this.ip;
    }
    
}
