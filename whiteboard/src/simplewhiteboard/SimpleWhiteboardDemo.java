package simplewhiteboard;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.*;
import tcpconnections.TCPConnection;

public class SimpleWhiteboardDemo implements Runnable
{
  private SimpleWhiteboard simpleWhiteboard;
  private String nodename;
  private LogicalClock clock;
  private Peer peer;
  private DatagramSocket socket;

  public SimpleWhiteboardDemo(String nodename)
  {
    this.nodename = nodename;
    this.simpleWhiteboard = new SimpleWhiteboard(this.nodename, 1000, 1000);
  }

  public void run()
  {
    this.simpleWhiteboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.simpleWhiteboard.setPreferredSize(new Dimension(1000, 1000));
    this.simpleWhiteboard.pack();
    this.simpleWhiteboard.setVisible(true);
    
    //network stuff
    //note whiteboard lag is not the fault of the network it's the fault of the java stuff
    try{
        this.socket = new DatagramSocket(8888);
        InetAddress addr = InetAddress.getByName("DESKTOP-G6CK49G");
        this.peer = new Peer(addr, socket);
        this.clock = new LogicalClock();
        clock.setPeer(peer);
        peer.setClock(clock);
        //new Thread(peer).start();
    }
    catch(Exception e){
        e.printStackTrace();
    }
    
  }

  public static void main(String[] args) throws Exception
  {
      /*
      TCPConnection tcp = new TCPConnection();
    tcp.run();
    */
      
      /*
    DatagramSocket s = new DatagramSocket(8888);
    InetAddress addr = InetAddress.getByName("CMPLAB3-16");
    Peer p1 = new Peer(addr, s);
    LogicalClock clock = new LogicalClock();
    p1.setClock(clock);
    clock.setPeer(p1);
    p1.sendJoin(InetAddress.getByName("CMPLAB3-15"));
    peer.run();*/
      
    JFrame.setDefaultLookAndFeelDecorated(true);
    String nodename = "defaultnode";
    if (args.length > 0)
    {
      nodename = args[0];
    }
    SimpleWhiteboardDemo simpleWhiteboardDemo = new SimpleWhiteboardDemo(nodename);
    javax.swing.SwingUtilities.invokeLater(simpleWhiteboardDemo);
    
      
      
  }
}
