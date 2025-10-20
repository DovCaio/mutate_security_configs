package com.caio.engine;

public class MutantClassLoader extends ClassLoader{

    private final String className;
    private final byte[] classBytes;

    public MutantClassLoader(String className, byte[] classBytes) {
        this.className = className;
        this.classBytes = classBytes;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.equals(className.replace('/', '.'))) {
            return defineClass(name.replace('/', '.'), classBytes, 0, classBytes.length);
        }
        return super.findClass(name);
    }

}
