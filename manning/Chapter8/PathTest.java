package Chapter8;
public class PathTest
{
  static public void main( String args[] ) {
    Path path = null;

    path = new Path();
    path.add( "a", ".\\pathtest\\a\\" );
    path.add( "b", ".\\pathtest\\b\\" );
    System.out.println( path.formatAsPath() );

    path = new Path();
    path.add( "b", ".\\pathtest\\b\\" );
    path.add( "a", ".\\pathtest\\a\\" );
    System.out.println( path.formatAsPath() );

    path = new Path();
    path.add( "a", ".\\pathtest\\a\\" );
    path.add( "b", ".\\pathtest\\b\\" );
    System.out.println( path.findFile( "Test.java" ) );
    path.setActive( "a", false );
    System.out.println( path.findFile( "Test.java" ) );
  }
}
