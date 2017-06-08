/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpconnections;

import java.io.Serializable;

/**
 *
 * @author Jack L. Clements
 */
public abstract class Message implements Serializable{
    static final long serialVersionUID = 102;
    protected String header;
    
    protected Message(String header){
        this.header = header;
    }
    
    public String getHeader(){
        return this.header;
    }
}
