package Chapter2;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class FastChecksum
{
  static public void main( String args[] ) throws Exception {
    if (args.length != 3) {
      System.err.println( "Usage: java FastChecksum "+
                          "<filename> <start pos> <# bytes>" );
      System.exit( 1 );
    }
    String filename = args[0];
    int start = Integer.parseInt( args[1] );
    int length = Integer.parseInt( args[2] );

    long fileLength = new File( filename ).length();

    if (length < start) {
      throw new IllegalArgumentException( "length < start" );
    }

    if (length < 0) {
      throw new IllegalArgumentException( "length < 0" );
    }

    if (start+length > fileLength) {
      throw new IllegalArgumentException( "start+length > fileLength" );
    }

    FileInputStream fin = new FileInputStream( filename );
    FileChannel finc = fin.getChannel();
    MappedByteBuffer mbb =
      finc.map( FileChannel.MapMode.READ_ONLY, start, length );

    long sum = 0;
    for (int i=0; i<length; ++i) {
      sum += mbb.get( i );
    }

    fin.close();

    System.out.println( "Sum: "+sum );
  }
}
