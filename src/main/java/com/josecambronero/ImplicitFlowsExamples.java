package com.josecambronero;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.columbia.cs.psl.phosphor.runtime.Taint;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ImplicitFlowsExamples {

    public static void testExample1() {
        // Source
        boolean x;
        x = MultiTainter.taintedBoolean(false, "source1");
        boolean y;
        y = !x;
        Taint t = MultiTainter.getTaint(y);
        // we can now track unary negation, by using control flow tracking
        assert(t != null);
    }

    public static void testExample2() {
        // Source
        double x = MultiTainter.taintedDouble(100.0, "x");
        double y = 0.0;

        // information flows from x to y through control flow
        if (x >= 90.0) {
            y = 20.0;
        } else {
            y = 30.0;
        }

        Taint t = MultiTainter.getTaint(y);
        assert(t != null);
    }

    public static void testExample3() {
        // source
        boolean x = MultiTainter.taintedBoolean(true, "x");
        // sink
        double y = (x) ? 1.0 : 2.0;
        Taint t  = MultiTainter.getTaint(y);
        assert (t != null);
    }

    public static void testExample4() {
        // source
        int x = MultiTainter.taintedInt(100, "x");
        int y = 0;

        // loop termination condition allows information to flow
        for(int i = 0; i < x; i++) {
            y += i;
        }

        Taint t = MultiTainter.getTaint(y);
        assert (t != null);
    }

    // Sourced from the original Phosphor code
    public static void main(String[] args) {
        for (Method m : ImplicitFlowsExamples.class.getDeclaredMethods()) {
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

