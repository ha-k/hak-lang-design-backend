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
 * This is the runtime representation for tuples.
 */
public class RuntimeTuple implements RuntimeObject
{
  private int[] _intComponents;
  private double[] _realComponents;
  private Object[] _objectComponents;

  public final static RuntimeTuple EMPTY = new RuntimeTuple(null,null,null);

  public RuntimeTuple (int[] intComponents, double[] realComponents, Object[] objectComponents)
    {
      _intComponents = intComponents;
      _realComponents = realComponents;
      _objectComponents = objectComponents;
    }

  public int[] intComponents ()
    {
      return _intComponents;
    }

  public double[] realComponents ()
    {
      return _realComponents;
    }

  public final double[] floatComponents ()
    {
      return realComponents();
    }

  public Object[] objectComponents ()
    {
      return _objectComponents;
    }

  public int intDimension ()
    {
      return _intComponents == null ? 0 : _intComponents.length;
    }

  public int realDimension ()
    {
      return _realComponents == null ? 0 : _realComponents.length;
    }

  public final int floatDimension ()
    {
        return realDimension();
    }

  public int objectDimension ()
    {
      return _objectComponents == null ? 0 : _objectComponents.length;
    }

  public final int getIntComponent (int position)
    {
      return _intComponents[position-1];
    }
        
  public final int setIntComponent (int position, int value)
    {
      return _intComponents[position-1] = value;
    }
        
  public final double getRealComponent (int position)
    {
      return _realComponents[position-1];
    }
        
  public final double getFloatComponent (int position)
    {
        return getRealComponent(position);
    }
        
  public final double setRealComponent (int position, double value)
    {
      return _realComponents[position-1] = value;
    }
        
  public final double setFloatComponent (int position, double value)
    {
        return setRealComponent(position,value);
    }
        
  public final Object getObjectComponent (int position)
    {
      return _objectComponents[position-1];
    }
        
  public final Object setObjectComponent (int position, Object value)
    {
      return _objectComponents[position-1] = value;
    }

  public final Object getSlicer (int[] slice)
    {
      RuntimeTuple component = this;

      int depth = slice.length-2;
      for (int i=0; i<depth; i++)
        component = (RuntimeTuple)component.getObjectComponent(slice[i]);

      switch (slice[depth+1])
        {
        case Type.INT_SORT:
          return new RuntimeInt(component.getIntComponent(slice[depth]));
        case Type.REAL_SORT:
          return new RuntimeReal(component.getRealComponent(slice[depth]));
        }

      return component.getObjectComponent(slice[depth]);
    }

  public final int hashCode ()
    {
      int icode = 0, rcode = 0, ocode = 0;
      
      int iDim = intDimension();
      for (int i=0; i<iDim; i++)
        icode += _intComponents[i];

      int rDim = realDimension();
      for (int i=0; i<rDim; i++)
        rcode += (int)_realComponents[i];


      int oDim = objectDimension();
      for (int i=0; i<oDim; i++)
        ocode += _objectComponents[i].hashCode();

      return 3*icode + 5*rcode + 7*ocode;
    }

  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof RuntimeTuple))
        return false;

      RuntimeTuple tuple = (RuntimeTuple)object;

      if (intDimension() != tuple.intDimension()) return false;
      if (realDimension() != tuple.realDimension()) return false;
      if (objectDimension() != tuple.objectDimension()) return false;

      int i, dim;

      dim = intDimension();
      for (i=0; i<dim; i++)
        if (_intComponents[i] != tuple.intComponents()[i]) return false;

      dim = realDimension();
      for (i=0; i<dim; i++)
        if (_realComponents[i] != tuple.realComponents()[i]) return false;

      dim = objectDimension();
      for (i=0; i<dim; i++)
        if (!_objectComponents[i].equals(tuple.objectComponents()[i])) return false;

      return true;
    }

  public final String toString ()
    {
      StringBuilder buf = new StringBuilder("<");
      int i, dim;

      dim = intDimension();
      for (i=0; i<dim; i++)
        buf.append(_intComponents[i]+(i==dim-1?"":","));

      buf.append("|");

      dim = realDimension();
      for (i=0; i<dim; i++)
        buf.append(_realComponents[i]+(i==dim-1?"":","));

      buf.append("|");

      dim = objectDimension();
      for (i=0; i<dim; i++)
        buf.append(_objectComponents[i]+(i==dim-1?"":","));

      return buf.append(">").toString();
    }

}
