package Chapter2;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class TranslateCharset
{
  static public void main( String args[] ) throws Exception {
    if (args.length != 4) {
      System.err.println(
        "Usage: java TranslateCharset <infile> <incharset> "+
        "<outfile> <outcharset>" );
      System.exit( 1 );
    }

    String inFilename = args[0];
    String inFileCharsetName = args[1];
    String outFilename = args[2];
    String outFileCharsetName = args[3];

    File infile = new File( inFilename );
    File outfile = new File( outFilename );

    RandomAccessFile inraf = new RandomAccessFile( infile, "r" );
    RandomAccessFile outraf = new RandomAccessFile( outfile, "rw" );

    FileChannel finc = inraf.getChannel();
    FileChannel foutc = outraf.getChannel();

    MappedByteBuffer inmbb =
      finc.map( FileChannel.MapMode.READ_ONLY, 0, (int)infile.length() );

    Charset inCharset = Charset.forName( inFileCharsetName );
    Charset outCharset = Charset.forName( outFileCharsetName );

    CharsetDecoder inDecoder = inCharset.newDecoder();
    CharsetEncoder outEncoder = outCharset.newEncoder();

    CharBuffer cb = inDecoder.decode( inmbb );
    ByteBuffer outbb = outEncoder.encode( cb );

    foutc.write( outbb );

    inraf.close();
    outraf.close();
  }
}
