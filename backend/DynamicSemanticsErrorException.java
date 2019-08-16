//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 22:23:55 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

public abstract class DynamicSemanticsErrorException extends RuntimeException
{
  protected String _msg = "";

  public final String msg ()
    {
      return _msg;
    }
  
  public final String getMessage ()
    {
      return _msg;
    }
  
  public String toString() 
    {
      return _msg;
    }
}
