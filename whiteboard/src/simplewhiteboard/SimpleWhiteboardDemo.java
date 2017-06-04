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
    this.simpleWhiteboard = new SimpleWhiteboard(this.nodename, 1000, 600);
  }

  public void run()
  {
    this.simpleWhiteboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.simpleWhiteboard.setPreferredSize(new Dimension(400, 300));
    this.simpleWhiteboard.pack();
    this.simpleWhiteboard.setVisible(true);
  }

  public static void main(String[] args) throws Exception
  {
      /*
    JFrame.setDefaultLookAndFeelDecorated(true);
    String nodename = "defaultnode";
    if (args.length > 0)
    {
      nodename = args[0];
    }
    SimpleWhiteboardDemo simpleWhiteboardDemo = new SimpleWhiteboardDemo(nodename);
    javax.swing.SwingUtilities.invokeLater(simpleWhiteboardDemo);*/
    /*
    TCPConnection tcp = new TCPConnection();
    tcp.run();*/
    /*for(int i = 0; i < 100; i+=4){
        System.out.println(i);
    }*/
    DatagramSocket s = new DatagramSocket(8888);
    InetAddress addr = InetAddress.getByName("127.0.0.1");
    Peer p1 = new Peer(addr, s);
    p1.addPeer(InetAddress.getByName("255.255.255.0"));
    p1.run();
  }
}
