package Chapter10;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.*;
import javax.swing.*;

public class ListenerExample
{
  private JFrame window;
  private Preferences prefs =
    Preferences.userNodeForPackage( getClass() );
  static private final Rectangle horizontalOrientation =
    new Rectangle( 40, 220, 200, 100 );
  static private final Rectangle verticalOrientation =
    new Rectangle( 220, 40, 80, 200 );

  public ListenerExample() {
    addPrefsListener();
    setupGUI();
    setWindow();
  }

  private void addPrefsListener() {
    prefs.addPreferenceChangeListener(
      new PreferenceChangeListener() {
        public void preferenceChange( PreferenceChangeEvent pce ) {
          System.out.println( "Change: ("+pce.getNode()+") key="+
                              pce.getKey()+" value="+
                              pce.getNewValue() );
          setWindow();
        }
      } );
  }

  private void setupGUI() {
    JFrame controlFrame = new JFrame( "Control" );
    JButton horizontal = new JButton( "Horizontal" );
    JButton vertical = new JButton( "Vertical" );
    Container cp = controlFrame.getContentPane();
    cp.setLayout( new FlowLayout( FlowLayout.CENTER ) );
    cp.add( horizontal, BorderLayout.NORTH );
    cp.add( vertical, BorderLayout.SOUTH );

    controlFrame.setLocation( 40, 40 );
    controlFrame.setSize( 120, 120 );

    window = new JFrame( "Window" );
    cp = window.getContentPane();
    cp.setLayout( new BorderLayout() );
    cp.add( new JTextArea(), BorderLayout.CENTER );

    horizontal.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent ae ) {
        setHorizontal();
      }
    } );

    vertical.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent ae ) {
        setVertical();
      }
    } );

    controlFrame.setVisible( true );
    window.setVisible( true );
  }

  private void setHorizontal() {
    prefs.putBoolean( "horizontal", true );
  }

  private void setVertical() {
    prefs.putBoolean( "horizontal", false );
  }

  private void setWindow() {
    boolean horizontal = prefs.getBoolean( "horizontal", true );
    Rectangle rect = null;
    if (horizontal) {
      rect = horizontalOrientation;
    } else {
      rect = verticalOrientation;
    }

    window.setVisible( false );
    window.setLocation( rect.getLocation() );
    window.setSize( rect.getSize() );
    window.doLayout();
    window.setVisible( true );
  }

  static public void main( String args[] ) {
    new ListenerExample();
  }
}
