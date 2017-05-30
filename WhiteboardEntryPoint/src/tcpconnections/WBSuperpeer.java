/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpconnections;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Abstract representation of a superpeer entry point
 * Need to know IP address of node and name of P2P network
 * Essentially a data structure thanks to Java's lack of structs
 * @author Jack L. Clements
 */
public class WBSuperpeer implements Serializable{
    static final long serialVersionUID = 101;
    private String name;
    private InetAddress address;
    
    public WBSuperpeer(String name, InetAddress address){
        this.name = name;
        this.address = address;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setAddress(InetAddress address){
        this.address = address;
    }
    
    public String getName(){
        return this.name;
    }
    
    public InetAddress getAddress(){
        return this.address;
    }
}
