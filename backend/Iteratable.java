//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.Iterator;
import hlt.language.util.IntIterator;
import hlt.language.util.DoubleIterator;

/**
 * This is an interface denoting a collection that can be iterated over.
 */
public interface Iteratable
{
  public Iterator iterator (boolean ordered);
  public IntIterator intIterator (boolean ordered);
  public DoubleIterator realIterator (boolean ordered);

  public Iterator backwardIterator ();
  public IntIterator backwardIntIterator ();
  public DoubleIterator backwardRealIterator ();

  public Iterator iterator ();
  public IntIterator intIterator ();
  public DoubleIterator realIterator ();
}

