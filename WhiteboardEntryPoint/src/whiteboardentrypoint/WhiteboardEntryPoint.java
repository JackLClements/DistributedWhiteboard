/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboardentrypoint;

import java.io.IOException;

/**
 *
 * @author Jack L. Clements
 */
public class WhiteboardEntryPoint {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ConnectionManager conm = new ConnectionManager();
        conm.run();
    }
    
}
