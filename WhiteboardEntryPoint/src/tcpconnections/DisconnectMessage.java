/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpconnections;

/**
 *
 * @author Jack L. Clements
 */
public class DisconnectMessage extends Message {
    static final long serialVersionUID = 103;
    public DisconnectMessage(String header) {
        super(header);
    }
    
}
