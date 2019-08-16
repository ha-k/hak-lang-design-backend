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
import java.io.IOException;
import hlt.language.design.types.Type;

/**
 * This class defines a default display manager that consists of a
 * default display device manager and a default display form manager.
 */
public class DefaultDisplayManager implements DisplayManager
{
  private DisplayDeviceManager _device;
  private DisplayFormManager _formatter;    

  public DefaultDisplayManager ()
    {
      _device = new DefaultDisplayDeviceManager();
      _formatter = new DefaultDisplayFormManager();
    }

  public DefaultDisplayManager (DisplayDeviceManager device)
    {
      _device = device;
      _formatter = new DefaultDisplayFormManager();
    }

  public DefaultDisplayManager (DisplayFormManager formatter)
    {
      _device = new DefaultDisplayDeviceManager();
      _formatter = formatter;
    }

  public DefaultDisplayManager (DisplayDeviceManager device, DisplayFormManager formatter)
    {
      _device = device;
      _formatter = formatter;
    }

  public DefaultDisplayManager (DisplayFormManager formatter, DisplayDeviceManager device)
    {
      _device = device;
      _formatter = formatter;
    }

  public final DisplayDeviceManager displayDeviceManager ()
    {
      return _device;
    }

  public final DisplayFormManager displayFormManager ()
    {
      return _formatter;
    }

  public final DisplayManager setDisplayDeviceManager (DisplayDeviceManager device)
    {
      _device = device;
      return this;
    }

  public final DisplayManager setDisplayFormManager (DisplayFormManager formatter)
    {
      _formatter = formatter;
      return this;
    }

  public void setOutputStream (PrintStream stream)
    {
      _device.setOutputStream(stream);
    }

  public PrintStream getOutputStream ()
    {
      return _device.getOutputStream();
    }

  public void close ()
    {
      _device.close();
    }

  public void flush () throws IOException
    {
    }

  public void println ()
    {
      _device.println();
    }
    
  public void print (String string)
    {
      _device.print(string);
    }
    
  public void println (String string)
    {
      _device.println(string);
    }

  public String displayVoid ()
    {
      return _formatter.displayVoid();
    }

  public String typedDisplayForm (int n, Type type)
    {
      return _formatter.typedDisplayForm(n,type);
    }

  public String typedDisplayForm (double x)
    {
      return _formatter.typedDisplayForm(x);
    }

  public String typedDisplayForm (Object o, Type type)
    {
      return _formatter.typedDisplayForm(o,type);
    }

  public String quotedDisplayForm (int n, Type type)
    {
      return _formatter.quotedDisplayForm(n,type);
    }

  public String quotedDisplayForm (double x)
    {
      return _formatter.quotedDisplayForm(x);
    }

  public String quotedDisplayForm (Object o, Type type)
    {
      return _formatter.quotedDisplayForm(o,type);
    }

  public String unquotedDisplayForm (int n, Type type)
    {
      return _formatter.unquotedDisplayForm(n,type);
    }

  public String unquotedDisplayForm (double x)
    {
      return _formatter.unquotedDisplayForm(x);
    }

  public String unquotedDisplayForm (Object o, Type type)
    {
      return _formatter.unquotedDisplayForm(o,type);
    }

  public String displayForm (int n, Type type)
    {
      return _formatter.quotedDisplayForm(n,type);
    }

  public String displayForm (double x)
    {
      return _formatter.quotedDisplayForm(x);
    }

  public String displayForm (Object o, Type type)
    {
      return _formatter.quotedDisplayForm(o,type);
    }

  public DisplayFormManager clearTags ()
    {
      return _formatter.clearTags();
    }

}
