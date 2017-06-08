package simplewhiteboard;

import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

enum DrawMode
{
  LINE, TEXT;
}

class SimpleWhiteboardControls extends JPanel implements ActionListener, MouseListener, KeyListener
{
  //original
  private SimpleWhiteboardPanel simpleWhiteboardPanel;
  private JComboBox drawModeComboBox;
  private JButton colorButton;
  private static String[] drawModeName = {"line", "text"};
  private DrawMode drawMode;
  private Color color;
  private Point point;
  private String fontname;
  private int fontsize;
  
  //P2P stuff
  Peer peer;
  
  public void setPeer(Peer peer){
      this.peer = peer;
  }
  
  public SimpleWhiteboardControls(SimpleWhiteboardPanel simpleWhiteboardPanel)
  {
    super();
    this.drawMode = DrawMode.LINE;
    this.simpleWhiteboardPanel = simpleWhiteboardPanel;
    this.drawModeComboBox = new JComboBox(drawModeName);
    this.drawModeComboBox.addActionListener(this);
    this.add(this.drawModeComboBox);
    this.colorButton = new JButton("set colour");
    this.colorButton.addActionListener(this);
    this.add(this.colorButton);
    this.syncState();
    this.simpleWhiteboardPanel.addMouseListener(this);
    this.simpleWhiteboardPanel.addKeyListener(this);
    this.color = Color.BLACK;
    this.fontname = "Monospaced";
    this.fontsize = 20;
  }

  public void drawLine(Point newPoint) throws UnsupportedEncodingException
  {
    if (this.point == null)
    {
      //marks start point - not needed for sending
      this.point = newPoint;
    }
    else
    {
      //this.point = this.simpleWhiteboardPanel.drawLine(this.point, newPoint, this.color);
      WBLineEvent line = new WBLineEvent(this.point.x, this.point.y, newPoint.x, newPoint.y, this.color);
      peer.sendVoteRequest(line);
      //peer.sendLine(line, 0); //clocktime just set to zero for now we aren't implementing ordering just yet
    }
  }
  
  //draws non-user line
  public void drawOtherLine(Point p1, Point p2, Color colorToUse){
      System.out.println("DRAWING LINE FROM - " + p1.toString() + " " + p2.toString());
      this.point = simpleWhiteboardPanel.drawLine(p1, p2, colorToUse);
  }

  public void drawString(String s) throws UnsupportedEncodingException
  {
    if (this.point != null)
    {
      //this.point = this.simpleWhiteboardPanel.drawString(s, this.point, this.fontname, this.fontsize, this.color);
      WBTextEvent text = new WBTextEvent(this.point.x, this.point.y, this.color, s);
      peer.sendVoteRequest(text);
    }
  }
  
  public void drawOtherString(Point p1, String s, Color colorToUse){
      System.out.println("DRAWING STRING FROM - " + p1.toString() + " " + s);
      this.point = this.simpleWhiteboardPanel.drawString(s, p1, this.fontname, this.fontsize, colorToUse);
  }
  
  public void clearBoard(){
      this.simpleWhiteboardPanel.clearWhiteboard();
  }

  public void syncState()
  {
    switch (this.drawMode)
    {
    case LINE:
      this.drawModeComboBox.setSelectedIndex(0);
      break;
    case TEXT:
      this.drawModeComboBox.setSelectedIndex(1);
      break;
    default:
      throw new RuntimeException("unknown draw mode");
    }
  }

  private void drawModeActionPerformed(ActionEvent actionEvent)
  {
    String cmd = (String) this.drawModeComboBox.getSelectedItem();
    if (cmd.equals("line"))
    {
      this.drawMode = DrawMode.LINE;
    }
    else if (cmd.equals("text"))
    {
      this.drawMode = DrawMode.TEXT;
    }
    else
    {
      throw new RuntimeException(String.format("unknown command: %s", cmd));
    }
  }

  private void colorActionPerformed(ActionEvent actionEvent)
  {
    color = JColorChooser.showDialog(this.simpleWhiteboardPanel, "choose colour", this.color);
    if (color != null)
    {
      this.color = color;
    }
  }

  public void actionPerformed(ActionEvent actionEvent)
  {
    if (actionEvent.getSource() == this.drawModeComboBox)
    {
      this.drawModeActionPerformed(actionEvent);
    }
    else if (actionEvent.getSource() == this.colorButton)
    {
      this.colorActionPerformed(actionEvent);
    }
  }

  @Override
  public void keyPressed(KeyEvent keyEvent)
  {
  }

  @Override
  public void keyReleased(KeyEvent keyEvent)
  {
  }

  @Override
  public void keyTyped(KeyEvent keyEvent)
  {
    switch (this.drawMode)
    {
    case TEXT:
      String s = Character.toString(keyEvent.getKeyChar());
    {
        try {
            this.drawString(s);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SimpleWhiteboardControls.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
      break;
    default:
      // ignore event if not in text mode
      break;
    }
  }

  @Override
  public void mouseEntered(MouseEvent mouseEvent)
  {
  }

  @Override
  public void mouseExited(MouseEvent mouseEvent)
  {
  }

  @Override
  public void mousePressed(MouseEvent mouseEvent)
  {
  }

  @Override
  public void mouseReleased(MouseEvent mouseEvent)
  {
  }

  @Override
  public void mouseClicked(MouseEvent mouseEvent)
  {
    // make sure panel gets focus when clicked
    this.simpleWhiteboardPanel.requestFocusInWindow();
    Point newPoint = mouseEvent.getPoint();
    switch (this.drawMode)
    {
    case TEXT:
      this.point = newPoint;
      break;
    case LINE:
      switch (mouseEvent.getButton())
      {
      case MouseEvent.BUTTON1:
    {
        try {
            //System.err.println(mouseEvent);
            this.drawLine(newPoint);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SimpleWhiteboardControls.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	break;
      case MouseEvent.BUTTON3:
	this.point = null;
	break;
      default:
	System.err.println(String.format("got mouse button %d", mouseEvent.getButton()));
	break;
      }
      break;
    default:
      throw new RuntimeException("unknown drawing mode");
    }
  }
}

class SimpleWhiteboardMenuActionListener implements ActionListener
{
  public void actionPerformed(ActionEvent actionEvent)
  {
    System.err.println(String.format("menu action: %s", actionEvent.getActionCommand()));
  }
}

public class SimpleWhiteboard extends JFrame
{
  private JScrollPane scrollPane;
  private SimpleWhiteboardPanel simpleWhiteboardPanel;
  private SimpleWhiteboardControls simpleWhiteboardControls;
  private JMenuBar menuBar;
  private SimpleWhiteboardMenuActionListener menuActionListener;
   
  public SimpleWhiteboard(String nodename, int width, int height)
  {

    super(String.format("100053671 whiteboard: %s", nodename));
    this.simpleWhiteboardPanel = new SimpleWhiteboardPanel(width, height);
    this.scrollPane = new JScrollPane(this.simpleWhiteboardPanel);
    this.getContentPane().add(this.scrollPane);
    this.simpleWhiteboardControls = new SimpleWhiteboardControls(this.simpleWhiteboardPanel);
    this.getContentPane().add(this.simpleWhiteboardControls, BorderLayout.SOUTH);
    this.menuBar = new JMenuBar();
    this.menuActionListener = new SimpleWhiteboardMenuActionListener();
    JMenu networkMenu = new JMenu("Network");
    JMenuItem connectItem = new JMenuItem("Connect");
    connectItem.addActionListener(this.menuActionListener);
    JMenuItem disconnectItem = new JMenuItem("Disconnect");
    disconnectItem.addActionListener(this.menuActionListener);
    networkMenu.add(connectItem);
    networkMenu.add(disconnectItem);
    this.menuBar.add(networkMenu);
    this.setJMenuBar(this.menuBar);
    // this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    
    //
    
  }
  
  public SimpleWhiteboardControls getControls(){
      return this.simpleWhiteboardControls;
  }
  
  public void setPeer(Peer peer){
      this.simpleWhiteboardControls.setPeer(peer);
  }
  
  public SimpleWhiteboardPanel getWhiteboardPanel()
  {
    return (this.simpleWhiteboardPanel);
  }
}
