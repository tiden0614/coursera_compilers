import java.io.PrintStream;
import java.util.*;

/**
 * This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.
 */
class ClassTable {
    private PrintStream errorStream = System.err;
    private int semantErrors;
    private class_c baseClass;
    private Map<AbstractSymbol, class_c> classMap = new Hashtable<AbstractSymbol, class_c>();
    private Map<AbstractSymbol, Map<AbstractSymbol, method>> methodEnvs;
    private Map<AbstractSymbol, Map<AbstractSymbol, AbstractSymbol>> objectEnvs;

    private class_c addBasicClass(
            int lineNumber,
            AbstractSymbol className,
            AbstractSymbol parentName,
            Features features,
            AbstractSymbol fileName) {
        class_c c = new class_c(lineNumber, className, parentName, features, fileName);
        loadClass(c);
        return c;
    }

    private void loadClass(class_c clazz) {
        AbstractSymbol className = clazz.name;
        if (className == TreeConstants.SELF_TYPE) {
            PrintStream p = semantError(clazz);
            p.println("Cannot declare SELF_TYPE as a class");
            return;
        }
        classMap.put(className, clazz);
        Features features = clazz.features;
        Map<AbstractSymbol, AbstractSymbol> objectEnv = new HashMap<AbstractSymbol, AbstractSymbol>();
        objectEnv.put(TreeConstants.self, TreeConstants.SELF_TYPE);
        Map<AbstractSymbol, method> methodEnv = new HashMap<AbstractSymbol, method>();
        for (Enumeration e = features.getElements(); e.hasMoreElements(); ) {
            Feature f = (Feature) e.nextElement();
            if (f instanceof attr) {
                attr a = (attr) f;
                objectEnv.put(a.name, a.type_decl);
            } else if (f instanceof method) {
                method m = (method) f;
                methodEnv.put(m.name, m);
            }
        }
        objectEnvs.put(className, objectEnv);
        methodEnvs.put(className, methodEnv);
    }

    private AbstractSymbol getParentClassName(AbstractSymbol c) {
        return classMap.get(c).name;
    }

    private class_c getParentClass(class_c c) {
        return classMap.get(c.parent);
    }

    private boolean checkLoop(class_c entry) {
        if (entry == null) {
            // class undefined
            errorStream.println("Semant error: null pointer");
            return false;
        }
        class_c p = entry, q = entry;
        int i = 0;
        for (; p != baseClass; i++) {
            p = getParentClass(p);
            if (p == null) {
                // parent class undefined
                semantError(entry, entry, "Class " + entry.name.getString() + " undefined");
                return false;
            }
            if (i % 2 == 1) {
                q = getParentClass(q);
            }
            if (p == q) {
                // loop exists
                semantError(entry, entry, "Inheritance loop exists");
                return false;
            }
        }
        return true;
    }

    /**
     * Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     */
    private void installBasicClasses() {
        AbstractSymbol filename
                = TreeConstants.base_class_filename;

        // The following demonstrates how to create dummy parse trees to
        // refer to basic Cool classes.  There's no need for method
        // bodies -- these are already built into the runtime system.

        // IMPORTANT: The results of the following expressions are
        // stored in local variables.  You will want to do something
        // with those variables at the end of this method to make this
        // code meaningful.

        // The Object class has no parent class. Its methods are
        //        cool_abort() : Object    aborts the program
        //        type_name() : Str        returns a string representation
        //                                 of class name
        //        copy() : SELF_TYPE       returns a copy of the object

        class_c Object_class =
                addBasicClass(0,
                        TreeConstants.Object_,
                        TreeConstants.No_class,
                        new Features(0)
                                .appendElement(new method(0,
                                        TreeConstants.cool_abort,
                                        new Formals(0),
                                        TreeConstants.Object_,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.type_name,
                                        new Formals(0),
                                        TreeConstants.Str,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.copy,
                                        new Formals(0),
                                        TreeConstants.SELF_TYPE,
                                        new no_expr(0))),
                        filename);
        baseClass = Object_class;

        // The IO class inherits from Object. Its methods are
        //        out_string(Str) : SELF_TYPE  writes a string to the output
        //        out_int(Int) : SELF_TYPE      "    an int    "  "     "
        //        in_string() : Str            reads a string from the input
        //        in_int() : Int                "   an int     "  "     "

        class_c IO_class =
                addBasicClass(0,
                        TreeConstants.IO,
                        TreeConstants.Object_,
                        new Features(0)
                                .appendElement(new method(0,
                                        TreeConstants.out_string,
                                        new Formals(0)
                                                .appendElement(new formalc(0,
                                                        TreeConstants.arg,
                                                        TreeConstants.Str)),
                                        TreeConstants.SELF_TYPE,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.out_int,
                                        new Formals(0)
                                                .appendElement(new formalc(0,
                                                        TreeConstants.arg,
                                                        TreeConstants.Int)),
                                        TreeConstants.SELF_TYPE,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.in_string,
                                        new Formals(0),
                                        TreeConstants.Str,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.in_int,
                                        new Formals(0),
                                        TreeConstants.Int,
                                        new no_expr(0))),
                        filename);

        // The Int class has no methods and only a single attribute, the
        // "val" for the integer.

        class_c Int_class =
                addBasicClass(0,
                        TreeConstants.Int,
                        TreeConstants.Object_,
                        new Features(0)
                                .appendElement(new attr(0,
                                        TreeConstants.val,
                                        TreeConstants.prim_slot,
                                        new no_expr(0))),
                        filename);

        // Bool also has only the "val" slot.
        class_c Bool_class =
                addBasicClass(0,
                        TreeConstants.Bool,
                        TreeConstants.Object_,
                        new Features(0)
                                .appendElement(new attr(0,
                                        TreeConstants.val,
                                        TreeConstants.prim_slot,
                                        new no_expr(0))),
                        filename);

        // The class Str has a number of slots and operations:
        //       val                              the length of the string
        //       str_field                        the string itself
        //       length() : Int                   returns length of the string
        //       concat(arg: Str) : Str           performs string concatenation
        //       substr(arg: Int, arg2: Int): Str substring selection

        class_c Str_class =
                addBasicClass(0,
                        TreeConstants.Str,
                        TreeConstants.Object_,
                        new Features(0)
                                .appendElement(new attr(0,
                                        TreeConstants.val,
                                        TreeConstants.Int,
                                        new no_expr(0)))
                                .appendElement(new attr(0,
                                        TreeConstants.str_field,
                                        TreeConstants.prim_slot,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.length,
                                        new Formals(0),
                                        TreeConstants.Int,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.concat,
                                        new Formals(0)
                                                .appendElement(new formalc(0,
                                                        TreeConstants.arg,
                                                        TreeConstants.Str)),
                                        TreeConstants.Str,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.substr,
                                        new Formals(0)
                                                .appendElement(new formalc(0,
                                                        TreeConstants.arg,
                                                        TreeConstants.Int))
                                                .appendElement(new formalc(0,
                                                        TreeConstants.arg2,
                                                        TreeConstants.Int)),
                                        TreeConstants.Str,
                                        new no_expr(0))),
                        filename);

	/* Do something with Object_class, IO_class, Int_class,
           Bool_class, and Str_class here */

    }

    public method getMethod(AbstractSymbol className, AbstractSymbol methodName) {
        method m = null;
        AbstractSymbol temp = className;
        while (m == null) {
            m = methodEnvs.get(temp).get(methodName);
            if (temp == TreeConstants.Object_) {
                PrintStream p = semantError(classMap.get(className));
                p.println("Class " + className + " does not have a method " + methodName);
                return null;
            }
            temp = getParentClassName(temp);
        }
        return m;
    }

    public Map<AbstractSymbol, AbstractSymbol> getObjectEnv(AbstractSymbol className) {
        return objectEnvs.get(className);
    }

    public boolean isSubclass(AbstractSymbol c1, AbstractSymbol c2) {
        if (c1 == c2) {
            return true;
        }
        if (c1 == TreeConstants.Object_) {
            return c2 == TreeConstants.Object_;
        }
        while (c1 != TreeConstants.Object_) {
            class_c c1c = classMap.get(c1);
            if (c1c == null) {
                return false;
            }
            if (c1c.parent == c2) {
                return true;
            }
            c1 = c1c.parent;
        }
        return false;
    }

    public AbstractSymbol findCommonAncestor(AbstractSymbol c1, AbstractSymbol c2) {
        if (c1 == TreeConstants.Object_ || c2 == TreeConstants.Object_
                || c1 == TreeConstants.No_type || c2 == TreeConstants.No_type) {
            return TreeConstants.Object_;
        }
        int len1 = getDistanceToRoot(c1);
        int len2 = getDistanceToRoot(c2);
        // put the longer list in front of the shorter one
        if (len1 < len2) {
            int t = len1;
            len1 = len2;
            len2 = t;
            AbstractSymbol c3 = c1;
            c1 = c2;
            c2 = c3;
        }
        // cut the longer list to the same length as the shorter one
        while (len1 > len2 && len1 > 0) {
            c1 = getParentClassName(c1);
            len1--;
        }
        if (c1 == TreeConstants.Object_) {
            return c1;
        }
        // find the nearest common ancestor
        // they will finally reach Object_
        while (c1 != c2) {
            c1 = getParentClassName(c1);
            c2 = getParentClassName(c2);
        }
        return c1;
    }

    public AbstractSymbol findCommonAncestor(List<AbstractSymbol> list) {
        if (list.size() == 0) {
            PrintStream p = semantError();
            p.print("Error: case expression with no branch");
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            AbstractSymbol last = list.get(0);
            for (AbstractSymbol next : list) {
                last = findCommonAncestor(last, next);
            }
            return last;
        }
    }

    private int getDistanceToRoot (AbstractSymbol c1) {
        int distance = 1;
        while (c1 != TreeConstants.Object_) {
            c1 = getParentClassName(c1);
            distance++;
        }
        return distance;
    }

    public ClassTable(Classes cls) {
        semantErrors = 0;
		objectEnvs = new HashMap<AbstractSymbol, Map<AbstractSymbol, AbstractSymbol>>();
		methodEnvs = new HashMap<AbstractSymbol, Map<AbstractSymbol, method>>();

	    /* fill this in */
        installBasicClasses();
        for(Enumeration e = cls.getElements(); e.hasMoreElements(); ) {
            class_c c = (class_c) e.nextElement();
            if (classMap.containsKey(c.name)) {
                PrintStream p = semantError(c);
                p.println("Class " + c.name.getString() + " is already defined");
            } else {
                loadClass(c);
            }
        }
        for (class_c cc : classMap.values()) {
            checkLoop(cc);
        }
    }

    /**
     * Prints line number and file name of the given class.
     * <p/>
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     */
    public PrintStream semantError(class_c c) {
        return semantError(c.getFilename(), c);
    }

    /**
     * Prints the file name and the line number of the given tree node.
     * <p/>
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t        the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     */
    public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
        errorStream.print(filename + ":" + t.getLineNumber() + ": ");
        return semantError();
    }

    /**
     * Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     */
    public PrintStream semantError() {
        semantErrors++;
        return errorStream;
    }

    /**
     * Returns true if there are any semantic errors.
     */
    public boolean errors() {
        return semantErrors != 0;
    }

    /**
     * Prints the file name and the line number of the given tree node
     * as well as the message.
     * @param c        the class containing the error
     * @param t        the tree node
     * @param msg      the message
     */
    public void semantError(class_c c, TreeNode t, String msg) {
        PrintStream stream = semantError(c.getFilename(), t);
        stream.print(msg + "\n");
    }
}
			  
    
