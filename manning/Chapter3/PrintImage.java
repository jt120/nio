package Chapter3;
import java.io.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

public class PrintImage
{
  public PrintImage( String filename ) {
    try {
      PrintRequestAttributeSet pras =
        new HashPrintRequestAttributeSet();
      pras.add( new Copies( 1 ) );

      PrintService pss[] =
        PrintServiceLookup.lookupPrintServices(
          DocFlavor.INPUT_STREAM.GIF, pras );

      if (pss.length==0)
        throw new RuntimeException(
          "No printer services available." );

      PrintService ps = pss[0];
      System.out.println( "Printing to "+ps );

      DocPrintJob job = ps.createPrintJob();

      FileInputStream fin = new FileInputStream( filename );
      Doc doc = new SimpleDoc( fin,
                               DocFlavor.INPUT_STREAM.GIF, null );

      job.print( doc, pras );

      fin.close();
    } catch( IOException ie ) {
      ie.printStackTrace();
    } catch( PrintException pe ) {
      pe.printStackTrace();
    }
  }

  static public void main( String args[] ) throws Exception {
    if (args.length < 1) {
      System.err.println( "Usage: java PrintImage <image name>" );
      System.exit( 1 );
    }

    new PrintImage( args[0] );
  }
}
