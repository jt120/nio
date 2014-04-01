package Chapter3;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;

public class WriteImageIW
{
  public WriteImageIW( String filename, String type ) {
    try {
      int width = 200, height = 200;
      int x0 = 20, y0 = 20, x1 = width-20, y1 = width-20;

      BufferedImage bi = new BufferedImage( width, height,
                                            BufferedImage.TYPE_INT_ARGB );

      Graphics2D ig2 = bi.createGraphics();

      GradientPaint paint =
        new GradientPaint( x0, y0, Color.white, x1, y1, Color.black );
      ig2.setPaint( paint );
      ig2.fillRect( 0, 0, width-1, height-1 );

      BasicStroke stroke = new BasicStroke( 10, BasicStroke.CAP_ROUND,
                                            BasicStroke.JOIN_ROUND );
      ig2.setPaint( Color.lightGray );
      ig2.setStroke( stroke );
      ig2.draw( new Ellipse2D.Double( x0, y0, x1-x0, y1-y0 ) );

      Font font = new Font( "TimesRoman", Font.BOLD, 20 );
      ig2.setFont( font );
      String message = "Java2D!";
      FontMetrics fontMetrics = ig2.getFontMetrics();
      int stringWidth = fontMetrics.stringWidth( message );
      int stringHeight = fontMetrics.getAscent();
      ig2.setPaint( Color.black );
      ig2.drawString( message, (width-stringWidth)/2,
                      height/2+stringHeight/4 );


      Iterator imageWriters =
        ImageIO.getImageWritersByFormatName( type );
      ImageWriter imageWriter = (ImageWriter)imageWriters.next();
      File file = new File( filename );
      ImageOutputStream ios =
        ImageIO.createImageOutputStream( file );
      imageWriter.setOutput( ios );
      imageWriter.write( bi );
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  static public void main( String args[] ) throws Exception {
    if (args.length < 2) {
      System.err.println( "Usage: java PrintImage <image name> <type>" );
      System.exit( 1 );
    }

    new WriteImageIW( args[0], args[1] );
  }
}
