package com.josecambronero;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple set of examples to show how to use
 * integer tags with Phosphor. Meant to be accompanied by the document
 * at ....
 * TODO: add location of document
 */
public class IntegerTagExamples
{

    public static int identity(int x) {
        return x;
    }

    public static int zero(int x) {
        return 0;
    }

    public static List<Integer> getTaintSources(int tag) {
        char[] binaryRep = Integer.toBinaryString(tag).toCharArray();
        List<Integer> sources = new ArrayList<Integer>();
        for (int i = 0; i < binaryRep.length; i++) {
            if (binaryRep[i] == '1') {
                sources.add((int) Math.pow(2, 1 + i));
            }
        }
        return sources;
    }

    public static <E> void printList(List<E> l) {
        for (E elem : l) {
            System.out.println(elem + ",");
        }
    }

    public static void testExample1() {
        // A simple binary operation
        // source
        int x = 0;
        // sink
        int y = 0;
        // primitive and taint tag
        x = Tainter.taintedInt(x, 1);
        y = x * 3;
        // check sink for taint
        assert (Tainter.getTaint(y) != 0);
    }

    public static void testExample2() {
        // Nullifying taint
        // source
        int x = 0;
        // sink
        int y = 0;
        x = Tainter.taintedInt(x, 1);
        // tainted
        y = x * 3;
        assert (Tainter.getTaint(y) != 0);
        // not tainted, as zero returns constant (not function of x)
        y = zero(x);
        assert (Tainter.getTaint(y) == 0);
    }

    public static void testExample3() {
        // Taint and arithmetic identities
        // source
        int x = 1;
        x = Tainter.taintedInt(x, 1);
        // sink
        int y = 0;
        y = 2 * x - (x + x);
        assert (Tainter.getTaint(y) != 0);
    }

    public static void testExample4() {
        // Tracking sources of taint
        // sources
        int x = 0, w = 0, r = 0;
        x = Tainter.taintedInt(x, 2);
        w = Tainter.taintedInt(x, 16);
        r = Tainter.taintedInt(x, 4);
        // sinks
        int y = 0, z = 0;
        y = x * 3;
        z = x + w;

        assert (Tainter.getTaint(y) == 2);
        // get list of sources (recall taint tags are OR'ed)
        List<Integer> zSources = getTaintSources(Tainter.getTaint(z));
        assert (zSources.contains(Tainter.getTaint(x)));
        assert (zSources.contains(Tainter.getTaint(w)));
        assert (!zSources.contains(Tainter.getTaint(r)));

    }

    public static void testExample5() {
        // Element-level taint tags in array
        // source
        int x = 0;
        x = Tainter.taintedInt(x, 1);
        // (potential) sinks
        int[] y = new int[4];
        y[0] = x * 2;
        assert (Tainter.getTaint(y[0]) != 0);
        assert (Tainter.getTaint(y[1]) == 0);
    }

    public static void testExample6() {
        // Boolean types
        // source
        boolean x;
        x = Tainter.taintedBoolean(false, 2);
        boolean y;
        y = x & false;
        //System.out.println(Tainter.getTaint(y));
        // Note that ! becomes jump (as per Jon's explanation) so
        // won't track correctly without enabling implicit flow tracking
        y = !x;
        //System.out.println(Tainter.getTaint(y));
        y = !(x & true);
        //System.out.println(Tainter.getTaint(y));
        //assert(Tainter.getTaint(y) != 0);
    }

    public static void testExample7() {
        // A simple binary operation
        // source
        int x = 0;
        // sink
        int y = 0;
        // primitive and taint tag
        x = Tainter.taintedInt(x, 1);
        y = x * 3;
        String ys = Integer.toString(y);
        // check sink for taint
        assert (Tainter.getTaint(y) != 0);
        // this fails for some reason
        assert (Tainter.getTaint(ys) != 0);
    }


    // Sourced from the original Phosphor code
    public static void main(String[] args) {
        for (Method m : IntegerTagExamples.class.getDeclaredMethods()) {
            if (m.getName().startsWith("test")) {
                System.out.println(m.getName());
                try {
                    m.invoke(null);
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
