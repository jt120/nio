package Chapter3;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;

public class Graph
{
  // A blank border around the graph
  static private final int border = 25;

  private BufferedImage image;

  // The graph data
  private double data[];

  private int width, height;

  // The background grid
  static private final int gridWidth=12, gridHeight=10;

  static private final String months[] = {
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
    "Oct", "Nov", "Dec" };

  public Graph( double data[], int width, int height ) {
    this.data = data;
    this.width = width;
    this.height = height;
  }

  private void generateImage() {
    image = new BufferedImage( width, height,
                               BufferedImage.TYPE_INT_ARGB );
    Graphics2D g2 = image.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    GradientPaint paint =
      new GradientPaint( 0, 0, Color.white, width, height,
                         Color.lightGray );
    g2.setPaint( paint );
    g2.fillRect( 0, 0, width-1, height-1 );

    g2.setPaint( Color.black );

    BasicStroke veryThin = new BasicStroke( 1,
                                            BasicStroke.CAP_ROUND,
                                          BasicStroke.JOIN_ROUND );
    BasicStroke thin = new BasicStroke( 3, BasicStroke.CAP_ROUND,
                                          BasicStroke.JOIN_ROUND );
    BasicStroke thick = new BasicStroke( 7, BasicStroke.CAP_ROUND,
                                          BasicStroke.JOIN_ROUND );
    g2.setStroke( thin );
    g2.setPaint( new Color( 155, 155, 155 ) );

    for (int i=0; i<gridWidth; ++i) {
      int x = border+(i*(width-2*border))/(gridWidth-1);
      g2.drawLine( x, border, x, height-border );
    }
    for (int i=0; i<gridHeight; ++i) {
      int y = border+(i*(height-2*border))/(gridHeight-1);
      g2.drawLine( border, y, width-border, y );
    }

    g2.setStroke( thick );
    g2.setPaint( Color.black );

    for (int i=1; i<data.length; ++i) {
      int x0 = border+
        (int)(((width-2*border)*(i-1))/(gridWidth-1));
      int y0 = border+
        (int)(((height-2*border)*data[i-1])/(gridHeight-1));
      int x1 = border+
        (int)(((width-2*border)*i)/(gridWidth-1));
      int y1 = border+
        (int)(((height-2*border)*data[i])/(gridHeight-1));
      y0 = height-1-y0;
      y1 = height-1-y1;

      g2.drawLine( x0, y0, x1, y1 );
    }

    g2.setStroke( veryThin );
    g2.setPaint( Color.darkGray );

    Font font = new Font( "Courier", Font.BOLD, 12 );
    g2.setFont( font );
    for (int i=0; i<12; ++i) {
      String month = months[i];
      FontMetrics fontMetrics = g2.getFontMetrics();
      int stringWidth = fontMetrics.stringWidth( month );
      int stringHeight = fontMetrics.getAscent();
      int x = border+
        (int)(((width-2*border)*(i))/(gridWidth-1)) - stringWidth/2;
      int y = height-border+stringHeight;
      g2.drawString( month, x, y );
    }
  }

  public void write( String filename ) throws IOException {
    // Generate the image if we haven't already
    if (image == null)
      generateImage();

    // Find an ImageWriter that can write the file type
    // specified by the filename
    String suffix =
      filename.substring( filename.lastIndexOf( '.' )+1 );
    Iterator imageWriters =
      ImageIO.getImageWritersBySuffix( suffix );
    ImageWriter imageWriter = (ImageWriter)imageWriters.next();
    if (imageWriter==null)
      throw new RuntimeException( "Format for "+filename+
                                  " not supported" );

    File file = new File( filename );
    ImageOutputStream ios =
      ImageIO.createImageOutputStream( file );
    imageWriter.setOutput( ios );
    imageWriter.write( image );
  }

  static public void main( String args[] ) throws IOException {
    // Some sample data
    double data0[] = { 4.2, 4.3, 5.3, 6.5, 9.0, 8.5, 0.2, 0.4,
                      1.3, 4.3, 2.6, 7.8 };
    Graph graph0 = new Graph( data0, 400, 250 );
    graph0.write( "graph0.png" );

    // Some more sample data
    double data1[] = { 0, 3.3, 1.2, 6.6, 2.5, 8.3, 4.9, 5,
                        4.7, 3.9, 2.6, 1.1 };
    Graph graph1 = new Graph( data1, 400, 250 );
    graph1.write( "graph1.png" );
  }
}
