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

public class CreateImageStrip extends Panel
{
  private BufferedImage image;
  private int imageWidth, imageHeight;

  public CreateImageStrip( String filename ) {
    try {
      FileInputStream fin = new FileInputStream( filename );
      String suffix =
        filename.substring( filename.lastIndexOf( '.' )+1 );
      Iterator readers = ImageIO.getImageReadersBySuffix( suffix );
      ImageReader imageReader = (ImageReader)readers.next();
      ImageInputStream iis = ImageIO.createImageInputStream( fin );
      imageReader.setInput( iis, false );
      int num = imageReader.getNumImages( true );
      System.out.println( "Found "+num+" images" );

      int totalHeight = 0;
      int maxWidth = 0;
      for (int i=0; i<num; ++i) {
        totalHeight += imageReader.getHeight( i );
        int w = imageReader.getWidth( i );
        if (w>maxWidth)
          maxWidth = w;
      }

      imageWidth = maxWidth;
      imageHeight = totalHeight;

      ImageTypeSpecifier its =
        (ImageTypeSpecifier)imageReader.getImageTypes( 0 ).next();
      image = its.createBufferedImage( imageWidth, imageHeight );

      int currentY = 0;
      for (int i=0; i<num; ++i) {
        int wd = imageReader.getWidth( i );
        int ht = imageReader.getHeight( i );
        ImageReadParam irp = imageReader.getDefaultReadParam();
        BufferedImage subImage =
          image.getSubimage( 0, currentY, wd, ht );
        irp.setDestination( subImage );
        imageReader.read( i, irp );
        currentY += ht;
      }

      fin.close();
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  public void paint( Graphics g ) {
    g.drawImage( image, 0, 0, null );
  }

  static public void main( String args[] ) throws Exception {
    JFrame frame = new JFrame( "CreateImageStrip.java" );
    CreateImageStrip panel = new CreateImageStrip( args[0] );
    frame.getContentPane().add( panel );
    frame.setSize( panel.imageWidth+20, panel.imageHeight+70 );
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
