package com.caio.engine;

import java.util.Map;

public class NonMutantClassLoader extends ClassLoader {

    private final Map<String, byte[]> classes;

    public NonMutantClassLoader(Map<String, byte[]> classes, ClassLoader parent) {
        super(parent);
        this.classes = classes;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {

            // Já carregada?
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null) return loadedClass;

            // Se for uma classe "sua", define aqui
            if (classes.containsKey(name)) {
                byte[] bytes = classes.get(name);
                Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
                if (resolve) resolveClass(clazz);
                return clazz;
            }

            // Senão, deixa o parent resolver (Spring, JUnit, etc.)
            return super.loadClass(name, resolve);
        }
    }

    
}

