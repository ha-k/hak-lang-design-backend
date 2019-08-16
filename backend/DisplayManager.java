//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.io.PrintStream;
import hlt.language.design.types.Type;

/**
 * This interface specifies the method signatures for a display manager
 * consisting of a device manager and a formatter.
 */
public interface DisplayManager extends DisplayDeviceManager, DisplayFormManager
{
  public DisplayDeviceManager displayDeviceManager ();
  public DisplayFormManager displayFormManager ();
  public DisplayManager setDisplayDeviceManager (DisplayDeviceManager device);
  public DisplayManager setDisplayFormManager (DisplayFormManager formatter);

  public void setOutputStream (PrintStream stream);
  public PrintStream getOutputStream ();

  public String displayForm (int n, Type type);
  public String displayForm (double x);
  public String displayForm (Object o, Type type);
}
