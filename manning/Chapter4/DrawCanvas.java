package Chapter4;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;

/**
 * A Canvas you can draw lines in.  Also knows how to
 * print itself, via the Printable interface
 */
public class DrawCanvas extends JPanel implements Printable
{
  // A list of java.awt.geom.Line2D that have been drawn
  private Vector lines = new Vector();

  // Background image
  private Image backgroundImage;

  // The index of the line that is currently being drawn
  private int newLine = -1;

  // Draw a thick line
  private BasicStroke stroke =
    new BasicStroke( 5, BasicStroke.CAP_ROUND,
                     BasicStroke.JOIN_ROUND );

  /**
   * Constructor: add listeners
   */
  public DrawCanvas() {
    addListeners();
  }

  /**
   * Return the list of lines
   */
  public Vector lines() {
    return lines;
  }

  /**
   * Install a background image
   */
  public void setBackgroundImage( Image backgroundImage ) {
    this.backgroundImage = backgroundImage;
    repaint();
  }

  /**
   * Draw the lines and background
   */
  public void paintComponent( Graphics g ) {
    Graphics2D g2 = (Graphics2D)g;

    g2.setStroke( stroke );

    int width = getWidth();
    int height = getHeight();

    // Draw the background
    if (backgroundImage!=null) {
      // Draw a background image
      g2.drawImage( backgroundImage, 0, 0, width, height, null );
    } else {
      // Draw a blank rectangle
      g2.setColor( Color.black );
      g2.fillRect( 0, 0, width-1, height-1 );
    }

    // Draw the lines
    g2.setColor( Color.white );
    for (Enumeration e = lines.elements(); e.hasMoreElements();) {
      Line2D line = (Line2D)e.nextElement();
      g2.draw( line );
    }
  }

  /**
   * Add a new line to the list
   */
  private void startDrawingLine( int x, int y ) {
    Line2D line = new Line2D.Double( x, y, x, y );
    lines.addElement( line );

    // Remember the index, within the list,
    //  of the new line
    newLine = lines.size()-1;

    repaint();
  }

  /**
   * We're still drawing the line: endpoint of the line
   * tracks the mouse cursor
   */
  private void updateDrawingLine( int x, int y ) {
    // We must be in the middle of drawing the new line
    if (newLine==-1)
      return;

    Line2D line = (Line2D)lines.elementAt( newLine );
    line.setLine( line.getX1(), line.getY1(), x, y );
    repaint();
  }

  /**
   * Done drawing the line
   */
  private void endDrawingLine() {
    newLine = -1;
  }

  /**
   * Render the document as an Image
   */
  public Image getImage() {
    int width = getWidth();
    int height = getHeight();
    BufferedImage bi = new BufferedImage( width, height,
      BufferedImage.TYPE_INT_ARGB );
    Graphics2D g2 = (Graphics2D)bi.getGraphics();
    paintComponent( g2 );
    return bi;
  }

  /**
   * Event listeners for drawing lines
   */
  private void addListeners() {
    addMouseListener( new MouseListener() {
      public void mousePressed( MouseEvent me ) {
        startDrawingLine( me.getX(), me.getY() );
      }
      public void mouseEntered( MouseEvent me ) {
      }
      public void mouseExited( MouseEvent me ) {
      }
      public void mouseReleased( MouseEvent me ) {
        endDrawingLine();
      }
      public void mouseClicked( MouseEvent me ) {
      }
    } );

    addMouseMotionListener( new MouseMotionListener() {
      public void mouseDragged( MouseEvent me ) {
        updateDrawingLine( me.getX(), me.getY() );
      }
      public void mouseMoved( MouseEvent me ) {
      }
    } );
  }

  /**
   * Render printable image
   */
  public int print( Graphics graphics, PageFormat pageFormat,
                   int pageIndex ) throws PrinterException {
    paintComponent( graphics );
    return Printable.PAGE_EXISTS;
  }
}
