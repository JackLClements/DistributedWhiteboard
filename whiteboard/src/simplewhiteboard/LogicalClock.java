/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplewhiteboard;

/**
 *
 * @author dha13jyu
 */
public class LogicalClock {
    private long timestamp;
    
    public void updateClock(){
        //This method should move the local clock "timestamp" forward to the local clock
    }
    
    public void dooperation(){
        //the local clock "timestamp" should increment, perform and go
    }
    
    //Other notes
    //Store list of other, conneced nodes
    //Some sort of method to gossip new nodes across
    
    //May want to use vector clock for ordering - introduces overhead
    //Or use consensus system
    
    //Create payload class with timestamp and various payloads, (x,y) for line, radius for circle, string for text
    
    //Arguably, use IP and UDP port to identify nodes
    
    //Regular ping keepalive on all nodes on node list
    
    //Solve issue of redrawing old nodes and node-polling-drawing-order later
    
    //Ultimate draw message only sent after majority poll achieved, must be drawn by all nodes regardless
    
    //Lamport Logical clock
    //1. New process, increment and poll users
    //2. If in use or clock is outside, then return no, otherwise return yes and increment your own thus reserving it
    //when incrementing ID, you need to poll other nodes whether you //can use this node
    //if the reply is "no" you poll again 
    //remember if you have more than 50% of "yes" replies then you've won
    //poll before every operation
    //essentially you need to increment, ask every operation if you can use and check
    //when you poll, the node ought to be updated to the value reserved to be requested
}
