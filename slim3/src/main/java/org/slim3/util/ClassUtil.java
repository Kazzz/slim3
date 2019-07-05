/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.util;

/**
 * A utility class for {@link Class}.
 * 
 * @author higa
 * @since 1.0.0
 */
public final class ClassUtil {

    /**
     * Returns the class object specified by the name.
     * 
     * @param <T>
     *            the type
     * @param className
     *            the class name
     * @return the class object
     * @throws NullPointerException
     *             if the className parameter is null
     * @throws WrapRuntimeException
     *             if the class specified by the name is not found
     */
    public static <T> Class<T> forName(String className)
            throws NullPointerException, WrapRuntimeException {
        return forName(className, Thread
            .currentThread()
            .getContextClassLoader());
    }

    /**
     * Returns the class object specified by the name.
     * 
     * @param <T>
     *            the type
     * @param className
     *            the class name
     * @param loader
     *            the class loader
     * @return the class object
     * @throws NullPointerException
     *             if the className parameter is null or if the loader parameter
     *             is null
     * @throws WrapRuntimeException
     *             if the class specified by the name is not found
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(String className, ClassLoader loader)
            throws NullPointerException, WrapRuntimeException {
        if (className == null) {
            throw new NullPointerException("The className parameter is null.");
        }
        if (loader == null) {
            throw new NullPointerException("The loader parameter is null.");
        }
        try {
            return (Class<T>) Class.forName(className, true, loader);
        } catch (ClassNotFoundException e) {
            throw new WrapRuntimeException("The class("
                + className
                + ") is not found.", e);
        }
    }

    /**
     * Creates a new instance.
     * 
     * @param <T>
     *            the target type
     * @param clazz
     *            the class
     * @return a new instance
     * @throws WrapRuntimeException
     *             if an error occurred while creating a new instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Throwable cause) {
            throw new WrapRuntimeException(
                "An error occurred while creating a new instance of the class("
                    + clazz.getName()
                    + "). Error message: "
                    + cause.getMessage(),
                cause);
        }
    }

    /**
     * Creates a new instance.
     * 
     * @param <T>
     *            the target type
     * @param className
     *            the class name
     * @return a new instance
     * @throws WrapRuntimeException
     *             if an error occurred while creating a new instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className)
            throws WrapRuntimeException {
        return (T) newInstance(className, Thread
            .currentThread()
            .getContextClassLoader());
    }

    /**
     * Creates a new instance.
     * 
     * @param <T>
     *            the target type
     * @param className
     *            the class name
     * @param loader
     *            the class loader
     * @return a new instance
     * @throws WrapRuntimeException
     *             if an error occurred while creating a new instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className, ClassLoader loader)
            throws WrapRuntimeException {
        Class<?> clazz = forName(className, loader);
        return (T) newInstance(clazz);
    }
    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you clearly prefer a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     * @return the default ClassLoader (only {@code null} if even the system
     * ClassLoader isn't accessible)
     * @see Thread#getContextClassLoader()
     * @see ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtil.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

    private ClassUtil() {
    }
}
