package Chapter3;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ShowImage extends Panel
{
  private BufferedImage image;

  public ShowImage( String filename ) {
    try {
      image = ImageIO.read( new File( filename ) );
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  public void paint( Graphics g ) {
    g.drawImage( image, 0, 0, null );
  }

  static public void main( String args[] ) throws Exception {
    JFrame frame = new JFrame( "ShowImage.java" );
    Panel panel = new ShowImage( args[0] );
    frame.getContentPane().add( panel );
    frame.setSize( 400, 400 );
    frame.addWindowListener( new WindowListener() {
      public void windowActivated( WindowEvent we ) {
      }
      public void windowClosed( WindowEvent we ) {
      }
      public void windowClosing( WindowEvent we ) { 
        System.exit( 0 );
      }
      public void windowDeactivated( WindowEvent we ) {
      }
      public void windowDeiconified( WindowEvent we ) {
      }
      public void windowIconified( WindowEvent we ) {
      }
      public void windowOpened( WindowEvent we ) {
      }
    } );
    frame.setVisible( true );
  }
}
