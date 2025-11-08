package com.caio.engine;

import java.util.Map;

public class AllClassesClassLoader extends ClassLoader {

    private final Map<String, byte[]> classes;

    public AllClassesClassLoader(Map<String, byte[]> classes, ClassLoader parent) {
        super(parent);
        this.classes = classes;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {

            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null)
                return loadedClass;

            if (classes.containsKey(name)) {
                byte[] bytes = classes.get(name);
                Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
                if (resolve)
                    resolveClass(clazz);
                return clazz;
            }

            return super.loadClass(name, resolve);
        }
    }

}
