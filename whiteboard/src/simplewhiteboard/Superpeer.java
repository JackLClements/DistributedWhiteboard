/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplewhiteboard;

import java.net.InetAddress;

/**
 * Class to encapsulate the superpeer functions 
 * - Join
 * - Leave
 * - TCP entry connection
 * @author Jack L. Clements
 */
public class Superpeer {
    private boolean isSuper;
    private static String RoomName;
    //private static etc. TCPEntry
    private static InetAddress entryPoint; //not final, can change
    //TCP entry point is known by all, but only knows superpeers connected
    
    public Superpeer(){
        isSuper = false;
    }
    
    public Superpeer(boolean isSuper){
        this.isSuper = isSuper;
    }
    
    public boolean isSuper(){
        return this.isSuper;
    }
    
    public void makeSuper(){
        this.isSuper = true;
    }
    
    public void takeSuper(){
        this.isSuper = false;
    }
    
    public static void setRoomName(String name){
        RoomName = name;
    }
    
    public static void setEntryPoint(InetAddress ip){
        entryPoint = ip;
    }
}
