package simplewhiteboard;

import java.awt.Color;
import java.io.Serializable;

/**
 * Abstraction of a whiteboard event
 * Done to utilise polymorphism with ordering/data structures without
 * having to screw with bytes too much.
 * @author Jack L. Clements
 */
public class WBEvent implements Serializable {
    protected int x;
    protected int y;
    protected Color colour;
    private static final long serialVersionUID = 201;
    
    public WBEvent(int x, int y, Color colour){
        this.x = x;
        this.y = y;
        this.colour = colour;
    }
    
    public int [] getPoint(){
        return new int[]{x, y};
    }
    
    public Color getColour(){
        return this.colour;
    }
    
}
