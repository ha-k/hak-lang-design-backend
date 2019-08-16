//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.HashMap;
import java.util.Iterator;

import hlt.language.tools.Misc;
import hlt.language.util.IntIterator;
import hlt.language.util.DoubleIterator;
import hlt.language.design.types.*;

/**
 * This class defines methods that format runtime values according to their types
 * to enable them to be legibly displayed.
 */
public class DefaultDisplayFormManager implements DisplayFormManager
{
  public String displayVoid ()
    {
      return "()";
    }

  public String typedDisplayForm (int n, Type type)
    {
      return quotedDisplayForm(n,type) + " : " + type;
    }

  public String typedDisplayForm (double x)
    {
      return quotedDisplayForm(x) + " : real";
    }

  public String typedDisplayForm (Object o, Type type)
    {
      return quotedDisplayForm(o,type) + " : " + type.toQuantifiedString();
    }

  public String quotedDisplayForm (int n, Type type)
    {
      if (type.isBoolean())
        return n == 0 ? "false" : "true";

      if (type.isChar())
        return "'" + String.valueOf((char)n) + "'";

      return String.valueOf(n);
    }
        
  public String unquotedDisplayForm (int n, Type type)
    {
      String s = quotedDisplayForm (n,type);

      if (type.isChar())
        {
          s = Misc.stringify(s);
          return s.substring(1,s.length()-1);
        }

      return s;
    }

  public String quotedDisplayForm (double x)
    {
      return String.valueOf(x);
    }

  public String unquotedDisplayForm (double x)
    {
        return quotedDisplayForm(x);
    }

  public String unquotedDisplayForm (Object o, Type type)
    {
      if (type.isString())
        return (String)o;

      String s = quotedDisplayForm(o,type);

      if (type.isChar())
        {
          s = Misc.stringify(s);
          return s.substring(1,s.length()-1);
        }

      return s;
    }

  public String quotedDisplayForm (Object o, Type type)
    {
      type = type.actualType();

      if (o == null)
        return _nullDisplayForm(type);

      if (o instanceof Closure)
        return "<function>";

      if (type == Type.STRING)
        return "\"" + Misc.stringify((String)o) + "\"";

      if (o instanceof RuntimeInt)
        return quotedDisplayForm(((RuntimeInt)o).value(),type);

      if (o instanceof RuntimeReal)
        return quotedDisplayForm(((RuntimeReal)o).value());

      if (o instanceof RuntimeSet)
        return _setDisplayForm((RuntimeSet)o,((SetType)type).baseType());

      if (o instanceof int[])
        return _intArrayDisplayForm((int[])o,((ArrayType)type).baseType());

      if (o instanceof double[])
        return _realArrayDisplayForm((double[])o);

      if (o instanceof Object[])
        return _objectArrayDisplayForm((Object[])o,((ArrayType)type).baseType());

      if (o instanceof RuntimeMap)
        return _mapDisplayForm((RuntimeMap)o,
                               ((ArrayType)type).baseType(),
                               (((ArrayType)type).indexSetType()).baseType());

      if (o instanceof RuntimeTuple)
        return _tupleDisplayForm((RuntimeTuple)o,(TupleType)type);

      if (o instanceof ObjectInstance)
        return _objectInstanceDisplayForm((ObjectInstance)o,(ClassType)type);

      return o.toString();
    }

  private final String _nullDisplayForm (Type type)
    {
      if (type.isVoid()) return displayVoid();
      if (type.isInt()) return "0";
      if (type.isReal()) return "0.0";
      if (type.isBoolean()) return "false";
      if (type.isChar()) return "''";
      if (type.isString()) return "\"\"";
      return "null";
    }

  private final String _setDisplayForm (RuntimeSet set, Type baseType)
    {
      if (set instanceof ObjectSet)
        return _objectSetDisplayForm((ObjectSet)set,baseType);

      if (set instanceof IntSet)
        return _intSetDisplayForm((IntSet)set,baseType);

      return _realSetDisplayForm((RealSet)set);
    }

  private final String _objectSetDisplayForm (ObjectSet set, Type baseType)
    {
      StringBuilder buf = new StringBuilder("{");
      for (Iterator i=set.orderedIterator(); i.hasNext();)
        buf.append(quotedDisplayForm(i.next(),baseType) + (i.hasNext() ? "," : ""));
      return buf.append("}").toString();
    }

  private final String _intSetDisplayForm (IntSet set, Type baseType)  
    {
      StringBuilder buf = new StringBuilder("{");
      for (IntIterator i=set.orderedIterator(); i.hasNext();)
        buf.append(quotedDisplayForm(i.next(),baseType) + (i.hasNext() ? "," : ""));
      return buf.append("}").toString();
    }

  private final String _realSetDisplayForm (RealSet set)  
    {
      StringBuilder buf = new StringBuilder("{");
      for (DoubleIterator i=set.orderedIterator(); i.hasNext();)
        buf.append(i.next() + (i.hasNext() ? "," : ""));
      return buf.append("}").toString();
    }

  private final String _intArrayDisplayForm (int[] array, Type baseType)
    {
      StringBuilder buf = new StringBuilder("#[");
      for (int i=0; i<array.length; i++)
        buf.append(quotedDisplayForm(array[i],baseType) + (i<array.length-1 ? "," : ""));
      return buf.append("]#").toString();
    }

  private final String _realArrayDisplayForm (double[] array)
    {
      StringBuilder buf = new StringBuilder("#[");
      for (int i=0; i<array.length; i++)
        buf.append(array[i]+(i<array.length-1 ? "," : ""));
      return buf.append("]#").toString();
    }

  private final String _objectArrayDisplayForm (Object[] array, Type baseType)
    {
      StringBuilder buf = new StringBuilder("#[");
      for (int i=0; i<array.length; i++)
        buf.append(quotedDisplayForm(array[i],baseType) + (i<array.length-1 ? "," : ""));
      return buf.append("]#").toString();
    }

  private final String _mapDisplayForm (RuntimeMap map, Type baseType, Type indexType)
    {
      if (baseType.boxSort() == Type.INT_SORT)
        return _intMapDisplayForm((IntMap)map,baseType,indexType);

      if (baseType.boxSort() == Type.REAL_SORT)
        return _realMapDisplayForm((RealMap)map,indexType);

      return _objectMapDisplayForm((ObjectMap)map,baseType,indexType);
    }

  private final String _intMapDisplayForm (IntMap map, Type baseType, Type indexType)
    {
      Indexable indexable = (Indexable)map.indexable();

      StringBuilder buf = new StringBuilder("#[");

      if (indexable instanceof IntRange || indexable instanceof IntSet)
        {
          for (IntIterator i=(IntIterator)indexable.intIterator(); i.hasNext();)
            {
              int element = i.next();
              buf.append(quotedDisplayForm(element,indexType))
                 .append(":")
                 .append(quotedDisplayForm(map.get(element),baseType));
              if (i.hasNext()) buf.append(",");
            }

          return buf.append("]#").toString();
        }

      if (indexable instanceof RealSet)
        {
          for (DoubleIterator i=indexable.realIterator(); i.hasNext();)
            {
              double element = i.next();
              buf.append(element)
                 .append(":")
                 .append(quotedDisplayForm(map.get(element),Type.INT));
              if (i.hasNext()) buf.append(",");
            }

          return buf.append("]#").toString();
        }

      for (Iterator i=indexable.iterator(); i.hasNext();)
        {
          Object element = i.next();
          buf.append(quotedDisplayForm(element,indexType))
             .append(":")
             .append(quotedDisplayForm(map.get(element),Type.INT));
          if (i.hasNext()) buf.append(",");
        }

      return buf.append("]#").toString();
    }

  private final String _realMapDisplayForm (RealMap map, Type indexType)
    {
      Indexable indexable = (Indexable)map.indexable();

      StringBuilder buf = new StringBuilder("#[");

      if (indexable instanceof IntRange || indexable instanceof IntSet)
        {
          for (IntIterator i=(IntIterator)indexable.intIterator(); i.hasNext();)
            {
              int element = i.next();
              buf.append(quotedDisplayForm(element,indexType))
                 .append(":")
                 .append(quotedDisplayForm(map.get(element)));
              if (i.hasNext()) buf.append(",");
            }

          return buf.append("]#").toString();
        }

      if (indexable instanceof RealSet)
        {
          for (DoubleIterator i=indexable.realIterator(); i.hasNext();)
            {
              double element = i.next();
              buf.append(element)
                 .append(":")
                 .append(quotedDisplayForm(map.get(element)));
              if (i.hasNext()) buf.append(",");
            }

          return buf.append("]#").toString();
        }

      for (Iterator i=indexable.iterator(); i.hasNext();)
        {
          Object element = i.next();
          buf.append(quotedDisplayForm(element,indexType))
             .append(":")
             .append(quotedDisplayForm(map.get(element)));
          if (i.hasNext()) buf.append(",");
        }

      return buf.append("]#").toString();
    }

  private final String _objectMapDisplayForm (ObjectMap map, Type baseType, Type indexType)
    {
      Indexable indexable = (Indexable)map.indexable();

      StringBuilder buf = new StringBuilder("#[");

      if (indexable instanceof IntRange || indexable instanceof IntSet)
        {
          for (IntIterator i=(IntIterator)indexable.intIterator(); i.hasNext();)
            {
              int element = i.next();
              buf.append(quotedDisplayForm(element,indexType))
                 .append(":")
                 .append(quotedDisplayForm(map.get(element),baseType));
              if (i.hasNext()) buf.append(",");
            }

          return buf.append("]#").toString();
        }

      if (indexable instanceof RealSet)
        {
          for (DoubleIterator i=indexable.realIterator(); i.hasNext();)
            {
              double element = i.next();
              buf.append(element)
                 .append(":")
                 .append(quotedDisplayForm(map.get(element),baseType));
              if (i.hasNext()) buf.append(",");
            }

          return buf.append("]#").toString();
        }

      for (Iterator i=indexable.iterator(); i.hasNext();)
        {
          Object element = i.next();
          buf.append(quotedDisplayForm(element,indexType))
             .append(":")
             .append(quotedDisplayForm(map.get(element),baseType));
          if (i.hasNext()) buf.append(",");
        }

      return buf.append("]#").toString();
    }

  private final String _tupleDisplayForm (RuntimeTuple tuple, TupleType type)
    {
      if (type instanceof NamedTupleType)
        return _namedTupleDisplayForm(tuple,(NamedTupleType)type);

      StringBuilder buf = new StringBuilder("<");

      int intPos = 0, realPos = 0, objectPos = 0;
      int dimension = type.dimension();

      for (int i=0; i<dimension; i++)
        {
          Type componentType = type.component(i);
          switch (componentType.boxSort())
            {
            case Type.INT_SORT:
              buf.append(quotedDisplayForm(tuple.intComponents()[intPos++],componentType));
              break;
            case Type.REAL_SORT:
              buf.append(quotedDisplayForm(tuple.realComponents()[realPos++]));
              break;
            default:
              buf.append(quotedDisplayForm(tuple.objectComponents()[objectPos++],componentType));
            }

          if (i < dimension-1) buf.append(",");
        }

      return buf.append(">").toString();
    }

  private final String _namedTupleDisplayForm (RuntimeTuple tuple, NamedTupleType type)
    {
      int intPos = 0, realPos = 0, objectPos = 0;
      int dimension = type.dimension();

      String[] components= new String[dimension];

      for (int i=0; i<dimension; i++)
        {
          Type componentType = type.component(i);
          switch (componentType.boxSort())
            {
            case Type.INT_SORT:
              components[i] = quotedDisplayForm(tuple.intComponents()[intPos++],componentType);
              break;
            case Type.REAL_SORT:
              components[i] = quotedDisplayForm(tuple.realComponents()[realPos++]);
              break;
            default:
              components[i] = quotedDisplayForm(tuple.objectComponents()[objectPos++],componentType);
            }
        }

      StringBuilder buf = new StringBuilder("<");

      for (int i=0; i<dimension; i++)
        buf.append(type.fields()[type.index()[i]])
           .append(":=")
           .append(components[type.index()[i]])
           .append(i==dimension-1?"":",");

      return buf.append(">").toString();
    }

  private final String _objectInstanceDisplayForm(ObjectInstance o,ClassType type)
    {
      String tag = _getTag(o);
      boolean dejaVu = (tag != null);

      if (!dejaVu) tag = _putTag(o);

      StringBuilder buf = new StringBuilder(type.name()+tag);

      if (!dejaVu)
        {
          buf.append("{");

          DefinedEntry[] fields = type.fields();

          if (fields.length == 0) return buf+"}";

          for (int i=0; i<fields.length; i++)
            buf.append(fields[i].symbol()+" = "+_fieldDisplayForm(o,fields[i])
                       +(i == fields.length-1?"}":", "));
        }

      return buf.toString();
    }

//    private final String _fieldDisplayForm (ObjectInstance o, DefinedEntry entry)
//      {
//        switch (entry.fieldSort())
//          {
//          case Type.INT_SORT:
//            return displayForm(o.getIntField(entry.fieldOffset()),entry.fieldType());
//          case Type.REAL_SORT:
//            return displayForm(o.getRealField(entry.fieldOffset()));
//          }
//        return displayForm(o.getObjectField(entry.fieldOffset()),entry.fieldType());
//      }

  private final String _fieldDisplayForm (ObjectInstance o, DefinedEntry entry)
    {
      o.type().bindArguments();
      Type type = entry.fieldType().copy();
      o.type().unbindArguments();

      //hlt.language.tools.Debug.step(type);

      switch (entry.fieldSort())
        {
        case Type.INT_SORT:
          return quotedDisplayForm(o.getIntField(entry.fieldOffset()),type);
        case Type.REAL_SORT:
          return quotedDisplayForm(o.getRealField(entry.fieldOffset()));
        }
      return quotedDisplayForm(o.getObjectField(entry.fieldOffset()),type);
    }

  /**
   * The following is a table to keep a record of tags labelling objects that have
   * already been displayed and thus need not be redisplayed beyond their tag.
   * This allows printing circular objects, and saves on the display form of objects
   * that are shared. A tag is of the form <tt>Classname#n</tt>, where <tt>Classname</tt>
   * is the name of the class of the tagged object and <tt>n</tt> is a number. Therefore,
   * the table <tt>_tags</tt> is a table of tables: it maps a classname to a table that
   * maps a number to the object it designates.
   */
  private final HashMap _tags = new HashMap();

  public DisplayFormManager clearTags ()
    {
      _tags.clear();
      return this;
    }

  private final String _getTag (ObjectInstance o)
    {
      HashMap typeTags = (HashMap)_tags.get(o.type());

      if (typeTags == null)
        {
          _tags.put(o.type(),typeTags = new HashMap());
          return null;
        }

      return (String)typeTags.get(o);
    }

  private final String _putTag (ObjectInstance o)
    {
      HashMap typeTags = (HashMap)_tags.get(o.type());

      if (typeTags == null)
        _tags.put(o.type(),typeTags = new HashMap());

      String tag = "#"+typeTags.size();
      typeTags.put(o,tag);

      return tag;
    }

}
