package com.caio.engine;

public class MutantClassLoader extends ClassLoader{

    private final String className;
    private final byte[] classBytes;

    public MutantClassLoader(String className, byte[] classBytes) {
        super(MutantClassLoader.class.getClassLoader());
        this.className = className;
        this.classBytes = classBytes;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Se for a classe mutada, define a partir dos bytes mutados
        if (name.equals(className.replace('/', '.'))) {
            Class<?> clazz = defineClass(name, classBytes, 0, classBytes.length);
            if (resolve) resolveClass(clazz);
            return clazz;
        }

        // Caso contrário, delega para o classloader padrão
        return super.loadClass(name, resolve);
    }

}
