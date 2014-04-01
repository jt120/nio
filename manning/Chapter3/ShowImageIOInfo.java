package Chapter3;
import javax.imageio.*;

public class ShowImageIOInfo
{
  public ShowImageIOInfo() {
    String names[] = ImageIO.getReaderFormatNames();
    for (int i=0; i<names.length; ++i) {
      System.out.println( "reader "+names[i] );
    }

    names = ImageIO.getWriterFormatNames();
    for (int i=0; i<names.length; ++i) {
      System.out.println( "writer "+names[i] );
    }
  }

  static public void main( String args[] ) throws Exception {
    new ShowImageIOInfo();
  }
}
