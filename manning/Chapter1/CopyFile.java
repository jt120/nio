package Chapter1;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class CopyFile
{
  static public void main( String args[] ) throws Exception {
    String infile = args[0], outfile = args[1];
    FileInputStream fin = new FileInputStream( infile );
    FileOutputStream fout = new FileOutputStream( outfile );

    FileChannel inc = fin.getChannel();
    FileChannel outc = fout.getChannel();

    ByteBuffer bb = ByteBuffer.allocate( 1024 );

    while (true) {
      int ret = inc.read( bb );
      if (ret==-1) // nothing left to read
        break;
      bb.flip(); //make position start
      outc.write( bb );
      bb.clear();  // Make room for the next read
    }
  }
}
