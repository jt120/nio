package Chapter5.logging;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Main GUI window for logging-window system
 */
public class LoggerGUI extends JFrame
{
  // List displaying the currently-instantiated loggers
  private JList loggerList;

  /**
   * Set up the interface and make visible
   */
  public LoggerGUI() {
    super( "Logging" );

    setupGUI();
    addListeners();
    populateList();

    setVisible( true );
  }

  /**
   * Set up the interface
   */
  private void setupGUI() {
    Container cp = getContentPane();

    loggerList = new JList();
    loggerList.setSelectionMode(
      ListSelectionModel.SINGLE_SELECTION );

    cp.setLayout( new BorderLayout() );
    cp.add( loggerList, BorderLayout.CENTER );

    JButton showButton = new JButton( "show" );
    cp.add( showButton, BorderLayout.SOUTH );
    showButton.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent ae ) {
        String name = (String)loggerList.getSelectedValue();
        if (name != null && !name.equals( "" )) {
          openWindow( name );
        }
      }
    } );

    setLocation( 40, 40 );
    setSize( 200, 200 );
  }

  /**
   * List the currently-instantiated Loggers
   */
  private void populateList() {
    // Get the names of the currently-instantiated loggers
    LogManager logManager = LogManager.getLogManager();
    Enumeration e = logManager.getLoggerNames();

    // Build a Vector of the names
    Vector names = new Vector();
    while (e.hasMoreElements()) {
      String name = (String)e.nextElement();
      names.addElement( name );
    }

    // Display the names in the JList
    loggerList.setListData( names );
  }

  /**
   * Add listener to properly close window when
   * close-button is pressed
   */
  private void addListeners() {
    addWindowListener( new WindowListener() {
      public void windowActivated( WindowEvent we ) {
      }
      public void windowClosed( WindowEvent we ) {
      }
      public void windowClosing( WindowEvent we ) {
        // Remove window if the close-button is pressed
        closeWindow();
      }
      public void windowDeactivated( WindowEvent we ) {
      }
      public void windowDeiconified( WindowEvent we ) {
      }
      public void windowIconified( WindowEvent we ) {
      }
      public void windowOpened( WindowEvent we ) {
      }
    } );
  }

  /**
   * Open a Logger Window for a given Logger
   */
  private void openWindow( String loggerName ) {
    Logger logger = Logger.getLogger( loggerName );

    // Create a WindowHandler
    WindowHandler windowHandler = new WindowHandler( loggerName );

    // Install it as a handler for the logger
    logger.addHandler( windowHandler );
  }

  /**
   * Hide and dispose of the window
   */
  private void closeWindow() {
    setVisible( false );
    dispose();
  }
}
