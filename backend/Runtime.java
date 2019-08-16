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
import hlt.language.design.types.ClassType;
import hlt.language.design.types.CodeEntry;
import hlt.language.design.types.DefinedEntry;
import hlt.language.design.instructions.Instruction;

import hlt.language.util.ArrayList;
import hlt.language.util.Stack;
import hlt.language.util.IntStack;
import hlt.language.util.DoubleStack;
import hlt.language.tools.Debug;

/**
 * This is the class defining a runtime object. Such an object serves
 * as the common execution environment context shared by <a
 * href="../instructions/Instruction.html"><tt>Instruction</tt></a>s being
 * executed. It encapsulates a state of comptutation that is
 * effected by each instruction as it is executed in its context.
 *
 * <p>
 * 
 * A <tt>Runtime</tt> object consists of attributes and structures that
 * together define a state of computation, and methods that are used by
 * instructions to effect this state as they are executed. Thus, each
 * instruction class defines an <tt>execute(Runtime)</tt> method that
 * specifies its operational semantics as a state transformation of its
 * given runtime context.
 *
 * <p>
 *
 * Initiating execution of a <tt>Runtime</tt> object consists of setting
 * its code array to a given instruction sequence, setting its
 * instruction pointer <tt>_ip</tt> to its code's first instruction and
 * repeatedly calling <tt>execute(this)</tt> on whatever instruction is
 * currently at address <tt>_ip</tt> in the current code array. The
 * final state is reached when a flag indicating that it is so is set to
 * <tt>true</tt>. Each instruction is responsible for appropriately
 * setting the next state according to its semantics, including saving
 * and restoring states, and (re)setting the code array and the various
 * runtime registers pointing into the state's structures.
 *
 * <p>
 * 
 * Runtime states encapsulated by objects in this class are essentially
 * those of a stack automaton, specifically conceived to support the
 * computations of a higher-order functional language with lexical
 * closures - <i>i.e.</i>, a &lambda;-calculus machine. As
 * such it may viewed as an optimized variant of Peter Landin's SECD
 * machine - in the same spirit as Luca Cardelli's Functional Abstract
 * Machine (FAM), although our design is quite different from
 * Cardelli's in its structure and operations.  It supports many
 * features beyond basic &lambda;-calculus - <i>e.g.</i>,
 * in-place assignments (both stack-based and heap-based), automatic
 * boxing/unboxing for polymorphic functions, objects, automatic
 * currying, to name a few...
 *
 * <p>
 *
 * Because this is a Java implementation, in order to avoid the space
 * and performance overhead of being confined to boxed values for
 * primitive type computations, three concurrent sets of structures are
 * maintained: in addition to those needed for boxed (Java object)
 * values, two extra ones are used to support unboxed integer and
 * floating-point values, respectively. The runtime operations performed
 * by instructions on a <tt>Runtime</tt> object are guaranteed to be
 * type-safe in that each state is always such as it must be expected for
 * the correct accessing and setting of values. Such a guarantee must be
 * (and is!) provided by the <a href="../types/TypeChecker.html"><tt>TypeChecker</tt></a>
 * and the <a href="../kernel/Sanitizer.html"><tt>Sanitizer</tt></a>, which
 * ascertain all the conditions that must be met prior to having a <a
 * href="../kernel/Compiler.html"><tt>Compiler</tt></a> proceed to generating
 * instructions which will safely act on the appropriate stacks and
 * environments of the correct sort (integer, floating-point, or object).
 */

public class Runtime
{
  /**
   * A constant that denotes the <i>unboxed</i> boolean <tt>false</tt> at runtime. 
   */
  public static final int FALSE =  0;
  /**
   * A constant that denotes the <i>unboxed</i> boolean <tt>true</tt> at runtime. 
   */
  public static final int TRUE  =  1;
  /**
   * A constant that denotes the <i>unboxed</i> <tt>void</tt> value at runtime. 
   */
  public static final int VOID  = -1;

  /**
   * A constant that denotes the <i>boxed</i> boolean <tt>false</tt> at runtime. 
   */
  public static final RuntimeInt BOXED_FALSE = newInt(FALSE);
  /**
   * A constant that denotes the <i>boxed</i> boolean <tt>true</tt> at runtime. 
   */
  public static final RuntimeInt BOXED_TRUE  = newInt(TRUE);
  /**
   * A constant that denotes the <i>boxed</i> <tt>void</tt> value at runtime. 
   */
  public static final RuntimeInt BOXED_VOID  = newInt(VOID);

  //////////////////////////////////////////////////////////////////////////

  /**
   * This is the code array containing the instruction sequence being currently
   * executed.
   */
  protected Instruction[] _code;

  /**
   * This is the <i>instruction pointer</i> - an index into the code array
   * indicating the instruction to execute next.
   */
  protected int _ip;

  /**
   * This is the stack recording saved states of computation to allow safe
   * excursions on closure applications. This is affected by the
   * <tt>saveState()</tt> and <tt>restoreState()</tt> methods.
   */
  protected Stack         _saveStack      = new Stack();

  /**
   * This is the stack recording saved states of computation to allow safe
   * excursions on definition calls. This is affected by the <tt>pushCall()</tt>
   * and <tt>popCall()</tt> methods.
   */
  protected Stack         _callStack      = new Stack();

  /**
   * <b>The three following stacks record intermediate results of computations (<i>i.e.</i>,
   * the values returned by expressions that are arguments of another expression).</b>
   */

  /**
   * This stack records intermediate integer results.
   */
  protected IntStack      _intStack       = new IntStack();
  /**
   * This stack records intermediate floating-point number results.
   */
  protected DoubleStack   _realStack      = new DoubleStack();
  /**
   * This stack records intermediate object results.
   */
  protected Stack        _objectStack    = new Stack();

  /**
   * <b>The three following stacks record closure environments (<i>i.e.</i>, encapsulated
   * values of local variables that belong to enclosing lexical scopes).</b>
   */

  /**
   * This stack records integer environments.
   */
  protected IntStack      _intEnv         = new IntStack();
  /**
   * This stack records floating-point number environments.
   */
  protected DoubleStack   _realEnv        = new DoubleStack();
  /**
   * This stack records object environments.
   */
  protected Stack         _objectEnv      = new Stack();

  /**
   * When non <tt>null</tt>, this is the state saved when the current
   * enclosing exitable closure is applied.
   */
  protected State _currentExitableState = null;

  /**
   * This value records the runtime sort of the latest value that was pushed
   * on one of the three result stacks.
   */
  protected byte _resultSort;

  /**
   * This flag indicates that execution has completed.
   */
  private boolean _terminated = false;

  /**
   * This flag indicates that tracing in on (for debugging purposes).
   */
  private static boolean _tracing = false;

  ////////////////////////////////////////////////////////////////////
  
  /**
   * This is the default display manager associated to this runtime.
   */
  private DisplayManager _displayManager = new DefaultDisplayManager();

  /**
   * Returns this runtime's display manager.
   */
  public final DisplayManager displayManager ()
    {
      return _displayManager;
    }

  /**
   * Returns this runtime's display manager.
   */
  public final DisplayManager getDisplayManager ()
    {
      return _displayManager;
    }

  /**
   * Sets this runtime's display manager to the specified one, and returns
   * this runtime.
   */
  public final Runtime setDisplayManager (DisplayManager displayManager)
    {
      _displayManager = displayManager;
      return this;
    }

  /**
   * Returns this runtime's display device manager.
   */
  public final DisplayDeviceManager displayDeviceManager ()
    {
      return _displayManager.displayDeviceManager();
    }

  /**
   * Returns this runtime's display device manager.
   */
  public final DisplayDeviceManager getDisplayDeviceManager ()
    {
      return _displayManager.displayDeviceManager();
    }

  /**
   * Sets this runtime's display Device manager to the specified one, and returns
   * this runtime.
   */
  public final Runtime setDisplayDeviceManager (DisplayDeviceManager displayDeviceManager)
    {
      _displayManager.setDisplayDeviceManager(displayDeviceManager);
      return this;
    }

  /**
   * Returns this runtime's display form manager.
   */
  public final DisplayFormManager displayFormManager ()
    {
      return _displayManager.displayFormManager();
    }

  /**
   * Returns this runtime's display form manager.
   */
  public final DisplayFormManager getDisplayFormManager ()
    {
      return _displayManager.displayFormManager();
    }

  /**
   * Sets this runtime's display Form manager to the specified one, and returns
   * this runtime.
   */
  public final Runtime setDisplayFormManager (DisplayFormManager displayFormManager)
    {
      _displayManager.setDisplayFormManager(displayFormManager);
      return this;
    }

  ////////////////////////////////////////////////////////////////////
  
  /**
   * Sets the code array to the specified one.
   */
  public final void setCode (Instruction[] code)
    {
      _code = code;
    }

  /**
   * Returns the value of the current instruction pointer.
   */
  public final int ip ()
    {
      return _ip;
    }

  /**
   * Sets the value of the current instruction pointer to the specified one.
   */
  public final void setIP (int ip)
    {
      _ip = ip;
    }

  /**
   * Resets the value of the current instruction pointer to 0, and unsets
   * the termination flag.
   */
  public final void resetIP ()
    {
      _ip = 0;
      _terminated = false;
    }

  /**
   * Increments the instruction pointer (<i>i.e.</i>, sets it to point to the
   * next instruction in the code array).
   */
  public final void incIP ()
    {
      _ip++;
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Pushes the specified integer on the result stack.
   */
  public final void pushInt (int x)
    {
      _intStack.push(x);
      _resultSort = Type.INT_SORT;
    }

  /**
   * Pushes the specified double on the result stack.
   */
  public final void pushReal (double x)
    {
      _realStack.push(x);
      _resultSort = Type.REAL_SORT;
    }

  /**
   * Pushes the specified object on the result stack.
   */
  public final void pushObject (Object x)
    {
      _objectStack.push(x);
      _resultSort = Type.OBJECT_SORT;
    }

  /**
   * Pushes the specified boolean on the result stack.
   */
  public final void pushBoolean (boolean x)
    {
      _intStack.push(x ? TRUE : FALSE);
      _resultSort = Type.INT_SORT;
    }

  /**
   * Returns the char corresponding to the Unicode numeric value popped from
   * the result stack. This assumes that the integer value is non-negative.
   */
  public final char popChar ()
    {
      return (char)popInt();
    }

  /**
   * Pushes the Unicode numeric value of the character as a nonnegative integer
   * on the result stack.
   */
  public final void pushChar (char c)
    {
      pushInt(c);
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Returns the runtime sort of the latest value pushed on the result stack.
   */
  public final byte resultSort ()
    {
      return _resultSort;
    }

  /**
   * Set the runtime sort of the latest value pushed on the result stack to
   * be that of an int. This is useful for maintaining consistency when an
   * instruction acts on the top of the result stack without popping it.
   */
  public final void setIntSort ()
    {
      _resultSort = Type.INT_SORT;
    }

  /**
   * Set the runtime sort of the latest value pushed on the result stack to
   * be that of a real. This is useful for maintaining consistency when an
   * instruction acts on the top of the result stack without popping it.
   */
  public final void setRealSort ()
    {
      _resultSort = Type.REAL_SORT;
    }

  /**
   * Set the runtime sort of the latest value pushed on the result stack to
   * be that of an object. This is useful for maintaining consistency when
   * an instruction acts on the top of the result stack without popping it.
   */
  public final void setObjectSort ()
    {
      _resultSort = Type.OBJECT_SORT;
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Returns the value of the latest integer pushed on the result stack.
   */
  public final int intResult ()
    {
      return _intStack.peek();
    }

  /**
   * Returns the value of the latest floating-point number pushed on the
   * result stack.
   */
  public final double realResult ()
    {
      return _realStack.peek();
    }

  /**
   * Returns the value of the latest object pushed on the result stack.
   */
  public final Object objectResult ()
    {
      return _objectStack.peek();
    }

  /**
   * Returns the value of the latest boolean pushed on the result stack.
   */
  public final boolean booleanResult ()
    {
      return _intStack.peek() == TRUE;
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Removes and returns the value of the latest integer pushed on the result
   * stack.
   */
  public final int popInt ()
    {
      return _intStack.pop();
    }

  /**
   * Removes and returns the value of the latest floating-point number pushed
   * on the result stack.
   */
  public final double popReal ()
    {
      return _realStack.pop();
    }

  /**
   * Removes and returns the value of the latest object pushed on the result
   * stack.
   */
  public final Object popObject ()
    {
      return _objectStack.pop();
    }

  /**
   * Removes and returns the value of the latest object pushed on the
   * result stack if this object is not <tt>null</tt>. Otherwise, throws
   * a <tt>NullValueException</tt> with the specified object as
   * argument.  */
  public final Object popObject (Object infoIfNull) throws NullValueException
    {
      Object object = _objectStack.pop();

      if (object == null)
        throw new NullValueException(infoIfNull);
      
      return object;
    }

  /**
   * Removes and returns the value of the latest boolean pushed on the result
   * stack.
   */
  public final boolean popBoolean ()
    {
      return _intStack.pop() == TRUE;
    }

  /**
   * Removes the latest object pushed on the result stack, which must be an
   * array or an <a href="IntMap.html"><tt>IntMap</tt></a>, and returns
   * the array (<i>i.e.</i>, if it's a map, extracts its array).
   */
  public int[] popIntArray ()
    {
      Object a = _objectStack.pop();

      if (a instanceof IntMap)
        return ((IntMap)a).array();

      return (int[])a;
    }

  /**
   * If the latest object pushed on the result stack is <tt>null</tt>, this
   * throws a <tt>NullValueException</tt> with the specified object as argument;
   * otherwise, this works as <tt>popIntArray()</tt>.
   */
  public int[] popIntArray (Object infoIfNull) throws NullValueException
    {
      Object a = popObject(infoIfNull);

      if (a instanceof IntMap)
        return ((IntMap)a).array();

      return (int[])a;
    }

  /**
   * Removes the latest object pushed on the result stack, which must be an
   * array or a <a href="RealMap.html"><tt>RealMap</tt></a>, and returns
   * the array (<i>i.e.</i>, if it's a map, extracts its array).
   */
  public double[] popRealArray ()
    {
      Object a = _objectStack.pop();

      if (a instanceof RealMap)
        return ((RealMap)a).array();

      return (double[])a;
    }

  /**
   * If the latest object pushed on the result stack is <tt>null</tt>, this
   * throws a <tt>NullValueException</tt> with the specified object as argument;
   * otherwise, this works as <tt>popRealArray()</tt>.
   */
  public double[] popRealArray (Object infoIfNull) throws NullValueException
    {
      Object a = popObject(infoIfNull);

      if (a instanceof RealMap)
        return ((RealMap)a).array();

      return (double[])a;
    }

  /**
   * Removes the latest object pushed on the result stack, which must be an
   * array or an <a href="ObjectMap.html"><tt>ObjectMap</tt></a>, and returns
   * the array (<i>i.e.</i>, if it's a map, extracts its array).
   */
  public Object[] popObjectArray ()
    {
      Object a = _objectStack.pop();

      if (a instanceof ObjectMap)
        return ((ObjectMap)a).array();

      return (Object[])a;
    }

  /**
   * If the latest object pushed on the result stack is <tt>null</tt>, this
   * throws a <tt>NullValueException</tt> with the specified object as argument;
   * otherwise, this works as <tt>popObjectArray()</tt>.
   */
  public Object[] popObjectArray (Object infoIfNull) throws NullValueException
    {
      Object a = popObject(infoIfNull);

      if (a instanceof ObjectMap)
        return ((ObjectMap)a).array();

      return (Object[])a;
    }

  ////////////////////////////////////////////////////////////////////

//    /**
//     * Returns <tt>true</tt> iff the latest object pushed on the result stack
//     * is <tt>null</tt>.
//     */
//    public final boolean objectIsNull ()
//      {
//        return _objectStack.peek() == null;
//      }

//    /**
//     * Returns <tt>true</tt> iff the object preceding the latest object pushed on the
//     * result stack is <tt>null</tt>. This is used by <a href="../instructions/FieldInstruction.html">
//     * <tt>FieldInstruction</tt></a>s when updating a field, in which case the object is the
//     * penultimate object on the object stack.
//     */
//    public final boolean fieldObjectIsNull ()
//      {
//        if (_objectStack.size() < 2) return false;

//        return _objectStack.peek(1) == null;
//      }

  ////////////////////////////////////////////////////////////////////

  /**
   * This returns a boxed integer with the specified int value; values <tt>0</tt>
   * and <tt>1</tt> are boxed into unique canonical objects since they are frequently
   * used.
   */
  public static final RuntimeInt newInt (int value)
    {
      switch (value)
        {
        case 0: return RuntimeInt.ZERO;      
        case 1: return RuntimeInt.ONE;      
        }
      return new RuntimeInt(value);
    }

  /**
   * This returns a boxed real number with the specified double value; the value
   * <tt>0.0</tt> is boxed into a unique canonical object it is frequently used.
   */
  public static final RuntimeReal newReal (double value)
    {
      if (value == 0.0) return  RuntimeReal.ZERO;      
      return new RuntimeReal(value);
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Pushes the specified int value on the environment stack.
   */
  public final void pushIntEnv (int x)
    {
      _intEnv.push(x);
    }

  /**
   * Pushes the specified double value on the environment stack.
   */
  public final void pushRealEnv (double x)
    {
      _realEnv.push(x);
    }

  /**
   * Pushes the specified object on the environment stack.
   */
  public final void pushObjectEnv (Object x)
    {
      _objectEnv.push(x);
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Pushes all the elements of the specified int array on the environment stack.
   */
  public final void pushIntEnvArray (int[] frame)
    {
      for (int i=0; i<frame.length; i++)
        _intEnv.push(frame[i]);
    }

  /**
   * Pushes all the elements of the specified double array on the environment stack.
   */
  public final void pushRealEnvArray (double[] frame)
    {
      for (int i=0; i<frame.length; i++)
        _realEnv.push(frame[i]);
    }

  /**
   * Pushes all the elements of the specified object array on the environment stack.
   */
  public final void pushObjectEnvArray (Object[] frame)
    {
      for (int i=0; i<frame.length; i++)
        _objectEnv.push(frame[i]);
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Copies the latest <tt>size</tt> int values from the environment stack into
   * an array, and returns the array.
   */
  public final int[] copyIntEnv (int size)
    {
      int[] env = new int[size];

      int depth = _intEnv.size()-size;
      for (int i=0; i<size; i++)
        env[i] = _intEnv.get(depth+i);
      
      return env;
    }

  /**
   * Copies the latest <tt>size</tt> double values from the environment stack into
   * an array, and returns the array.
   */
  public final double[] copyRealEnv (int size)
    {
      double[] env = new double[size];

      int depth = _realEnv.size()-size;
      for (int i=0; i<size; i++)
        env[i] = _realEnv.get(depth+i);
      
      return env;
    }

  /**
   * Copies the latest <tt>size</tt> objects from the environment stack into
   * an array, and returns the array.
   */
  public final Object[] copyObjectEnv (int size)
    {
      Object[] env = new Object[size];

      int depth = _objectEnv.size()-size;
      for (int i=0; i<size; i++)
        env[i] = _objectEnv.get(depth+i);
      
      return env;
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Returns the int value at offset <tt>n</tt> in the environment stack.
   */
  public final int getIntEnv (int n)
    {
      return _intEnv.peek(n);
    }

  /**
   * Returns the double value at offset <tt>n</tt> in the environment stack.
   */
  public final double getRealEnv (int n)
    {
      return _realEnv.peek(n);
    }

  /**
   * Returns the object at offset <tt>n</tt> in the environment stack.
   */
  public final Object getObjectEnv (int n)
    {
      return _objectEnv.peek(n);
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * The following are needed for initializing arrays and maps with a deep copy.
   */ 

  /**
   * Returns a copy of the specified array of ints.
   */ 
  public final static int[] copy (int[] source)
    {
      int[] copy = new int[source.length];
      System.arraycopy(source,0,copy,0,source.length);
      return copy;
    }

  /**
   * Returns a copy of the specified array of doubles.
   */ 
  public final static double[] copy (double[] source)
    {
      double[] copy = new double[source.length];
      System.arraycopy(source,0,copy,0,source.length);
      return copy;
    }

  /**
   * Returns a deep copy of the specified array of objects; <i>i.e.</i>, objects
   * that are arrays or <a href="RuntimeMap.html"><tt>RuntimeMap</tt></a>s are
   * also copied recursively.
   */ 
  public final static Object[] copy (Object[] source)
    {
      Object[] copy = new Object[source.length];
      for (int i=0; i<copy.length; i++)
        copy[i] = _copyIfArray(source[i]);
      return copy;
    }

  /**
   * Returns a deep array copy of the specified object; <i>i.e.</i>, it it is an
   * array of objects or <a href="RuntimeMap.html"><tt>RuntimeMap</tt></a> it is
   * copied recursively, otherwise the specified object is returned.
   */ 
  private final static Object _copyIfArray (Object object)
    {
      if (object instanceof int[])
        return copy((int[])object);

      if (object instanceof double[])
        return copy((double[])object);

      if (object instanceof Object[])
        return copy((Object[])object);

      if (object instanceof RuntimeMap)
        return ((RuntimeMap)object).copy();

      return object;
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Sets the int environment stack slot at offset <tt>n</tt> to the latest
   * int value that was pushed on the int result stack. The result stack is
   * not popped.
   */
  public final void setIntEnv (int n)
    {
      _intEnv.replace(n,_intStack.peek());
    }

  /**
   * Sets the double environment stack slot at offset <tt>n</tt> to the latest
   * double value that was pushed on the double result stack. The result stack
   * is not popped.
   */
  public final void setRealEnv (int n)
    {
      _realEnv.replace(n,_realStack.peek());
    }

  /**
   * Sets the object environment stack slot at offset <tt>n</tt> to the latest
   * object that was pushed on the object result stack. The result stack is not
   * popped.
   */
  public final void setObjectEnv (int n)
    {
      _objectEnv.replace(n,_objectStack.peek());
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Sets the topmost int environment stack slot to the specified int value.
   */
  public final void setIntValue (int n)
    {
      _intEnv.setLast(n);
    }

  /**
   * Sets the topmost real environment stack slot to the specified double value.
   */
  public final void setRealValue (double x)
    {
      _realEnv.setLast(x);
    }

  /**
   * Sets the topmost object environment stack slot to the spwcified object value.
   */
  public final void setObjectValue (Object o)
    {
      _objectEnv.setLast(o);
    }

  ////////////////////////////////////////////////////////////////////


  /**
   * Pushes the specified state onto the exitable state stack.  This
   * stack is a substack of the save stack: it is threaded in-place from
   * a global private register <tt>_currentExitableState</tt> which
   * always points to the latest (if any, <tt>null</tt> otherwise)
   * exitable <tt>State</tt> in the save stack that encloses this runtime's
   * current state of computation. The exitable states in the stack are
   * thus link-chained using the <tt>setEnclosingExitableState(State)</tt>
   * and <tt>getEnclosingExitableState()</tt> methods.
   */
  public final void pushExitableState (State state)
    {
      state.setEnclosingExitableState(_currentExitableState);
      _currentExitableState = state.setIsExitable(true);
    }

  /**
   * Pops and returns the latest exitable <tt>State</tt> in the save
   * stack that encloses this runtime's current state of
   * computation. NB: Typechecking garantees that is is always
   * non-<tt>null</tt>.
   */
  public final State popExitableState ()
    {
      State currentExitableState = _currentExitableState;
      _currentExitableState = _currentExitableState.getEnclosingExitableState();

      return currentExitableState;
    }

  /**
   * Saves the current state and pushes it onto the exitable state stack.
   */
  public final void enterExitableScope ()
    {
      pushExitableState(saveState());
    }

  /**
   * Saves the state obtained after the result stacks have been popped as many
   * arguments as the specified arities and pushes it onto the exitable state stack.
   */
  public final void enterExitableScope (int intArity, int realArity, int objectArity)
    {
      pushExitableState(saveState(intArity,realArity,objectArity));
    }

  /**
   * Restores the state that was saved when the latest exitable closure was
   * applied.
   */
  public final void leaveExitableScope ()
    {
      restoreState(popExitableState());
    }

  /**
   * Saves this runtime's current state of computation onto the save
   * stack and returns it.  A state consists of the code array, the next
   * instruction pointer, and the sizes of all the stacks.
   */
  public final State saveState ()
    {
      State state = new State(_code,_ip+1,
                              _intStack.size(),_realStack.size(),_objectStack.size(),
                              _intEnv.size(),_realEnv.size(),_objectEnv.size())
                              .setCurrentExitableState(_currentExitableState);
      _saveStack.push(state);
      return state;
    }

  /**
   * Saves this runtime's state of computation obtained after the result stacks
   * have been popped as many arguments as the specified arities.
   */
  public final State saveState (int intArity, int realArity, int objectArity)
    {
      State state = new State(_code,_ip+1,
                              _intStack.size() - intArity,
                              _realStack.size() - realArity,
                              _objectStack.size() - objectArity,
                              _intEnv.size(),_realEnv.size(),_objectEnv.size())
                              .setCurrentExitableState(_currentExitableState);
      _saveStack.push(state);
      return state;
    }

  /**
   * Restores the latest state of computation that was saved in the save stack
   * into this runtime.
   */
  public final void restoreState ()
    {
      restoreState((State)_saveStack.peek());
      _terminated = false;
    }

  /**
   * Restores the specified state of computation into this runtime.
   */
  public final void restoreState (State s)
    {
      while (_saveStack.peek() != s) _saveStack.pop();
      _saveStack.pop();

      _code = s.code();
      _ip = s.ip();
      _currentExitableState = s.getCurrentExitableState();

      _intStack.setSize(s.intStackPoint());
      _realStack.setSize(s.realStackPoint());
      _objectStack.setSize(s.objectStackPoint());

      _intEnv.setSize(s.intEnvPoint());
      _realEnv.setSize(s.realEnvPoint());
      _objectEnv.setSize(s.objectEnvPoint());
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * If the specified <a href="../types/DefinedEntry.html"><tt>DefinedEntry</tt></a>
   * is safe (<i>i.e.</i>, it and all its calls are fully defined), this pushes it
   * along with this runtime's current code array and next instruction pointer into the
   * call stack, and sets this runtime's code array to that of the specified entry's
   * and reset the instruction pointer to 0. Otherwise, a <tt>UnsafeCodeException</tt>
   * is thrown.
   */
  public final void pushCall (DefinedEntry entry) throws UnsafeCodeException
    {
      if (entry.isUnsafe()) throw new UnsafeCodeException(entry);
      _callStack.push(new CallState(entry,_code,_ip+1));
      _code = entry.code();
      _ip = 0;
    }

  /**
   * If the call stack is  empty, this sets the termination flag to <tt>true</tt>; otherwise,
   * pops a call state and restores from it the code array and the next instruction pointer.
   * If the entry from the popped state is to be set on evaluation, this will modify its code
   * to return the latest value pushed on the appropriate result stack.
   */
  public final void popCall ()
    {
      if (_callStack.isEmpty())
        _terminated = true;
      else
        {
          CallState state = (CallState)_callStack.pop();
          _code = state.code();
          _ip = state.ip();
          DefinedEntry entry = state.entry();
          if (entry.isSetOnEvaluation())
            setValue(entry);
        }
    }

  /**
   * Modifies the code array of the specified <a href="../types/DefinedEntry.html">
   * <tt>DefinedEntry</tt></a> to one that pushes the latest value that was pushed
   * on this runtime's appropriate result stack.
   */
  public final void setValue (DefinedEntry entry)
    {
      switch (entry.type().boxSort())
        {
        case Type.INT_SORT:
          entry.setValue(_intStack.peek());
          break;
        case Type.REAL_SORT:
          entry.setValue(_realStack.peek());
          break;
        default:
          entry.setValue(_objectStack.peek());
        }
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * Resets this runtime to a virginal state and returns this.
   */
  public final Runtime reset ()
    {
      _clearAllStacks();
      _ip = 0;
      _terminated = false;
      return this;
    }      

  /**
   * Resets this runtime and sets the code array to the specified one.
   */
  public final void initialize (Instruction[] code)
    {
      reset();
      _code = code;
    }

  /**
   * Clears all the stacks.
   */
  private final void _clearAllStacks ()
    {
      _saveStack.clear();
      _callStack.clear();

      _intStack.clear();
      _realStack.clear();
      _objectStack.clear();

      _intEnv.clear();
      _realEnv.clear();
      _objectEnv.clear();
    }

  /**
   * Resets this runtime, sets the code array to the specified one, and
   * initiates execution of this runtime's code, which proceeds as long
   * as the termination flag is not set.
   */
  public final void run (Instruction[] code) throws Exception
    {
      initialize(code);
      run();
    }

  /**
   * Initiates execution of this runtime's code and proceeds as long
   * as the termination flag is not set.
   */
  public final void run () throws Exception
    {
       while (!_terminated)
         {
           if (_tracing) _showState();
           _currentInstruction().execute(this);
         }
    }

  /**
   * Initiates execution of the specified block's body, and proceeds as long
   * as the current instruction is not a return instruction.
   */
  public final void runBody (Block block) throws Exception
    {
      _code = block.code();
      _ip = block.address();
      Instruction inst = null;

      for (;;)
         {
           if (_tracing) _showState();
           inst = _currentInstruction();
           if (inst.isReturn()) return;
           inst.execute(this);
         }
    }

  /**
   * Returns the instruction currently indicated by the instruction pointer.
   */
  private final Instruction _currentInstruction ()
    {
      return (Instruction)_code[_ip];
    }

  /**
   * Stops the execution of this runtime.
   */
  public final void stop ()
    {
      _terminated = true;
    }

  /**
   * Toggles the tracing flag.
   */
  public final static void toggleTrace ()
    {
      _tracing = !_tracing;

    }

  /**
   * Returns the tracing flag.
   */
  public final static boolean isTracing ()
    {
      return _tracing;
    }

  ////////////////////////////////////////////////////////////////////

  /**
   * FP: If true the trace must stop at each step
   */
  protected boolean mustStop ()
    {
      return true;
    }
  
  /**
   * Prints out a display form of this runtime's result and environment stacks.
   */
  private final void _showStacks (IntStack i, DoubleStack r, Stack o)
    {
      System.out.println("\n\tINT:\t\tREAL:\t\tOBJECT:\n");

      int depth = Math.max(i.size(),Math.max(r.size(),o.size()));

      for (int j=depth; j-->0;)
        {
          int index;
          
          System.out.println();
          index = j-depth+i.size();
          if (index >= 0)
            System.out.print("\t" + index + ":\t" + i.get(index));
          else
            System.out.print("\t\t");
          index = j-depth+r.size();
          if (index >= 0)
            System.out.print("\t" + index + ":\t" + r.get(index));
          else
            System.out.print("\t\t");
          index = j-depth+o.size();
          if (index >= 0)
            System.out.print("\t" + index + ":\t" + o.get(index));
        }
    }     

  public final void showState () { _showState(); }

  /**
   * Prints out a display form of this runtime's computation state.
   */
  private final void _showState ()
    {
      System.out.println("===============================================");
          
      System.out.println("\nRESULT STACKS:\n");
      _showStacks(_intStack,_realStack,_objectStack);
      System.out.println("\n");

      System.out.println("\nENVIRONMENTS:\n");
      _showStacks(_intEnv,_realEnv,_objectEnv);
      System.out.println("\n");

      System.out.println("\nSAVE STACK:\n");
      for (int i=_saveStack.size(); i-->0;)
        {
          State state = (State)_saveStack.get(i);
          System.out.println("\t" + i + ": " + state+
                             (state == _currentExitableState ? "\t<== CURRENT" : ""));
        }
      System.out.println("\n");

      System.out.println("\nCALL STACK:\n");
      for (int i=_callStack.size(); i-->0;)
        System.out.println("\t" + i + ": " + _callStack.get(i));
      System.out.println("\n");

      CodeEntry.showCode(_code,_ip);

      if (mustStop())
        Debug.step();
    }

  ////////////////////////////////////////////////////////////////////////////

  /**
   * This class denotes information common to any type of state.
   */
  private abstract static class AbstractState
    {
      protected Instruction[] _code;
      protected int _ip;

      protected AbstractState (Instruction[] code, int ip)
        {
          _code = code;
          _ip = ip;
        }

      final Instruction[] code ()
        {
          return _code;
        }

      final int ip ()
        {
          return _ip;
        }

      public String toString ()
        {
          return CodeEntry.getId(_code) + "\tIP = " + _ip;                 
        }
    }
      
  /**
   * This class denotes information comprising a call state.
   */
  protected static class CallState extends AbstractState
    {
      private DefinedEntry _entry;

      CallState (DefinedEntry entry, Instruction[] code, int ip)
        {
          super(code,ip);
          _entry = entry;
        }

      final DefinedEntry entry ()
        {
          return _entry;
        }

      public final boolean equals (Object other)
        {
          if (this == other)
            return true;

          if (!(other instanceof CallState))
            return false;

          return _entry == ((CallState)other)._entry
              && this._code  == ((CallState)other)._code
              && this._ip    == ((CallState)other)._ip;
        }

      public final int hashCode ()
        {
          return _entry.hashCode() + this._code.hashCode() + this._ip;
        }

      public String toString ()
        {
          Type.resetNames();
          return super.toString() + "\tENTRY = " + _entry;
        }
    }
      
  /**
   * This class denotes information comprising a complete computation state.
   */
  protected static class State extends AbstractState
    {
      private int _intStackPoint;
      private int _realStackPoint;
      private int _objectStackPoint;
      private int _intEnvPoint;
      private int _realEnvPoint;
      private int _objectEnvPoint;

      /**
       * This flag indicates whether this state is exitable.
       */
      private boolean _isExitable = false;
      /**
       * This state is the exitable state that is current in the runtime
       * at state-saving time and must be saved as part of the save state.
       */
      private State _currentExitableState;
      /**
       * This is the link for chaining the exitable states within the
       * saved-state stack (from inside out). This is either
       * <tt>null</tt> (if this is the outermost exitable state), or the
       * exitable state that is the immediately enclosing exitable state
       * at the runtime's state of computation at the creation of this state.
       */
      private State _enclosingExitableState;

      /**
       * Returns <tt>true</tt> iff this state is exitable.
       */
      public final boolean isExitable ()
        {
          return _isExitable;
        }

      /**
       * Sets this state to be exitable iff the specified flag is <tt>true</tt>.
       */
      public final State setIsExitable (boolean flag)
        {
          _isExitable = flag;
          return this;
        }

      /**
       * Returns the state that was the exitable state current in the
       * runtime at this state's saving time and that was saved as part
       * of this state.
       */
      public final State getCurrentExitableState ()
        {
          return _currentExitableState;
        }

      /**
       * Returns the last exitable state saved before this one, or
       * <tt>null</tt>, if none exists.
       */
      public final State getEnclosingExitableState ()
        {
          return _enclosingExitableState;
        }

      /**
       * Save in this state the specified state as the one that was
       * the current exitable state at runtime state-saving time.
       */
      public final State setCurrentExitableState (State exitable)
        {
          _currentExitableState = exitable;
          return this;
        }

      /**
       * Sets the last exitable state saved before this one to be
       * the specified one, and returns this.
       */
      public final State setEnclosingExitableState (State exitable)
        {
          _enclosingExitableState = exitable;
          return this;
        }

      State (Instruction[] code, int ip,
             int intStackPoint, int realStackPoint, int objectStackPoint,
             int intEnvPoint, int realEnvPoint, int objectEnvPoint)
        {
          super(code,ip);
          _intStackPoint = intStackPoint;
          _realStackPoint = realStackPoint;
          _objectStackPoint = objectStackPoint;
          _intEnvPoint = intEnvPoint;
          _realEnvPoint = realEnvPoint;
          _objectEnvPoint = objectEnvPoint;
        }

      final int intStackPoint ()
        {
          return _intStackPoint;
        }

      final int realStackPoint ()
        {
          return _realStackPoint;
        }

      final int objectStackPoint ()
        {
          return _objectStackPoint;
        }

      final int intEnvPoint ()
        {
          return _intEnvPoint;
        }

      final int realEnvPoint ()
        {
          return _realEnvPoint;
        }

      final int objectEnvPoint ()
        {
          return _objectEnvPoint;
        }

      public final boolean equals (Object other)
        {
          if (this == other)
            return true;

          if (!(other instanceof State))
            return false;

          return this._code        == ((State)other)._code
              && this._ip          == ((State)other)._ip
              && _intStackPoint    == ((State)other)._intStackPoint
              && _realStackPoint   == ((State)other)._realStackPoint
              && _objectStackPoint == ((State)other)._objectStackPoint
              && _intEnvPoint      == ((State)other)._intEnvPoint
              && _realEnvPoint     == ((State)other)._realEnvPoint
              && _objectEnvPoint   == ((State)other)._objectEnvPoint
              && _isExitable       == ((State)other)._isExitable
              && _currentExitableState    == ((State)other)._currentExitableState
              && _enclosingExitableState  == ((State)other)._enclosingExitableState;
        }

      public final int hashCode ()
        {
          return this._code.hashCode() + this._ip
               + _intStackPoint
               + _realStackPoint
               + _objectStackPoint
               + _intEnvPoint
               + _realEnvPoint
               + _objectEnvPoint;
        }

      public final String toString ()
        {
          return super.toString() +
                 "\tSP = <" + _intStackPoint + "," +
                              _realStackPoint + "," +
                              _objectStackPoint + ">" +
                 "\tEP = <" + _intEnvPoint + "," +
                              _realEnvPoint + "," +
                              _objectEnvPoint + ">" +
                 "\t" + (_isExitable ? "" : "NON-") + "EXITABLE";
        }
    }

}
