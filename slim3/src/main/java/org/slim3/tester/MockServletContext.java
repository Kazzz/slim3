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
package org.slim3.tester;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.slim3.util.ClassUtil;
import org.slim3.util.WrapRuntimeException;

/**
 * A mock implementation for {@link ServletContext}.
 * 
 * @author higa
 * @since 1.0.0
 */
public class MockServletContext implements ServletContext, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Major Version
     */
    public static final int MAJOR_VERSION = 3;

    /**
     * Minor Version
     */
    public static final int MINOR_VERSION = 1;

    private static final Set<SessionTrackingMode> DEFAULT_SESSION_TRACKING_MODES = new LinkedHashSet<>(4);

    static {
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.COOKIE);
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.URL);
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.SSL);
    }

    private final Set<String> declaredRoles = new LinkedHashSet<>();

    /**
     * The servlet context name.
     */
    protected String servletContextName = "";

    /**
     * The context path.
     */
    protected String contextPath = "";

    /**
     * The server information.
     */
    protected String serverInfo = "Slim3";
    /**
     * The map for the initial parameters.
     */
    protected Map<String, String> initParameterMap =
        new HashMap<String, String>();

    /**
     * The map for the attributes.
     */
    protected Map<String, Object> attributeTable =
        new HashMap<String, Object>();

    /**
     * The map for resource paths.
     */
    protected Map<String, Set<String>> resourcePathsMap =
        new HashMap<String, Set<String>>();

    /**
     * The map for resource.
     */
    protected Map<String, URL> resourceMap = new HashMap<String, URL>();

    /**
     * The map for initial parameters.
     */
    protected Map<String, String> realPathMap = new HashMap<String, String>();

    /**
     * The latest request dispatcher.
     */
    protected MockRequestDispatcher latestRequestDispatcher;

    private Set<SessionTrackingMode> sessionTrackingModes;

    private final SessionCookieConfig sessionCookieConfig = new MockSessionCookieConfig();
    /**
     * Constructor.
     */
    public MockServletContext() {
    }

    public ServletContext getContext(String path) {
        throw new UnsupportedOperationException();
    }

    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    public String getMimeType(String file) {
        throw new UnsupportedOperationException();
    }

    public Set<String> getResourcePaths(String path) {
        Set<String> resourcePaths = resourcePathsMap.get(path);
        if (resourcePaths != null) {
            return resourcePaths;
        }
        return new HashSet<String>();
    }

    /**
     * Adds the resource paths.
     * 
     * @param path
     *            the path
     * @param resourcePaths
     *            the resource paths
     */
    public void addResourcePaths(String path, Set<String> resourcePaths) {
        resourcePathsMap.put(path, resourcePaths);
    }

    public URL getResource(String path) {
        return resourceMap.get(path);
    }

    /**
     * Adds the URL resource.
     * 
     * @param path
     *            the path
     * @param url
     *            the URL resource
     */
    public void addResource(String path, URL url) {
        resourceMap.put(path, url);
    }

    public InputStream getResourceAsStream(String path) {
        try {
            URL url = getResource(path);
            if (url == null) {
                return null;
            }
            return url.openStream();
        } catch (Exception e) {
            throw new WrapRuntimeException(e);
        }
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        latestRequestDispatcher = new MockRequestDispatcher(path);
        return latestRequestDispatcher;
    }

    /**
     * Returns the latest request dispatcher.
     * 
     * @return the latest request dispatcher
     */
    public MockRequestDispatcher getLatestRequestDispatcher() {
        return latestRequestDispatcher;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        throw new UnsupportedOperationException();
    }

    public Servlet getServlet(String name) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public Enumeration<Servlet> getServlets() {
        throw new UnsupportedOperationException();
    }

    public Enumeration<String> getServletNames() {
        throw new UnsupportedOperationException();
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void log(Exception ex, String message) {
        System.out.println(message);
        ex.printStackTrace();
    }

    public void log(String message, Throwable t) {
        System.out.println(message);
        t.printStackTrace();
    }

    public String getRealPath(String path) {
        return realPathMap.get(path);
    }

    /**
     * Adds the real path.
     * 
     * @param path
     *            the context relative path
     * @param realPath
     *            the real path
     */
    public void addRealPath(String path, String realPath) {
        realPathMap.put(path, realPath);
    }

    public String getServerInfo() {
        return serverInfo;
    }

    /**
     * Sets the server information.
     * 
     * @param serverInfo
     *            the server information
     */
    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public String getInitParameter(String name) {
        return initParameterMap.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameterMap.keySet());
    }

    /**
     * Sets the initial parameter.
     * 
     * @param name
     *            the parameter name
     * @param value
     *            the value
     */
    public boolean setInitParameter(String name, String value) {
        try {
            initParameterMap.put(name, value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Removes the initial parameter.
     * 
     * @param name
     *            the parameter name
     */
    public void removeInitParameter(String name) {
        initParameterMap.remove(name);
    }

    public Object getAttribute(String name) {
        return attributeTable.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributeTable.keySet());
    }

    public void setAttribute(String name, Object value) {
        attributeTable.put(name, value);
    }

    public void removeAttribute(String name) {
        attributeTable.remove(name);
    }

    public String getServletContextName() {
        return servletContextName;
    }

    /**
     * Sets the servlet context name.
     * 
     * @param servletContextName
     *            the servlet context name
     */
    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    public String getContextPath() {
        return contextPath;
    }

    /**
     * Sets the context path.
     * 
     * @param contextPath
     *            the context path
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     *  for 3.1
     *
     * @return
     */

    @Override
    public int getEffectiveMajorVersion() {
        return MAJOR_VERSION;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return MINOR_VERSION;
    }

    //---------------------------------------------------------------------
    // Unsupported Servlet 3.0 registration methods
    //---------------------------------------------------------------------
    @Override
    public ServletRegistration.Dynamic addServlet(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration getServletRegistration(String s) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return Collections.emptyMap();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration getFilterRegistration(String s) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return Collections.emptyMap();
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return this.getSessionCookieConfig();
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        this.sessionTrackingModes = sessionTrackingModes;
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return DEFAULT_SESSION_TRACKING_MODES;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return (this.sessionTrackingModes != null ?
                Collections.unmodifiableSet(this.sessionTrackingModes) : DEFAULT_SESSION_TRACKING_MODES);
    }

    @Override
    public void addListener(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(Class<? extends EventListener> aClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
        throw new UnsupportedOperationException();
    }
    //---------------------------------------------------------------------
    // Unsupported Servlet 3.0 registration methods
    //---------------------------------------------------------------------

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getClassLoader() {
        return ClassUtil.getDefaultClassLoader();
    }

    @Override
    public void declareRoles(String... roleNames) {
        //Assert.notNull(roleNames, "Role names array must not be null");
        for (String roleName : roleNames) {
            //Assert.hasLength(roleName, "Role name must not be empty");
            this.declaredRoles.add(roleName);
        }
    }

    @Override
    public String getVirtualServerName() {
        throw new UnsupportedOperationException();
    }
}
