//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Thu Mar 24 21:01:30 2016 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.util.ToIntMap;
import hlt.language.tools.Misc;
import java.util.Iterator;

/**
 * This is the mother of all runtime representations for sets. There
 * are three subclasses:
 * <ul>
 * <li><a href="IntSet.html"><tt>IntSet</tt></a>
 * <li><a href="RealSet.html"><tt>RealSet</tt></a>
 * <li><a href="ObjectSet.html"><tt>ObjectSet</tt></a>
 * </ul>
 *
 * which only differ in the instantiation of the element type of their
 * representation structures, which is common, modulo representation
 * variations as per of the type of elements. The common backbone
 * representation of a set is that of a two way bijection (<i>i.e.</i>,
 * a 1-to-1 onto map) between the elements of the set on one hand, and
 * the set of integers <tt>{1,...,n}</tt> of the other hand, where
 * <tt>n</tt> is the number of elements in the set. One way (from
 * elements to indices) is represented as an associative map from keys
 * of type instantiated for the element type to <tt>int</tt>s
 * (<i>i.e.</i> <tt>IntToIntMap</tt>, <tt>DoubleToIntMap</tt>, or
 * <tt>ObjectToIntMap</tt>). The inverse map (from <tt>int</tt>s to
 * <tt>Object</tt>, <tt>int</tt>s, or <tt>double</tt>s) is simply a Java
 * array of the appropriate sort (<i>i.e.</i>, <tt>int[]</tt>,
 * <tt>double[]</tt>, or <tt>Object[]</tt>). This "inverse map" is built
 * only if necessary as it is not always needed since it acts mostly as
 * a cache for iterations. Because sets may be dynamically altered <i>in
 * situ</i>, these two maps must be kept mutually consistent (lest gaps
 * in the index set appearing from deletions corrupt the 2-way
 * correspondence).
 *
 * <p>
 *
 * There are situations when it may be needed to prevent a set from being
 * further modified.  Control of such behavior is achieved through a
 * simple locking mechanism. Thus, an array (<i>i.e.</i>, the map from
 * indices to elements) that has been built for an unlocked set prior to
 * the set is altered (by adding new elements or removing some - thus
 * possibly making "holes" in the index set) will be recomputed whenever
 * (and only if) this is necessary to prevent descrepancy between the
 * two sides of the element/index 2-way maps.
 *
 * <p>
 *
 * Thus, our most basic representation of a Runtime Set is simply a
 * table mapping elements to indices. These indices correspond to the
 * order of insertion of each element into the set. Note that this
 * representation makes sets implicitly <i>totally ordered</i>
 * sets. Although this is true, this is only a programming
 * convenience. Relying on the determinism of this feature is not a safe
 * practice, even though the choice is predictable (since programmed
 * using the implicit rules in the set operations defined herein). What
 * matters, and is guaranteed, is that the pure set semantics of all set
 * operations is always preserved.  The only need for indices is to
 * provide methods that a client class can use for user-controlled
 * iteration through a set - such as <tt>first()</tt>, <tt>next()</tt>,
 * <tt>last()</tt>, <tt>previous()</tt>, <i>etc.</i>, ..., as opposed to
 * implicit iteration provided by the built-in iterators. Such an order
 * on the elements could be any one as long as it is consistent and
 * predictable - this is needed for debugging purposes.  Indeed, even
 * though the mathematical semantics of (unordered sets) is consistent
 * with a non-deterministic choice of elements for performing an
 * iteration over the set, for programming (and debugging) purposes, it
 * is better to keep a consistent order whenever possible. In any case,
 * the order must be predictable.
 *
 * <p>
 *
 * Thus, whenever a predictable consistent canonical order may be
 * "naturally" derived, it is used.  The one we use is the order in
 * which the set was built - <i>i.e.</i> order of temporal insertion of
 * each element into the set. Such an order may not be maintained
 * consistent through most set operations. Take, <i>e.g.</i>,
 * <tt>{3,4,2,1} U {1,2,4,5}</tt>.  This, with explicit indices, may be
 * written <tt>{3<sub>/1</sub>, 4<sub>/2</sub>, 2<sub>/3</sub>,
 * 1<sub>/4</sub>} U {1<sub>/1</sub>, 2<sub>/2</sub>, 4<sub>/3</sub>,
 * 5<sub>/4</sub>}</tt>, and thus it is not clear what the resulting
 * set's indexing will be.  For the sake of predictable behavior, we
 * remove such ambiguities adopting the following rule:
 *
 * <p>
 *
 * <table summary="" align="center" width="80%" cellpadding="5"> <tr>
 * <td><i><tt><font color="red"> all binary set operations will preserve
 * the left operand's order and reassign an index to the elements from
 * the right set operand in the left set operand as per whether it is
 * added or removed from it.</font></tt></i></td></tr></table>
 *
 * <p> For example, the set order we get from the above union example is
 * <tt>{3<sub>/1</sub>, 4<sub>/2</sub>, 2<sub>/3</sub>, 1<sub>/4</sub>,
 * 5<sub>/5</sub>}</tt>.
 * */
abstract public class RuntimeSet implements RuntimeObject, IndexableContainer
{
  /**
   * This flag is set whenever this set has "holes".  A hole is a gap
   * in the indexing sequence.
   */
  protected boolean _hasHoles = false;

  /**
   * This is the maximum index. When the set has no holes, it is equal to
   * the size of the set. Otherwise, it is equal to the size of the set
   * at the time the first hole appeared + the number of insertions that
   * happened after that.
   */
  protected int _maxIndex = 0;

  /**
   * This flag is set whenever at least one hole has appeared in the indices.
   */
  protected boolean _isLocked = false;

  /**
   * Returns the underlying index map representing the set.
   */
  abstract ToIntMap map ();    

  /**
   * Returns <tt>true</tt> iff there are holes in the indexing of this set.
   */
  protected final boolean _hasHoles ()
    {
      return _hasHoles;
    }    

  /**
   * Sets the flag indicating that there are holes in the indexing to the
   * specified boolean.
   */
  protected final RuntimeSet _setHasHoles (boolean flag)
    {
      _hasHoles = flag;
      return this;
    }

  /**
   * Sets the max index to the specified int.
   */
  protected final RuntimeSet _setMaxIndex (int index)
    {
      _maxIndex = index;
      return this;
    }

  /**
   * Locks this set to prevent any further modification.
   */
  public final void lock ()
    {
      _isLocked = true;
    }

  /**
   * Unlocks this set to enable further modification.
   */
  public final void unlock ()
    {
      _isLocked = false;
    }

  /**
   * Returns <tt>true</tt> iff this set is locked.
   */
  public final boolean isLocked ()
    {
      return _isLocked;
    }

  /**
   * Returns the number of elements in this set.
   */
  public final int size ()
    {
      return map().size();
    }

  /**
   * Returns <tt>true</tt> iff this set is empty.
   */
  public final boolean isEmpty ()
    {
      return map().isEmpty();
    }

  /**
   * Returns a hash code for this set.
   */
  public final int hashCode ()
    {
      return size();
    }

  /**
   * Reassigns the indices of the elements of the set to eliminate holes in such a
   * way as to preserve the order of the original indices.
   *
   * @returns an array of int map entries in insertion order
   */
  protected final ToIntMap.Entry[] _resetIndices ()
    {
      ToIntMap.Entry[] entries = new ToIntMap.Entry[size()];

      int index = 0;
      for (Iterator i = map().iterator(); i.hasNext();)
        entries[index++] = (ToIntMap.Entry)i.next();

      //System.err.print("Sorting "+index+" elements...");
      //long time = System.currentTimeMillis();
      Misc.sort(entries);
      //System.err.println(" in "+(System.currentTimeMillis()-time)+" ms");

      for (; index-->0;)
        entries[index].setValue(index);

      _hasHoles = false;
      _maxIndex = size();
      return entries;
    }

  /**
   * Returns <tt>true</tt> when this set is equal (as a set) to the specified one,
   * with a side-effect on the specified array of ints that will contain the index
   * permutation when the sets are found to be equal.
   */
  abstract public boolean equals (Object object, int[] permutation);

  /**
   * Returns the first element of this set as an int. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int firstInt () throws NoSuchElementException;
  /**
   * Returns the first element of this set as a double. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public double firstReal () throws NoSuchElementException;
  /**
   * Returns the first element of this set as an object. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public Object firstObject () throws NoSuchElementException;

  /**
   * Returns the last element of this set as an int. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int lastInt () throws NoSuchElementException;
  /**
   * Returns the last element of this set as a double. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public double lastReal () throws NoSuchElementException;
  /**
   * Returns the last element of this set as an object. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public Object lastObject () throws NoSuchElementException;

  /**
   * Returns the position of given int if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int ord (int element) throws NoSuchElementException;
  /**
   * Returns the position of given double if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int ord (double element) throws NoSuchElementException;
  /**
   * Returns the position of given object if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int ord (Object element) throws NoSuchElementException;

  /**
   * Returns the element following the given one in this set, as an int.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int next (int element) throws NoSuchElementException;
  /**
   * Returns the element following the given one in this set, as a double.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public double next (double element) throws NoSuchElementException;
  /**
   * Returns the element following the given one in this set, as an object.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public Object next (Object element) throws NoSuchElementException;

  /**
   * Returns the element preceding the given one in this set, as an int.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int prev (int element) throws NoSuchElementException;
  /**
   * Returns the element preceding the given one in this set, as a double.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public double prev (double element) throws NoSuchElementException;
  /**
   * Returns the element preceding the given one in this set, as an object.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public Object prev (Object element) throws NoSuchElementException;

  /**
   * Returns the element following the given one in this set, as an int,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int nextc (int element) throws NoSuchElementException;
  /**
   * Returns the element following the given one in this set, as a double,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public double nextc (double element) throws NoSuchElementException;
  /**
   * Returns the element following the given one in this set, as an object,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public Object nextc (Object element) throws NoSuchElementException;

  /**
   * Returns the element preceding the given one in this set, as an int,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public int prevc (int element) throws NoSuchElementException;
  /**
   * Returns the element preceding the given one in this set, as a double,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public double prevc (double element) throws NoSuchElementException;
  /**
   * Returns the element preceding the given one in this set, as an object,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  abstract public Object prevc (Object element) throws NoSuchElementException;

  /**
   * These are protected destructive methods for set operations on this
   * set, and in terms of which all public set operations are expressed
   * (see below). They must be implemented by the daughter classes.
   */

  abstract protected RuntimeSet _union (RuntimeSet set);
  abstract protected RuntimeSet _intersection (RuntimeSet set);
  abstract protected RuntimeSet _minus (RuntimeSet set);
  abstract protected RuntimeSet _exclusion (RuntimeSet set);

  /**
   * These are protected destructive methods for adding and removing an
   * element to or from this set, and in terms of which all public
   * add/remove operations are expressed (see below). They must be
   * implemented by the daughter classes.  */

  abstract protected RuntimeSet _add (int element);
  abstract protected RuntimeSet _add (double element);
  abstract protected RuntimeSet _add (Object element);

  abstract protected RuntimeSet _remove (int element);
  abstract protected RuntimeSet _remove (double element);
  abstract protected RuntimeSet _remove (Object element);

  /**
   * Returns this set modified to contain the union of this and the specified set.
   * If this set is locked, a <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet union (RuntimeSet set) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _union(set);
    }

  /**
   * Returns this set modified to contain the intersection of this and the specified
   * set. If this set is locked, a <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet intersection (RuntimeSet set) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _intersection(set);
    }

  /**
   * Returns this set modified to contain the set difference of this and the specified
   * set. If this set is locked, a <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet minus (RuntimeSet set) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _minus(set);
    }

  /**
   * Returns this set modified to contain the symmetric difference of
   * this and the specified set. If this set is locked, a
   * <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet exclusion (RuntimeSet set) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _exclusion(set);
    }

  /**
   * Returns a new set equal to the union of the two specified sets.
   */
  public static final RuntimeSet union (RuntimeSet set1, RuntimeSet set2)
    {
      return set1.copy()._union(set2);
    }

  /**
   * Returns a new set equal to the intersection of the two specified sets.
   */
  public static final RuntimeSet intersection (RuntimeSet set1, RuntimeSet set2)
    {
      return set1.copy()._intersection(set2);
    }

  /**
   * Returns a new set equal to the set difference of the two specified sets.
   */
  public static final RuntimeSet minus (RuntimeSet set1, RuntimeSet set2)
    {
      return set1.copy()._minus(set2);
    }

  /**
   * Returns a new set equal to the symmetric difference of the two
   * specified sets.
   */
  public static final RuntimeSet exclusion (RuntimeSet set1, RuntimeSet set2)
    {
      return set1.copy()._exclusion(set2);
    }

  /**
   * Returns a copy of this set.
   */
  abstract public RuntimeSet copy ();

  /**
   * Returns <tt>true</tt> iff the specified set is a subset of this set.
   */
  abstract public boolean contains (RuntimeSet set);

  /**
   * If this set is not locked, adds the specified element to this set
   * if it does not already belong to this set, and returns this set. If
   * this set is locked, a <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet add (int element) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _add(element);
    }

  /**
   * If this set is not locked, adds the specified element to this set
   * if it does not already belong to this set, and returns this set. If
   * this set is locked, a <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet add (double element) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _add(element);
    }

  /**
   * If this set is not locked, adds the specified element to this set
   * if it does not already belong to this set, and returns this set. If
   * this set is locked, a <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet add (Object element) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _add(element);
    }

  /**
   * If this set is not locked, removes the specified element from this
   * set and returns this set. If this set is locked, a
   * <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet remove (int element) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _remove(element);
    }

  /**
   * If this set is not locked, removes the specified element from this
   * set and returns this set. If this set is locked, a
   * <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet remove (double element) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _remove(element);
    }

  /**
   * If this set is not locked, removes the specified element from this
   * set and returns this set. If this set is locked, a
   * <tt>LockViolationException</tt> is thrown.
   */
  public final RuntimeSet remove (Object element) throws LockViolationException
    {
      if (_isLocked)
        throw new LockViolationException();

      return _remove(element);
    }

}
