package com.josecambronero;

import java.lang.reflect.Method;

public class AutoExample {

    // source method
    public static int gimmeTainted(int i) {
        return i;
    }

    // sink method
    public static void printMyInt(int i) {
        System.out.println("Someone gave me a: " + i);
    }

    // no flow from source to sink
    public static void testExample1() {
        int one = 1;
        printMyInt(one);
    }

    // flow from source to sink
    public static void testExample2() {
        System.out.println("==> Expect exception");
        int tainted = gimmeTainted(2);
        printMyInt(tainted);
    }

    // Sourced from the original Phosphor code
    public static void main(String[] args) {
        for (Method m : AutoExample.class.getDeclaredMethods()) {
            if (m.getName().startsWith("test")) {
                System.out.println(m.getName());
                try {
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

