package Chapter3;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.*;

public class ShowImageIR extends Panel implements Runnable
{
  private BufferedImage images[];
  private int imageIndex=0;

  public ShowImageIR( String filename ) {
    try {
      FileInputStream fin = new FileInputStream( filename );
      String suffix =
        filename.substring( filename.lastIndexOf( '.' )+1 );
      System.out.println( "suf "+suffix );
      Iterator readers = ImageIO.getImageReadersBySuffix( suffix );
      ImageReader imageReader = (ImageReader)readers.next();
      ImageInputStream iis = ImageIO.createImageInputStream( fin );
      imageReader.setInput( iis, false );
      int num = imageReader.getNumImages( true );
      System.out.println( "Found "+num+" images" );
      images = new BufferedImage[num];
      for (int i=0; i<num; ++i) {
        images[i] = imageReader.read( i );
      }
      fin.close();
    } catch( IOException ie ) {
      ie.printStackTrace();
    }

    new Thread( this ).start();
  }

  public void paint( Graphics g ) {
    if (images==null)
      return;
    g.drawImage( images[imageIndex], 0, 0, null );
    imageIndex = (imageIndex+1)%images.length;
  }

  public void run() {
    while (true) {
      try {
        Thread.sleep( 100 );
        repaint();
      } catch( InterruptedException ie ) {}
    }
  }

  static public void main( String args[] ) throws Exception {
    JFrame frame = new JFrame( "ShowImageIR.java" );
    Panel panel = new ShowImageIR( args[0] );
    frame.getContentPane().add( panel );
    frame.setSize( 400, 400 );

    // Listener: quit on window close
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
