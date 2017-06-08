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

  public SimpleWhiteboardDemo(String nodename)
  {
    this.nodename = nodename;
    this.simpleWhiteboard = new SimpleWhiteboard(this.nodename, 1000, 1000);
  }
  
  public SimpleWhiteboardControls getControls(){
      return this.simpleWhiteboard.getControls();
  }
  
  public void setPeerToWB(Peer peer){
      this.simpleWhiteboard.getControls().setPeer(peer);
  }

  public void run()
  {
    this.simpleWhiteboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.simpleWhiteboard.setPreferredSize(new Dimension(1000, 1000));
    this.simpleWhiteboard.pack();
    this.simpleWhiteboard.setVisible(true);
    
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
    
    DatagramSocket s = new DatagramSocket(8888);
    InetAddress addr = InetAddress.getByName("DESKTOP-G6CK49G");
    System.out.println(addr.toString());
    Peer p1 = new Peer(addr, s);
    LogicalClock clock = new LogicalClock();
    p1.setClock(clock);
    clock.setPeer(p1);
    //p1.sendJoin(InetAddress.getByName("192.168.0.104"));
    p1.setWhiteboard(simpleWhiteboardDemo.getControls());
    simpleWhiteboardDemo.setPeerToWB(p1);
    //need to also link peer to WB
    new Thread(p1).start();
      
  }
}
