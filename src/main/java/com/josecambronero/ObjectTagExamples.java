package com.josecambronero;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.columbia.cs.psl.phosphor.runtime.Taint;
import edu.columbia.cs.psl.phosphor.struct.LinkedList;
import edu.columbia.cs.psl.phosphor.struct.LinkedList.Node;


public class ObjectTagExamples {

    public static Object identity(Object o) {
        return o;
    }

    public static void testExample1() {
        // Creating object tags for primitives
        // Source
        int x;
        x = MultiTainter.taintedInt(100, "source 1");
        // Source
        int y;
        y = MultiTainter.taintedInt(100, new Taint("source 2"));
        // Sink
        int z = x + y;
        Taint tz = MultiTainter.getTaint(z);
        assert (tz != null);
        System.out.println(tz.toString());
    }

    public static void testExample2() {
        // Create object tags for objects
        Object s = "My string";
        MultiTainter.taintedObject(s, new Taint("source x"));
        Object s2 = identity(s);
        Taint tag = MultiTainter.getTaint(s2);
        assert (tag != null);
        System.out.println((String) tag.getLabel());
        System.out.println(tag.toString());
    }

    public static void testExample3() {
        // Creating more than 32 distinct tags (the advantage of object tags)
        // Array of 33 different sources
        double[] mydata = new double[33];
        for(int i = 0; i < mydata.length; i++) {
            mydata[i] = MultiTainter.taintedDouble((float) i, "Source " + i);
        }
        // Sink
        double result = 0.0;
        for (double elem : mydata) {
            result += elem;
        }
        Taint tag = MultiTainter.getTaint(result);
        assert (tag != null);
        System.out.println(tag.toString());
    }
/*
    public static void testExample4() {
        // Iterating over dependencies in an object taint tag
        int x = MultiTainter.taintedInt(100, "x");
        int y = MultiTainter.taintedInt(100, "y");
        int z = x + y;
        double w = MultiTainter.taintedDouble(100.0, "w");
        int n = 0;

        int result = z + (int) w + n;
        Taint tag = MultiTainter.getTaint(result);
        assert (tag != null);
        LinkedList<Object> dependencies = tag.getDependencies();
        System.out.println(dependencies.toString());

        // iterate over the linked list
        Node currNode = (Node) dependencies.getFirst();
        while (currNode != null) {
            String currDepName = (String) currNode.entry;
            System.out.println(currDepName);
            currNode = currNode.next;
        }
    }
 */

    public static void testExample5() {
        // Creating object tags for primitives
        // Source
        int x;
        x = MultiTainter.taintedInt(100, "source 1");
        // Source
        int y;
        y = MultiTainter.taintedInt(100, new Taint("source 2"));
        // Sink
        int z = x + y;
        // cast int to string
        String zs = Integer.toString(z);
        Taint tzs = MultiTainter.getTaint(zs);
        Taint tz = MultiTainter.getTaint(z);
        assert (tz != null);
        // tzs is not null but as seen below the deps are not right
        assert (tzs != null);
        System.out.println(tz.toString());
        // dependencies here are not right
        System.out.println(tzs.toString());
    }

    // Sourced from the original Phosphor code
    public static void main(String[] args) {
        for (Method m : ObjectTagExamples.class.getDeclaredMethods()) {
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
