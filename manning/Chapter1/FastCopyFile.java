package Chapter1;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class FastCopyFile
{
  static public void main( String args[] ) throws Exception {
    String infile = args[0], outfile = args[1];
    FileInputStream fin = new FileInputStream( infile );
    FileOutputStream fout = new FileOutputStream( outfile );

    FileChannel inc = fin.getChannel();
    FileChannel outc = fout.getChannel();
    //use direct buffer to fast copy
    ByteBuffer bb = ByteBuffer.allocateDirect( 1024 );

    while (true) {
      int ret = inc.read( bb );
      if (ret==-1)
        break;
      bb.flip();
      outc.write( bb );
      bb.clear();
    }
  }
}
