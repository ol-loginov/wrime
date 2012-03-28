package wrime.bytecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrime.util.EscapeUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Use with ClassFinder to filter the Urls to be scanned, example:
 * <pre>
 * UrlSet urlSet = new UrlSet(classLoader);
 * urlSet = urlSet.exclude(ClassLoader.getSystemClassLoader().getParent());
 * urlSet = urlSet.excludeJavaExtDirs();
 * urlSet = urlSet.excludeJavaEndorsedDirs();
 * urlSet = urlSet.excludeJavaHome();
 * urlSet = urlSet.excludePaths(System.getProperty("sun.boot.class.path", ""));
 * urlSet = urlSet.exclude(".*?/JavaVM.framework/.*");
 * urlSet = urlSet.exclude(".*?/activemq-(core|ra)-[\\d.]+.jar(!/)?");
 * </pre>
 *
 * @author David Blevins
 * @version $Rev: 894090 $ $Date: 2009-12-27 19:18:29 +0100 (Sun, 27 Dec 2009) $
 */
public class UrlSet {
    private static final Logger LOG = LoggerFactory.getLogger(UrlSet.class);
    private final Map<String, URL> urls;
    private Set<String> protocols;


    public UrlSet(ClassLoaderInterface classLoader) throws IOException {
        this(getUrls(classLoader));
    }

    public UrlSet(ClassLoaderInterface classLoader, Set<String> protocols) throws IOException {
        this(getUrls(classLoader, protocols));
        this.protocols = protocols;
    }

    public UrlSet(URL... urls) {
        this(Arrays.asList(urls));
    }

    /**
     * Ignores all URLs that are not "jar" or "file"
     *
     * @param urls
     */
    public UrlSet(Collection<URL> urls) {
        this.urls = new HashMap<String, URL>();
        for (URL location : urls) {
            try {
                this.urls.put(location.toExternalForm(), location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private UrlSet(Map<String, URL> urls) {
        this.urls = urls;
    }

    public UrlSet include(UrlSet urlSet) {
        Map<String, URL> urls = new HashMap<String, URL>(this.urls);
        urls.putAll(urlSet.urls);
        return new UrlSet(urls);
    }

    public UrlSet exclude(UrlSet urlSet) {
        Map<String, URL> urls = new HashMap<String, URL>(this.urls);
        Map<String, URL> parentUrls = urlSet.urls;
        for (String url : parentUrls.keySet()) {
            urls.remove(url);
        }
        return new UrlSet(urls);
    }

    public UrlSet exclude(ClassLoaderInterface parent) throws IOException {
        return exclude(new UrlSet(parent, this.protocols));
    }

    public UrlSet exclude(File file) throws MalformedURLException {
        return exclude(relative(file));
    }

    public UrlSet exclude(String pattern) throws MalformedURLException {
        return exclude(matching(pattern));
    }

    /**
     * Calls excludePaths(System.getProperty("java.ext.dirs"))
     *
     * @return
     * @throws MalformedURLException
     */
    public UrlSet excludeJavaExtDirs() throws MalformedURLException {
        return excludePaths(System.getProperty("java.ext.dirs", ""));
    }

    /**
     * Calls excludePaths(System.getProperty("java.endorsed.dirs"))
     *
     * @return
     * @throws MalformedURLException
     */
    public UrlSet excludeJavaEndorsedDirs() throws MalformedURLException {
        return excludePaths(System.getProperty("java.endorsed.dirs", ""));
    }

    public UrlSet excludeJavaHome() throws MalformedURLException {
        String path = System.getProperty("java.home");
        if (path != null) {

            File java = new File(path);

            if (path.matches("/System/Library/Frameworks/JavaVM.framework/Versions/[^/]+/Home")) {
                java = java.getParentFile();
            }
            return exclude(java);
        } else {
            return this;
        }
    }

    public UrlSet excludePaths(String pathString) throws MalformedURLException {
        String[] paths = pathString.split(File.pathSeparator);
        UrlSet urlSet = this;
        for (String path : paths) {
            if (EscapeUtils.isNotEmpty(path)) {
                File file = new File(path);
                urlSet = urlSet.exclude(file);
            }
        }
        return urlSet;
    }

    public UrlSet matching(String pattern) {
        Map<String, URL> urls = new HashMap<String, URL>();
        for (Map.Entry<String, URL> entry : this.urls.entrySet()) {
            String url = entry.getKey();
            if (url.matches(pattern)) {
                urls.put(url, entry.getValue());
            }
        }
        return new UrlSet(urls);
    }

    /**
     * Try to find a classes directory inside a war file add its normalized url to this set
     */
    public UrlSet includeClassesUrl(ClassLoaderInterface classLoaderInterface) throws IOException {
        Enumeration<URL> rootUrlEnumeration = classLoaderInterface.getResources("");
        while (rootUrlEnumeration.hasMoreElements()) {
            URL url = rootUrlEnumeration.nextElement();
            String externalForm = EscapeUtils.removeEnd(url.toExternalForm(), "/");
            if (externalForm.endsWith(".war/WEB-INF/classes")) {
                //if it is inside a war file, get the url to the file
                externalForm = EscapeUtils.substringBefore(externalForm, "/WEB-INF/classes");
                URL warUrl = new URL(externalForm);
                URL normalizedUrl = URLUtil.normalizeToFileProtocol(warUrl);
                URL finalUrl = (URL) EscapeUtils.defaultIfNull(normalizedUrl, warUrl);

                Map<String, URL> newUrls = new HashMap<String, URL>(this.urls);
                newUrls.put(finalUrl.toExternalForm(), finalUrl);
                return new UrlSet(newUrls);
            }
        }

        return this;
    }

    public UrlSet relative(File file) throws MalformedURLException {
        String urlPath = file.toURI().toURL().toExternalForm();
        Map<String, URL> urls = new HashMap<String, URL>();
        for (Map.Entry<String, URL> entry : this.urls.entrySet()) {
            String url = entry.getKey();
            if (url.startsWith(urlPath) || url.startsWith("jar:" + urlPath)) {
                urls.put(url, entry.getValue());
            }
        }
        return new UrlSet(urls);
    }

    public List<URL> getUrls() {
        return new ArrayList<URL>(urls.values());
    }

    private static List<URL> getUrls(ClassLoaderInterface classLoader) throws IOException {
        List<URL> list = new ArrayList<URL>();

        //find jars
        ArrayList<URL> urls = Collections.list(classLoader.getResources("META-INF"));

        for (URL url : urls) {
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                String externalForm = url.toExternalForm();
                //build a URL pointing to the jar, instead of the META-INF dir
                url = new URL(EscapeUtils.substringBefore(externalForm, "META-INF"));
                list.add(url);
            } else if (LOG.isDebugEnabled())
                LOG.debug("Ignoring URL [#0] because it is not a jar", url.toExternalForm());

        }

        //usually the "classes" dir
        list.addAll(Collections.list(classLoader.getResources("")));
        return list;
    }

    private static List<URL> getUrls(ClassLoaderInterface classLoader, Set<String> protocols) throws IOException {

        if (protocols == null) {
            return getUrls(classLoader);
        }

        List<URL> list = new ArrayList<URL>();

        //find jars
        ArrayList<URL> urls = Collections.list(classLoader.getResources("META-INF"));

        for (URL url : urls) {
            if (protocols.contains(url.getProtocol())) {
                String externalForm = url.toExternalForm();
                //build a URL pointing to the jar, instead of the META-INF dir
                url = new URL(EscapeUtils.substringBefore(externalForm, "META-INF"));
                list.add(url);
            } else if (LOG.isDebugEnabled())
                LOG.debug("Ignoring URL [#0] because it is not a valid protocol", url.toExternalForm());

        }

        //usually the "classes" dir
        list.addAll(Collections.list(classLoader.getResources("")));
        return list;
    }

}
