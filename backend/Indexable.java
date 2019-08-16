//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.util.IntIterator;

/**
 * This is an interface denoting a collection whose elements possess an index.
 */
public interface Indexable extends Lockable, Iteratable
{
  public int size ();
  public int getIndex (Object o);
  public int getIndex (int i);
  public int getIndex (double x);
  public boolean equals (Object o, int[] permutation);
  public IntIterator indexIterator();
}

