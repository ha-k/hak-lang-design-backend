//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 22:29:29 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.Iterator;

import hlt.language.util.IntIterator;
import hlt.language.util.DoubleIterator;
import hlt.language.util.ToIntMap;
import hlt.language.util.IntToIntMap;

/**
 * This is a runtime representation for sets of (unboxed) ints.
 * @see <a href="RuntimeSet.html"><tt>RuntimeSet</tt></a>
 */
public class IntSet extends RuntimeSet
{
  /**
   * The hash table containing the elements and their indices.
   */
  private IntToIntMap _map = new IntToIntMap();

  /**
   * This array reflects the inverse relation of <tt>_map</tt>.
   */
  private int[] _array;

  /**
   * The tightest int range containing this set.
   */
  private IntRange _intRange;

  /**
   * The least element of this set.
   */
  private int _min = Integer.MAX_VALUE;

  /**
   * The greatest element of this set.
   */
  private int _max = Integer.MIN_VALUE;

  /**
   * Constructs a new empty set.
   */
  public IntSet ()
    {
    }

  /**
   * Constructs a new set with the elements of the specified map.
   */
  private IntSet (IntToIntMap map)
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
  public final int[] array ()
    {
      if (_map.size() > 0 && (_array == null || _hasHoles))
        {
          _array = new int[_map.size()];

          if (_hasHoles)
            {
              ToIntMap.Entry[] entries = _resetIndices();
              for (int i = _array.length; i-->0;)
                {
                  IntToIntMap.Entry entry = (IntToIntMap.Entry)entries[i];
                  _array[entry.value()] = entry.key();
                }
            }
          else
            for (Iterator i = _map.iterator(); i.hasNext();)
              {
                IntToIntMap.Entry entry = (IntToIntMap.Entry)i.next();
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
      if (size() == 0)
        throw new NoSuchElementException("first element of an empty set");

      return array()[0];
    }
    
  /**
   * Returns the first element of this set as a double. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double firstReal () throws NoSuchElementException
    {
      return (double)firstInt();
    }
    
  /**
   * Returns the first element of this set as an object. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object firstObject () throws NoSuchElementException
    {
      return new RuntimeInt(firstInt());
    }
    

  /**
   * Returns the last element of this set as an int. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int lastInt () throws NoSuchElementException
    {
      if (size() == 0)
        throw new NoSuchElementException("last element of an empty set");

      return array()[size()-1];
    }
    
  /**
   * Returns the last element of this set as a double. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double lastReal () throws NoSuchElementException
    {
      return (double)lastInt();
    }
    
  /**
   * Returns the last element of this set as an object. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object lastObject () throws NoSuchElementException
    {
      return new RuntimeInt(lastInt());
    }
    

  /**
   * Returns the position of given int if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (int element) throws NoSuchElementException
    {
      if (_hasHoles) _resetIndices();

      int index = getIndex(element);
      if (index == -1)
        throw new NoSuchElementException(element+" doesn't belong to this set");

      return index;       
    }
    
  /**
   * Returns the position of given double if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (double element) throws NoSuchElementException
    {
      return ord((int)element);
    }
    
  /**
   * Returns the position of given object if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (Object element) throws NoSuchElementException
    {
      return ord(((RuntimeInt)element).value());
    }
    

  /**
   * Returns the element following the given one in this set, as an int.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int next (int element) throws NoSuchElementException
    {
      int index = ord(element)+1;

      if (index == size())
        throw new NoSuchElementException(element+" has no successor in this set");

      return array()[index];        
    }
    
  /**
   * Returns the element following the given one in this set, as a double.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double next (double element) throws NoSuchElementException
    {
      return (double)next((int)element);
    }
    
  /**
   * Returns the element following the given one in this set, as an object.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object next (Object element) throws NoSuchElementException
    {
      return new RuntimeInt(next(((RuntimeInt)element).value()));
    }
    

  /**
   * Returns the element preceding the given one in this set, as an int.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int prev (int element) throws NoSuchElementException
    {
      int index = ord(element)-1;

      if (index == -1)
        throw new NoSuchElementException(element+" has no predecessor in this set");

      return array()[index];        
    }
    
  /**
   * Returns the element preceding the given one in this set, as a double.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double prev (double element) throws NoSuchElementException
    {
      return (double)prev((int)element);
    }
    
  /**
   * Returns the element preceding the given one in this set, as an object.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object prev (Object element) throws NoSuchElementException
    {
      return new RuntimeInt(prev(((RuntimeInt)element).value()));
    }
    

  /**
   * Returns the element following the given one in this set, as an int,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int nextc (int element) throws NoSuchElementException
    {
      return array()[(ord(element)+1) % size()];
    }
    
  /**
   * Returns the element following the given one in this set, as a double,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double nextc (double element) throws NoSuchElementException
    {
      return (double)nextc((int)element);
    }
    
  /**
   * Returns the element following the given one in this set, as an object,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object nextc (Object element) throws NoSuchElementException
    {
      return new RuntimeInt(next(((RuntimeInt)element).value()));
    }
    

  /**
   * Returns the element preceding the given one in this set, as an int,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int prevc (int element) throws NoSuchElementException
    {
      return array()[(ord(element)+size()-1) % size()];
    }
    
  /**
   * Returns the element preceding the given one in this set, as a double,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double prevc (double element) throws NoSuchElementException
    {
      return (double)prevc((int)element);
    }
    
  /**
   * Returns the element preceding the given one in this set, as an object,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object prevc (Object element) throws NoSuchElementException
    {
      return new RuntimeInt(prevc(((RuntimeInt)element).value()));
    }
    
  /**
   * Returns a copy of this set.
   */
  public final RuntimeSet copy ()
    {
      return new IntSet(_map)._setHasHoles(_hasHoles)._setMaxIndex(_maxIndex);
    }      

  /**
   * Returns the index of the specified int if it belongs to this set,
   * otherwise <tt>-1</tt>.
   */
  public final int getIndex (int element)
    {
      int index = _map.get(element);
      return index == _map.NOT_FOUND_VALUE && !_map.containsKey(element)
           ? -1
           : index;
    }

  /**
   * Returns the index of the specified double cast as an int if such a
   * value belongs to this set, otherwise <tt>-1</tt>.
   */
  public final int getIndex (double element)
    {
      return getIndex((int)element);
    }

  /**
   * Returns the index of the int value of specified boxed int if it belongs
   * to this set, otherwise <tt>-1</tt>.
   */
  public final int getIndex (Object element)
    {
      return getIndex(((RuntimeInt)element).value());
    }

  /**
   * Returns the least element of this set. 
   * If the set is empty, throws a <tt>NoSuchElementException</tt>.
   */
  public final int min ()
    {
      if (isEmpty())
        throw new NoSuchElementException("attempt to take the minimum element of an empty set");

      return _min;
    }

  /**
   * Returns the greatest element of this set. 
   * If the set is empty, throws a <tt>NoSuchElementException</tt>.
   */
  public final int max ()
    {
      if (isEmpty())
        throw new NoSuchElementException("attempt to take the maximum element of an empty set");

      return _max;
    }

  /**
   * Adds the specified int to this set if it does not already belong to this set.
   */
  public final void addInt (int element) 
    {
      add(element);
    }

  /**
   * Adds the specified int to this set if it does not already belong to this set, and
   * returns this set.
   */
  protected final RuntimeSet _add (int element)
    {
      if (!_map.containsKey(element))
        {
          _map.put(element,_maxIndex++);
          int min = size() == 1 ? element : Math.min(_min,element);
          int max = size() == 1 ? element : Math.max(_max,element);
          if (min != _min || max != _max) _intRange = null;
          _array = null;
          _min = min;
          _max = max;
        }

      return this;
    }

  /**
   * Adds the specified double cast as an int to this set if it does not already belong
   * to this set, and returns this set.
   */
  protected final RuntimeSet _add (double element)
    {
      return _add((int)element);
    }

  /**
   * Adds the specified boxed int to this set if it does not already belong to this
   * set, and returns this set.
   */
  protected final RuntimeSet _add (Object element)
    {
      return _add(((RuntimeInt)element).value());
    }

  /**
   * Removes the specified int from this set and returns this set.
   */
  protected final RuntimeSet _remove (int element)
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
   * Removes the specified double cast as an int from this set and returns this set.
   */
  protected final RuntimeSet _remove (double element)
    {
      return _remove((int)element);
    }

  /**
   * Removes the value of the specified boxed int from this set and returns this
   * set.
   */
  protected final RuntimeSet _remove (Object element)
    {
      return _remove(((RuntimeInt)element).value());
    }

  /**
   * Returns <tt>true</tt> iff the specified int belongs to this set.
   */
  public final boolean contains (int element)
    {
      return _map.containsKey(element);
    }

  /**
   * Returns <tt>true</tt> iff the specified double cast as an int belongs to this set.
   */
  public final boolean contains (double element)
    {
      return _map.containsKey((int)element);
    }

  /**
   * Returns <tt>true</tt> iff this set contains the value of the given boxed int.
   */
  public final boolean contains (Object element)
    {
      return _map.containsKey(((RuntimeInt)element).value());
    }

  /**
   * Returns <tt>true</tt> iff this set contains all the elements of the specified set.
   */
  public final boolean contains (RuntimeSet set)
    {
      for (IntIterator i = set.intIterator(false); i.hasNext();)
        if (!contains(i.next())) return false;

      return true;
    }

  /**
   * Returns this set modified to contain the union of this and the specified set.
   */
  protected final RuntimeSet _union (RuntimeSet set)
    {
      for (IntIterator i = set.intIterator(true); i.hasNext();)
        _add(i.next());

      return this;
    }

  /**
   * Returns this set modified to contain the intersection of this and the specified set.
   */
  public final RuntimeSet _intersection (RuntimeSet set)
    {
      for (IntIterator i = intIterator(false); i.hasNext();)
        {
          int element = i.next();
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
      for (IntIterator i = set.intIterator(false); i.hasNext();)
        _remove(i.next());

      return this;
    }

  /**
   * Returns this set modified to contain the symmetric difference of this and the specified set.
   */
  public final RuntimeSet _exclusion (RuntimeSet set)
    {
      for (IntIterator i = set.intIterator(true); i.hasNext();)
        {
          int element = i.next();
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
      _min = Integer.MAX_VALUE;
      for (IntIterator i = intIterator(false); i.hasNext();)
        _min = Math.min(_min,i.next());
      _intRange = null;
    }

  /**
   * Recomputes the maximum element.
   */
  public final void _recomputeMax ()
    {
      _max = Integer.MIN_VALUE;
      for (IntIterator i = intIterator(false); i.hasNext();)
        _max = Math.max(_max,i.next());
      _intRange = null;
    }

  /**
   * Returns an int range whose lower (resp., upper) bound is this set's miminum (resp.,
   * maximum) element.
   */
  public final IntRange toIntRange ()
    {
      if (_intRange != null) return _intRange;
      return _intRange = isEmpty() ? new IntRange() : new IntRange(_min,_max);
    }

  /**
   * Returns an iterator of ints over the elements of this set. This iterator is
   * not guaranteed to be ordered, unless the set is locked.
   */
  public final IntIterator intIterator ()
    {
      return intIterator(_isLocked);
    }

  /**
   * Returns an iterator of ints over the elements of this set such that whenever
   * the specified boolean is <tt>true</tt>, iteration will be respecting the
   * index order of the elements; otherwise, order of iteration is unpredictable.
   * The former is less efficient than the latter and should be used only when it
   * is mandatory to do ordered iteration.
   */
  public final IntIterator intIterator (boolean ordered)
    {
      return ordered ? (IntIterator)orderedIterator() : _map.keys();
    }

  /**
   * Returns an iterator of doubles over the elements of this set cast as doubles
   * such that whenever the specified boolean is <tt>true</tt>, iteration will be
   * respecting the index order of the elements; otherwise, order of iteration is
   * unpredictable. The former is less efficient than the latter and should be
   * used only when it is mandatory to do ordered iteration.
   */
  public final DoubleIterator realIterator (boolean ordered)
    {
      return new RealIterator(intIterator(ordered));
    }

  /**
   * Returns an iterator of doubles over the elements of this set cast as doubles.
   * This iterator is not guaranteed to be ordered, unless the set is locked.
   */
  public final DoubleIterator realIterator ()
    {
      return new RealIterator((IntIterator)intIterator());
    }

  /**
   * Returns an iterator of boxed ints for this set such that whenever the
   * specified boolean is <tt>true</tt>, iteration will be respecting the index
   * order of the elements; otherwise, order of iteration is unpredictable. The
   * former is less efficient than the latter and should be used only when it is
   * mandatory to do ordered iteration.
   */
  public final Iterator iterator (boolean ordered)
    {
      return new BoxedIntIterator(intIterator(ordered));
    }

  /**
   * Returns an iterator of boxed ints for this set. This iterator is not
   * guaranteed to be ordered, unless the set is locked.
   */
  public final Iterator iterator ()
    {
      return new BoxedIntIterator((IntIterator)intIterator());
    }

  /**
   * Returns an iterator of ints over the elements of this set in the order of
   * its indices.
   */
  public final IntIterator orderedIterator ()
    {
      return new OrderedIterator(array());
    }

  /**
   * Returns an iterator of ints over the elements of this set in the reverse order
   * of its indices.
   */
  public final IntIterator backwardIntIterator ()
    {
      return new BackwardIterator(array());
    }

  /**
   * Returns an iterator of doubles over the elements of this set cast as doubles
   * in the reverse order of its indices.
   */
  public final DoubleIterator backwardRealIterator ()
    {
      return new RealIterator(backwardIntIterator());
    }

  /**
   * Returns an iterator of boxed ints for this set in the reverse order
   * of its indices.
   */
  public final Iterator backwardIterator ()
    {
      return new BoxedIntIterator(backwardIntIterator());
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
   * Note that order on indices is ignored.
   */
  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof IndexableContainer)) // it may be an IntRange
        return false;

      IndexableContainer other = (IndexableContainer)object;

      if (size() != other.size())
        return false;

      for (IntIterator i = intIterator(false); i.hasNext();)
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

      if (!(object instanceof Indexable)) // it may be an IntRange
        return false;

      Indexable other = (Indexable)object;

      if (size() != other.size())
        return false;

     for (Iterator i = _map.iterator(); i.hasNext();)
       {
         IntToIntMap.Entry entry = (IntToIntMap.Entry)i.next();
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
      
      for (IntIterator i = intIterator(true); i.hasNext();)
        buf.append(i.next()).append(--l == 0 ? "" : ", ");

      return buf.append("}").toString();
    }

  private static class BoxedIntIterator implements Iterator
    {
      private IntIterator _iterator;

      BoxedIntIterator (IntIterator iterator)
        {
          _iterator = iterator;
        }

      public final boolean hasNext ()
        {
          return _iterator.hasNext();
        }

      public final Object next ()
        {
          return new RuntimeInt(_iterator.next());
        }

      public final void remove ()
        {
          throw new UnsupportedOperationException();
        }
    }

  private static class RealIterator implements DoubleIterator
    {
      private IntIterator _iterator;

      RealIterator (IntIterator iterator)
        {
          _iterator = iterator;
        }

      public final boolean hasNext ()
        {
          return _iterator.hasNext();
        }

      public final double next ()
        {
          return (double)_iterator.next();
        }
    }

  private static class OrderedIterator implements IntIterator
    {
      private int[] _array;
      private int _index = 0;

      OrderedIterator (int[] array)
        {
          _array = array;
        }

      public final boolean hasNext ()
        {
          return _array == null ? false : _index < _array.length;
        }

      public final int next ()
        {
          return _array[_index++];
        }
    }

  private static class BackwardIterator implements IntIterator
    {
      private int[] _array;
      private int _index;

      BackwardIterator (int[] array)
        {
          _array = array;
          _index = _array.length;
        }

      public final boolean hasNext ()
        {
          return _array == null ? false : _index > 0;
        }

      public final int next ()
        {
          return _array[--_index];
        }
    }
}
