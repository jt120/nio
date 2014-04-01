package Chapter1;
import java.io.*;
import java.nio.channels.*;
import java.util.*;

public class SimpleDatabase
{
  static public final int NUMSLOTS = 64;
  static public final int SLOTSIZE = 1024;
  private RandomAccessFile raf;
  private FileChannel fc;

  public SimpleDatabase( String filename ) throws IOException {
    File file = new File( filename );
    boolean exists = file.exists();
    raf = new RandomAccessFile( file, "rw" );
    fc = raf.getChannel();

    if (!exists) {
      byte b[] = new byte[SLOTSIZE];
      for (int i=0; i<NUMSLOTS; ++i)
        put( i, b );
    }
  }

  private FileLock getLock( int slot, boolean shared )
    throws IOException {
    long position = slot*SLOTSIZE;
    long size = SLOTSIZE;

    FileLock lock = fc.lock( position, size, shared );
    return lock;
  }

  public void put( int slot, byte data[] ) throws IOException {
    FileLock fl = null;
    try {
      if (data.length != SLOTSIZE)
        throw new IllegalArgumentException( "Data wrong size: "+
                                            data.length );
      fl = getLock( slot, false );
      synchronized( raf ) {
        raf.seek( slot*SLOTSIZE );
        raf.write( data, 0, SLOTSIZE/2 );
        Thread.yield();
        raf.seek( slot*SLOTSIZE+(SLOTSIZE/2) );
        raf.write( data, SLOTSIZE/2, SLOTSIZE/2 );
        raf.getFD().sync();
        Thread.yield();
      }
    } finally {
      fl.release();
    }
  }

  public void get( int slot, byte data[] ) throws IOException {
    FileLock fl = null;
    try {
      if (data.length != SLOTSIZE)
        throw new IllegalArgumentException( "Data wrong size: "+
                                            data.length );
      fl = getLock( slot, true );
      synchronized( raf ) {
        raf.seek( slot*SLOTSIZE );
        raf.read( data );
      }
    } finally {
      fl.release();
    }
  }

  public void close() throws IOException {
    raf.close();
  }

  static public class SimpleDatabaseTester
  {
    private Random rand = new Random();
    private final int NUMSLOTS = SimpleDatabase.NUMSLOTS;
    private final int SLOTSIZE = SimpleDatabase.SLOTSIZE;
    private SimpleDatabase sd;

    public SimpleDatabaseTester( SimpleDatabase sd ) {
      this.sd = sd;
    }

    public void test() throws IOException {
      byte buffer[] = new byte[SLOTSIZE];
      int numOps = 0;
      while (true) {
        // Flip a coin: heads, write a block
        //              tails, read a block
        if (rand.nextInt( 100 )<50) {
          int slot = rand.nextInt( NUMSLOTS );
          generateConstantBuffer( buffer );
          sd.put( slot, buffer );
        } else {
          int slot = rand.nextInt( NUMSLOTS );
          sd.get( slot, buffer );
          confirmConstantBuffer( slot, buffer );
        }
        if (((++numOps)%50)==0) {
          System.out.println( numOps+" operations" );
        }
      }
    }

    private void generateConstantBuffer( byte buffer[] ) {
      byte b = (byte)rand.nextInt( 256 );
      for (int i=0; i<buffer.length; ++i)
        buffer[i] = b;
    }

    private void confirmConstantBuffer( int slot, byte buffer[] ) {
      int b = buffer[0];
      for (int i=1; i<buffer.length; ++i) {
        if (b != buffer[i]) {
          throw new RuntimeException( "Corrupted slot "+slot );
        }
      }
    }
  }

  static public void main( String args[] ) throws IOException {
    if (args.length != 1) {
      System.err.println( "Usage: java SimpleDatabase <filename>" );
      System.exit( 1 );
    }
    String filename = args[0];

    SimpleDatabase sd = new SimpleDatabase( filename );
    SimpleDatabaseTester sdt = new SimpleDatabaseTester( sd );
    sdt.test();
  }
}
