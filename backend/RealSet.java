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
import hlt.language.util.ToIntMap;
import hlt.language.util.DoubleToIntMap;

/**
 * This is a runtime representation for sets of (unboxed) double.
 * @see <a href="RuntimeSet.html"><tt>RuntimeSet</tt></a>
 */
public class RealSet extends RuntimeSet
{
  /**
   * The hash table containing the elements and their indices.
   */
  private DoubleToIntMap _map = new DoubleToIntMap();

  /**
   * This array reflects the inverse relation of <tt>_map</tt>.
   */
  private double[] _array;

  /**
   * The tightest real range containing this set.
   */
  private RealRange _realRange;

  /**
   * The least element of this set.
   */
  private double _min = Double.POSITIVE_INFINITY;

  /**
   * The greatest element of this set.
   */
  private double _max = Double.NEGATIVE_INFINITY;

  /**
   * Constructs a new empty set.
   */
  public RealSet ()
    {
    }

  /**
   * Constructs a new set with the elements of the specified map.
   */
  public RealSet (DoubleToIntMap map)
    {
      _map.include(map);
    }

  /**
   * Returns the underlying index map representing the set.
   */
  final ToIntMap map ()
    {
      return _map;
    }

  /**
   * Returns the inverse relation of the index map as an array.
   */
  public final double[] array ()
    {
      if (_map.size() > 0 && (_array == null || _hasHoles))
        {
          _array = new double[_map.size()];

          if (_hasHoles)
            {
              ToIntMap.Entry[] entries = _resetIndices();
              for (int i = _array.length; i-->0;)
                {
                  DoubleToIntMap.Entry entry = (DoubleToIntMap.Entry)entries[i];
                  _array[entry.value()] = entry.key();
                }
            }
          else
            for (Iterator i = _map.iterator(); i.hasNext();)
              {
                DoubleToIntMap.Entry entry = (DoubleToIntMap.Entry)i.next();
                _array[entry.value()] = entry.key();
              }
        }

      return _array;
    }

  /**
   * Returns the first element of this set as an int. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int firstInt () throws NoSuchElementException
    {
      return (int)firstReal();
    }
    
  /**
   * Returns the first element of this set as a double. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double firstReal () throws NoSuchElementException
    {
      if (size() == 0)
        throw new NoSuchElementException("first element of an empty set");

      return array()[0];
    }
    
  /**
   * Returns the first element of this set as an object. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object firstObject () throws NoSuchElementException
    {
      return new RuntimeReal(firstReal());
    }
    

  /**
   * Returns the last element of this set as an int. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int lastInt () throws NoSuchElementException
    {
      return (int)lastReal();
    }
    
  /**
   * Returns the last element of this set as a double. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double lastReal () throws NoSuchElementException
    {
      if (size() == 0)
        throw new NoSuchElementException("last element of an empty set");

      return array()[size()-1];
    }
    
  /**
   * Returns the last element of this set as an object. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object lastObject () throws NoSuchElementException
    {
      return new RuntimeReal(lastReal());
    }
    

  /**
   * Returns the position of given int if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (int element) throws NoSuchElementException
    {
      return ord((double)element);
    }
    
  /**
   * Returns the position of given double if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (double element) throws NoSuchElementException
    {
      if (_hasHoles) _resetIndices();

      int index = getIndex(element);
      if (index == -1)
        throw new NoSuchElementException(element+" doesn't belong to this set");

      return index;
    }
    
  /**
   * Returns the position of given object if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (Object element) throws NoSuchElementException
    {
      return ord(((RuntimeReal)element).value());
    }
    

  /**
   * Returns the element following the given one in this set, as an int.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int next (int element) throws NoSuchElementException
    {
      return (int)next((double)element);
    }
    
  /**
   * Returns the element following the given one in this set, as a double.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double next (double element) throws NoSuchElementException
    {
      int index = ord(element)+1;

      if (index == size())
        throw new NoSuchElementException(element+" has no successor in this set");

      return array()[index];
    }
    
  /**
   * Returns the element following the given one in this set, as an object.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object next (Object element) throws NoSuchElementException
    {
      return new RuntimeReal(next(((RuntimeReal)element).value()));
    }
    

  /**
   * Returns the element preceding the given one in this set, as an int.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int prev (int element) throws NoSuchElementException
    {
      return (int)prev((double)element);
    }
    
  /**
   * Returns the element preceding the given one in this set, as a double.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double prev (double element) throws NoSuchElementException
    {
      int index = ord(element)-1;

      if (index == -1)
        throw new NoSuchElementException(element+" has no predecessor in this set");

      return array()[index];
    }
    
  /**
   * Returns the element preceding the given one in this set, as an object.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object prev (Object element) throws NoSuchElementException
    {
      return new RuntimeReal(prev(((RuntimeReal)element).value()));
    }
    

  /**
   * Returns the element following the given one in this set, as an int,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int nextc (int element) throws NoSuchElementException
    {
      return (int)nextc((double)element);
    }
    
  /**
   * Returns the element following the given one in this set, as a double,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double nextc (double element) throws NoSuchElementException
    {
      return array()[(ord(element)+1) % size()];
    }
    
  /**
   * Returns the element following the given one in this set, as an object,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object nextc (Object element) throws NoSuchElementException
    {
      return new RuntimeReal(next(((RuntimeReal)element).value()));
    }
    

  /**
   * Returns the element preceding the given one in this set, as an int,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int prevc (int element) throws NoSuchElementException
    {
      return (int)prevc((double)element);
    }
    
  /**
   * Returns the element preceding the given one in this set, as a double,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double prevc (double element) throws NoSuchElementException
    {
      return array()[(ord(element)+size()-1) % size()];
    }
    
  /**
   * Returns the element preceding the given one in this set, as an object,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object prevc (Object element) throws NoSuchElementException
    {
      return new RuntimeReal(prevc(((RuntimeReal)element).value()));
    }


  /**
   * Returns a copy of this set.
   */
  public final RuntimeSet copy ()
    {
      return new RealSet(_map)._setHasHoles(_hasHoles)._setMaxIndex(_maxIndex);
    }      

  /**
   * Returns the index of the specified double if it belongs to this set,
   * otherwise <tt>-1</tt>.
   */
  public final int getIndex (double element)
    {
      int index = _map.get(element);
      return index == _map.NOT_FOUND_VALUE && !_map.containsKey(element)
           ? -1
           : index;
    }

  /**
   * Returns the index of the specified int cast as a double if such a value
   * belongs to this set, otherwise <tt>-1</tt>.
   */
  public final int getIndex (int element)
    {
      return getIndex((double)element);
    }

  /**
   * Returns the index of the double value of specified boxed double if it belongs
   * to this set, otherwise <tt>-1</tt>.
   */
  public final int getIndex (Object element)
    {
      return getIndex(((RuntimeReal)element).value());
    }

  /**
   * Returns the least element of this set. 
   * If the set is empty, throws a <tt>NoSuchElementException</tt>.
   */
  public final double min ()
    {
      if (isEmpty())
        throw new NoSuchElementException("attempt to take the minimum element of an empty set");

      return _min;
    }

  /**
   * Returns the greatest element of this set. 
   * If the set is empty, throws a <tt>NoSuchElementException</tt>.
   */
  public final double max ()
    {
      if (isEmpty())
        throw new NoSuchElementException("attempt to take the maximum element of an empty set");

      return _max;
    }

  /**
   * Adds the specified double to this set if it does not already belong to this set,
   * and returns this set.
   */
  protected final RuntimeSet _add (double element)
    {
      if (!_map.containsKey(element))
        {
          _map.put(element,_maxIndex++);
          double min = size() == 1 ? element : Math.min(_min,element);
          double max = size() == 1 ? element : Math.max(_max,element);
          if (min != _min || max != _max) _realRange = null;
          _array = null;
          _min = min;
          _max = max;
        }

      return this;
    }

  /**
   * Adds the specified int cast as a double to this set if it does not already belong
   * to this set, and returns this set.
   */
  protected final RuntimeSet _add (int element)
    {
      return _add((double)element);
    }

  /**
   * Adds the specified boxed double to this set if it does not already belong to this
   * set, and returns this set.
   */
  protected final RuntimeSet _add (Object element)
    {
      return _add(((RuntimeReal)element).value());
    }

  /**
   * Removes the specified double from this set and returns this set.
   */
  protected final RuntimeSet _remove (double element)
    {
      if (_hasHoles |= (_map.remove(element) >= 0))
        {
          if (element == _min) _recomputeMin();
          if (element == _max) _recomputeMax();
          _array = null;
        }

      return this;
    }

  /**
   * Removes the specified int cast as a double from this set, and returns this set.
   */
  protected final RuntimeSet _remove (int element)
    {
      return _remove((double)element);
    }

  /**
   * Removes the value of the specified boxed double from this set and returns this
   * set.
   */
  protected final RuntimeSet _remove (Object element)
    {
      return _remove(((RuntimeReal)element).value());
    }

  /**
   * Returns <tt>true</tt> iff the specified double belongs to this set.
   */
  public final boolean contains (double element)
    {
      return _map.containsKey(element);
    }

  /**
   * Returns <tt>true</tt> iff the specified int cast as a double belongs to this set.
   */
  public final boolean contains (int element)
    {
      return _map.containsKey((double)element);
    }

  /**
   * Returns  <tt>true</tt> iff this set contains the value of the given boxed double.
   */
  public final boolean contains (Object element)
    {
      return _map.containsKey(((RuntimeReal)element).value());
    }

  /**
   * Returns <tt>true</tt> iff this set contains all the elements of the specified set.
   */
  public final boolean contains (RuntimeSet set)
    {
      for (DoubleIterator i = set.realIterator(false); i.hasNext();)
        if (!contains(i.next())) return false;

      return true;
    }

  /**
   * Returns this set modified to contain the union of this and the specified set.
   */
  protected final RuntimeSet _union (RuntimeSet set)
    {
      for (DoubleIterator i = set.realIterator(true); i.hasNext();)
        _add(i.next());

      return this;
    }

  /**
   * Returns this set modified to contain the intersection of this and the specified set.
   */
  public final RuntimeSet _intersection (RuntimeSet set)
    {
      for (DoubleIterator i = realIterator(false); i.hasNext();)
        {
          double element = i.next();
          if (!set.contains(element))
            _remove(element);
        }

      return this;
    }

  /**
   * Returns this set modified to contain the set difference of this and the specified set.
   */
  public final RuntimeSet _minus (RuntimeSet set) 
    {
      for (DoubleIterator i = set.realIterator(false); i.hasNext();)
        _remove(i.next());

      return this;
    }

  /**
   * Returns this set modified to contain the symmetric difference of this and the specified set.
   */
  public final RuntimeSet _exclusion (RuntimeSet set)
    {
      for (DoubleIterator i = set.realIterator(true); i.hasNext();)
        {
          double element = i.next();
          if (contains(element))
            _remove(element);
          else
            _add(element);             
        }

      return this;
    }

  /**
   * Recomputes the minimum element.
   */
  public final void _recomputeMin ()
    {
      _min = Double.POSITIVE_INFINITY;
      for (DoubleIterator i = realIterator(false); i.hasNext();)
        _min = Math.min(_min,i.next());
      _realRange = null;
    }

  /**
   * Recomputes the maximum element.
   */
  public final void _recomputeMax ()
    {
      _max = Double.NEGATIVE_INFINITY;
      for (DoubleIterator i = realIterator(false); i.hasNext();)
        _max = Math.max(_max,i.next());
      _realRange = null;
    }

  /**
   * Returns a real range whose lower (resp., upper) bound is this set's miminum (resp.,
   * maximum) element.
   */
  public final RealRange toRealRange ()
    {
      if (_realRange != null) return _realRange;
      return _realRange = isEmpty() ? new RealRange() : new RealRange(_min,_max);
    }

  /**
   * Returns an iterator of doubles over the elements of this set such that
   * whenever the specified boolean is <tt>true</tt>, iteration will be
   * respecting the index order of the elements; otherwise, order of iteration is
   * unpredictable. The former is less efficient than the latter and should be
   * used only when it is mandatory to do ordered iteration.
   */
  public final DoubleIterator realIterator (boolean ordered)
    {
      return ordered ? (DoubleIterator)orderedIterator() : _map.keys();
    }

  /**
   * Returns an iterator of doubles over the elements of this set. 
   * This iterator is not guaranteed to be ordered, unless the set is locked.
   */
  public final DoubleIterator realIterator ()
    {
      return realIterator(_isLocked);
    }

  /**
   * Returns an iterator of boxed reals for this set such that whenever the
   * specified boolean is <tt>true</tt>, iteration will be respecting the index
   * order of the elements; otherwise, order of iteration is unpredictable. The
   * former is less efficient than the latter and should be used only when it is
   * mandatory to do ordered iteration.
   */
  public final Iterator iterator (boolean ordered)
    {
      return new BoxedRealIterator(realIterator(ordered));
    }

  /**
   * Returns an iterator of boxed reals for this set.
   * This iterator is not guaranteed to be ordered, unless the set is locked.
   */
  public final Iterator iterator ()
    {
      return new BoxedRealIterator(realIterator());
    }

  /**
   * Returns an iterator of ints over the elements of this set cast as ints such
   * that whenever the specified boolean is <tt>true</tt>, iteration will be
   * respecting the index order of the elements; otherwise, order of iteration is
   * unpredictable.  The former is less efficient than the latter and should be
   * used only when it is mandatory to do ordered iteration.
   */
  public final IntIterator intIterator (boolean ordered)
    {
      return new CastIterator(realIterator(ordered));
    }

  /**
   * Returns an iterator of ints over the elements of this set cast as ints.
   * This iterator is not guaranteed to be ordered, unless the set is locked.
   */
  public final IntIterator intIterator ()
    {
      return new CastIterator(realIterator());
    }

  /**
   * Returns an iterator of doubles over the elements respecting the order of the
   * elements indices.
   */
  public final DoubleIterator orderedIterator ()
    {
      return new OrderedIterator(array());
    }

  /**
   * Returns an iterator of doubles over the elements of this set in the reverse order
   * of its indices.
   */
  public final DoubleIterator backwardRealIterator ()
    {
      return new BackwardIterator(array());
    }

  /**
   * Returns an iterator of ints over the elements of this set cast as ints
   * in the reverse order of its indices.
   */
  public final IntIterator backwardIntIterator ()
    {
      return new CastIterator(backwardRealIterator());
    }

  /**
   * Returns an iterator of boxed doubles for this set in the reverse order
   * of its indices.
   */
  public final Iterator backwardIterator ()
    {
      return new BoxedRealIterator(backwardRealIterator());
    }

  /**
   * Returns an iterator for the indices of the elements of this set.
   */
  public final IntIterator indexIterator ()
    {
      return _map.values();
    }

  /**
   * Returns <tt>true</tt> when this set is equal (as a set) to the specified object.
   * Note that order on indices is not important.
   */
  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof RuntimeSet))
        return false;

      RuntimeSet other = (RuntimeSet)object;

      if (size() != other.size())
        return false;

      for (DoubleIterator i = realIterator(false); i.hasNext();)
        if (!other.contains(i.next())) return false;

      return true;
    }

  /**
   * Returns <tt>true</tt> when this set is equal (as a set) to the specified object,
   * with a side-effect on the specified array of ints that will contain the index
   * permutation when the sets are found to be equal.
   */
  public final boolean equals (Object object, int[] permutation)
    {
      if (this == object)
        {
          for (int i=0; i<permutation.length; i++) permutation[i] = i;
          return true;
        }

      if (!(object instanceof RuntimeSet))
        return false;

      RuntimeSet other = (RuntimeSet)object;

      if (size() != other.size())
        return false;

     for (Iterator i = _map.iterator(); i.hasNext();)
       {
         DoubleToIntMap.Entry entry = (DoubleToIntMap.Entry)i.next();
         int index = other.getIndex(entry.key());
         if (index == -1) return false;
         permutation[index] = entry.value();
       }

      return true;
    }

  public final String toString ()
    {
      StringBuilder buf = new StringBuilder("{");

      int l = size();
      
      for (DoubleIterator i = realIterator(true); i.hasNext();)
        buf.append(i.next()).append(--l == 0 ? "" : ", ");

      return buf.append("}").toString();
    }

  private static class BoxedRealIterator implements Iterator
    {
      private DoubleIterator _iterator;

      BoxedRealIterator (DoubleIterator iterator)
        {
          _iterator = iterator;
        }

      public final boolean hasNext ()
        {
          return _iterator.hasNext();
        }

      public final Object next ()
        {
          return new RuntimeReal(_iterator.next());
        }

      public final void remove ()
        {
          throw new UnsupportedOperationException();
        }
    }

  private static class CastIterator implements IntIterator
    {
      private DoubleIterator _iterator;

      CastIterator (DoubleIterator iterator)
        {
          _iterator = iterator;
        }

      public final boolean hasNext ()
        {
          return _iterator.hasNext();
        }

      public final int next ()
        {
          return (int)_iterator.next();
        }
    }

  private static class OrderedIterator implements DoubleIterator
    {
      private double[] _array;
      private int _index = 0;

      OrderedIterator (double[] array)
        {
          _array = array;
        }

      public final boolean hasNext ()
        {
          return _array == null ? false : _index < _array.length;
        }

      public final double next ()
        {
          return _array[_index++];
        }
    }

  private static class BackwardIterator implements DoubleIterator
    {
      private double[] _array;
      private int _index;

      BackwardIterator (double[] array)
        {
          _array = array;
          _index = _array.length;
        }

      public final boolean hasNext ()
        {
          return _array == null ? false : _index > 0;
        }

      public final double next ()
        {
          return _array[--_index];
        }
    }
}

