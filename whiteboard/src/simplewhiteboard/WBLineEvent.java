/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplewhiteboard;

import java.awt.Color;

/**
 *
 * @author Jack L. Clements
 */
public class WBLineEvent extends WBEvent {
    private int x2;
    private int y2;
    
    
    public WBLineEvent(int x, int y, Color colour) {
        super(x, y, colour);
    }
    
    public WBLineEvent(int x, int y, int x2, int y2, Color colour) {
        super(x, y, colour);
        this.x2 = x2;
        this.y2 = y2;
    }
    
    public int[] getEndpoint(){
        return new int[]{x2, y2};
    }
    
}
