//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.types.Type;

/**
 * This class defines methods that format runtime values according to their types
 * to enable them to be legibly displayed.
 */
public interface DisplayFormManager
{
  public String displayVoid ();

  public String typedDisplayForm (int n, Type type);
  public String typedDisplayForm (double x);
  public String typedDisplayForm (Object o, Type type);

  // Quoted display form : "a\tb" is displayed as: "a\tb"

  public String quotedDisplayForm (int n, Type type);
  public String quotedDisplayForm (double x);
  public String quotedDisplayForm (Object o, Type type);

  // Unquoted display form : "a\tb" is displayed as: a        b

  public String unquotedDisplayForm (int n, Type type);
  public String unquotedDisplayForm (double x);
  public String unquotedDisplayForm (Object o, Type type);

  public DisplayFormManager clearTags ();
}
