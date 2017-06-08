package simplewhiteboard;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.*;
import tcpconnections.TCPConnection;

public class SimpleWhiteboardDemo implements Runnable
{
  private SimpleWhiteboard simpleWhiteboard;
  private String nodename;
  public static InetAddress connectTo;
  public TCPConnection connection;
  
  public SimpleWhiteboardDemo(String nodename)
  {
    this.nodename = nodename;
    this.simpleWhiteboard = new SimpleWhiteboard(this.nodename, 1000, 1000);
    connectTo = null;
  }
  
  public SimpleWhiteboardControls getControls(){
      return this.simpleWhiteboard.getControls();
  }
  
  public void setPeerToWB(Peer peer){
      this.simpleWhiteboard.getControls().setPeer(peer);
  }
  
  public void setConnection(TCPConnection connection){
      this.connection = connection;
  }
  
  public void changePeer(InetAddress old, InetAddress newP) throws IOException{
      this.connection.changeSPeer(old, newP);
  }
  
  public void startSPeer() throws SocketException, UnknownHostException{
     DatagramSocket s = new DatagramSocket(8888);
    //InetAddress addr = InetAddress.getByName("DESKTOP-G6CK49G");
    InetAddress addr = InetAddress.getLocalHost();
    System.out.println(addr.toString());
    Peer p1 = new Peer(addr, s);
    LogicalClock clock = new LogicalClock();
    p1.setClock(clock);
    clock.setPeer(p1);
    p1.setWhiteboard(getControls());
    p1.setSuperpeer(addr);
    setPeerToWB(p1);
    //need to also link peer to WB
    new Thread(p1).start();
  }
  
  public void startPeer(InetAddress superpeer) throws SocketException, UnknownHostException{
      DatagramSocket s = new DatagramSocket(8888);
    //InetAddress addr = InetAddress.getByName("DESKTOP-G6CK49G");
    InetAddress addr = InetAddress.getLocalHost();
    System.out.println(addr.toString());
    Peer p1 = new Peer(addr, s);
    LogicalClock clock = new LogicalClock();
    p1.setClock(clock);
    clock.setPeer(p1);
    p1.sendJoin(superpeer);
    p1.setSuperpeer(superpeer);
    p1.setWhiteboard(getControls());
    setPeerToWB(p1);
    //need to also link peer to WB
    new Thread(p1).start();
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
     
    JFrame.setDefaultLookAndFeelDecorated(true);
    String nodename = "defaultnode";
    if (args.length > 0)
    {
      nodename = args[0];
    }
    SimpleWhiteboardDemo simpleWhiteboardDemo = new SimpleWhiteboardDemo(nodename);
    javax.swing.SwingUtilities.invokeLater(simpleWhiteboardDemo);
    
    TCPConnection tcp = new TCPConnection();
    simpleWhiteboardDemo.setConnection(tcp);
    tcp.setDemo(simpleWhiteboardDemo);
    tcp.run();
    
    
    /*
    DatagramSocket s = new DatagramSocket(8888);
    //InetAddress addr = InetAddress.getByName("DESKTOP-G6CK49G");
    InetAddress addr = InetAddress.getLocalHost();
    System.out.println(addr.toString());
    Peer p1 = new Peer(addr, s);
    LogicalClock clock = new LogicalClock();
    p1.setClock(clock);
    clock.setPeer(p1);
    if(!SimpleWhiteboardDemo.connectTo.equals(null)){
        p1.sendJoin(SimpleWhiteboardDemo.connectTo);
    }
    //p1.sendJoin(InetAddress.getByName("192.168.0.104"));
    p1.setWhiteboard(simpleWhiteboardDemo.getControls());
    simpleWhiteboardDemo.setPeerToWB(p1);
    //need to also link peer to WB
    new Thread(p1).start();
    */
  }
}
