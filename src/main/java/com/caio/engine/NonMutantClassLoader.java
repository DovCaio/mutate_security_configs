package com.caio.engine;

import java.util.Map;

public class NonMutantClassLoader extends ClassLoader {

    private final Map<String, byte[]> classes;

    public NonMutantClassLoader(Map<String, byte[]> classes) {
        super(ClassLoader.getSystemClassLoader());
        this.classes = classes;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (classes.containsKey(name)) {
            Class<?> clazz = defineClass(name, classes.get(name), 0, classes.get(name).length);
            if (resolve) resolveClass(clazz);
            return clazz;
        }
        return super.loadClass(name, resolve);
    }
}

