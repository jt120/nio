package Chapter3;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.event.*;
import javax.imageio.stream.*;

public class ChangeFormat
{
  static public void main( String args[] ) throws Exception {
    if (args.length < 2) {
      System.err.println(
        "Usage: java PrintImage <infile> <outfile>" );
      System.exit( 1 );
    }
    String infile = args[0], outfile = args[1];

    FileInputStream fin = new FileInputStream( infile );
    String suffix = infile.substring( infile.lastIndexOf( '.' )+1 );
    Iterator readers = ImageIO.getImageReadersBySuffix( suffix );
    ImageReader imageReader = (ImageReader)readers.next();
    ImageInputStream iis = ImageIO.createImageInputStream( fin );
    imageReader.setInput( iis, false );

    imageReader.addIIOReadProgressListener(
      new IIOReadProgressListener() {
        public void imageComplete( ImageReader source ) {
          System.out.println( "image complete "+source );
        }
        public void imageProgress( ImageReader source,
                                   float percentageDone ) {
          System.out.println( "image progress "+source+": "+
                              percentageDone+"%" );
        }
        public void imageStarted( ImageReader source,
                                  int imageIndex ) {
          System.out.println( "image #"+imageIndex+" started "+
                              source );
        }
        public void readAborted( ImageReader source ) {
          System.out.println( "read aborted "+source );
        }
        public void sequenceComplete( ImageReader source ) {
          System.out.println( "sequence complete "+source );
        }
        public void sequenceStarted( ImageReader source,
                                     int minIndex ) {
          System.out.println( "sequence started "+source+": "+
                              minIndex );
        }
        public void thumbnailComplete( ImageReader source ) {
          System.out.println( "thumbnail complete "+source );
        }
        public void thumbnailProgress( ImageReader source,
                                       float percentageDone ) {
          System.out.println( "thumbnail started "+source+": "+
                              percentageDone+"%" );
        }
        public void thumbnailStarted( ImageReader source,
                                      int imageIndex,
                                      int thumbnailIndex ) {
          System.out.println( "thumbnail progress "+source+", "+
                              thumbnailIndex+" of "+imageIndex );
        }
    } );

    BufferedImage image = imageReader.read( 0 );

    suffix = outfile.substring( outfile.lastIndexOf( '.' )+1 );
    Iterator imageWriters =
      ImageIO.getImageWritersBySuffix( suffix );
    ImageWriter imageWriter = (ImageWriter)imageWriters.next();
    File file = new File( outfile );
    ImageOutputStream ios = ImageIO.createImageOutputStream( file );
    imageWriter.setOutput( ios );
    imageWriter.write( image );
  }
}
