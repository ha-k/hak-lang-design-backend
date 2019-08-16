//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.types.CodeEntry;
import hlt.language.design.instructions.Instruction;

import hlt.language.tools.Misc;

import java.util.HashMap;

/**
 * A <tt>Closure</tt> object is to a <a href="Block.html"><tt>Block</tt></a> object
 * what  an <a href="../kernel/Abstraction.html"><tt>Abstraction</tt></a> expression
 * is to a <a href="../kernel/Scope.html"><tt>Scope</tt></a> expression. In other words,
 * it is a <tt>Block</tt> with the added frame machinery.
 */
public class Closure extends Block
{
  private int[]    _intFrame;
  private double[] _realFrame;
  private Object[] _objectFrame;

  private boolean _isExitable = true;

  public final boolean isExitable ()
    {
      return _isExitable;
    }

  public Closure (Instruction[] code, int address,
                  int voidArity, int intArity, int realArity, int objectArity,
                  int[] intFrame, double[] realFrame, Object[] objectFrame)
    {
      super(code,address,voidArity,intArity,realArity,objectArity);
      _intFrame = intFrame;
      _realFrame = realFrame;
      _objectFrame = objectFrame;
    }

  public Closure (boolean isExitable, Instruction[] code, int address,
                  int voidArity, int intArity, int realArity, int objectArity,
                  int[] intFrame, double[] realFrame, Object[] objectFrame)
    {
      this(code,address,voidArity,intArity,realArity,objectArity,intFrame,realFrame,objectFrame);
      _isExitable = isExitable;
    }

  public final int[] intFrame ()
    {
      return _intFrame;
    }

  public final double[] realFrame ()
    {
      return _realFrame;
    }

  public final Object[] objectFrame ()
    {
      return _objectFrame;
    }

  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof Closure))
        return false;

      Closure closure = (Closure)object;

      return _address == closure.address()
          && _voidArity == closure.voidArity()
          && _intArity == closure.intArity()
          && _realArity == closure.realArity()
          && _objectArity == closure.objectArity()
          && _isExitable == closure.isExitable()
          && Misc.equal(_intFrame,closure.intFrame())
          && Misc.equal(_realFrame,closure.realFrame())
          && Misc.equal(_objectFrame,closure.objectFrame())
          && (_code == null ? closure.code() == null : _code == closure.code());
    }

  public final int hashCode ()
    {
      return 2*_address + (_code == null ? 0 : 3*_code.length)
           + 5*_voidArity + 7*_intArity + 11*_realArity + 13*_objectArity
           + 17*_intFrame.length + 19*_realFrame.length + 23*_objectFrame.length;           
    }

  public final String toString ()
    {
      String s = null;

      if (_dejaVu) // indicate that this closure is that at the outset with same code and address ...
        s = "CLOSURE(" + CodeEntry.getId(_code) + "," + _address + ", ...)";
      else
        {
          _dejaVu = true;

          s = "CLOSURE(" + CodeEntry.getId(_code) + "," + _address + ","
            + "<" + _voidArity + "," + _intArity + "," + _realArity + "," + _objectArity + ">,"
            + "<" + Misc.arrayToString(_intFrame) + ","
                  + Misc.arrayToString(_realFrame) + ","
                  + Misc.arrayToString(_objectFrame) + ">)";

          _dejaVu = false;
        }

      return s;
    }

}
