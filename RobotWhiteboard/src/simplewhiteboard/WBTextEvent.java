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
public class WBTextEvent extends WBEvent{
    private String text;
    //private x font;
    
    public WBTextEvent(int x, int y, Color colour) {
        super(x, y, colour);
    }
    
    public WBTextEvent(int x, int y, Color colour, String text){
        super(x, y, colour);
        this.text = text;
    }
    
    public String getText(){
        return text;
    }
    
}
