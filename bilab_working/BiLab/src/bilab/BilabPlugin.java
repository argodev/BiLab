/**
* This document is a part of the source code and related artifacts for BiLab,
* an open source interactive workbench for computational biologists.
*
* http://computing.ornl.gov/
*
* Copyright © 2011 Oak Ridge National Laboratory
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package bilab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.python.util.PythonInterpreter;
import org.eclipse.jface.resource.ImageDescriptor;

import scigol.CombinedSharedInputState;
import scigol.Debug;
import scigol.LexerSharedInputStateWrapper;
import scigol.NamespaceScope;
import scigol.ParserSharedInputStateWrapper;
import scigol.ScigolLexer;
import scigol.ScigolParser;
import scigol.ScigolTreeParser;
import scigol.Scope;
import scigol.TypeSpec;
import antlr.RecognitionException;
import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.collections.AST;

/**
* The BilabPlugin class controls the application/plug-in life cycle
*/
public class BilabPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biLab"; //$NON-NLS-1$
    
    // registry of Viewer for specific value types
    final static class RegistryPair {
        public TypeSpec valueType;

        public TypeSpec viewerType;

        public RegistryPair(final TypeSpec valType, final TypeSpec viewType) {
            valueType = valType;
            viewerType = viewType;
        }
    }

    // registry of resource types
    final static class ResourceTypeInfo {

        public String type; // e.g. PDB

        public String description; // e.g. Protein Data Bank file
        public String[] extensions;// e.g. { "pdb" }

        public ResourceTypeInfo(final String resourceType, final String desc,
                final String[] exts) {
            type = resourceType;
            description = desc;
            extensions = exts;
        }
    }

    // The shared instance.
    private static BilabPlugin plugin;

    private static int uniqueIDCount = 0;

    static LinkedList<RegistryPair> viewerRegistry;

    static java.util.Map<String, ResourceTypeInfo> resTypeRegistry; // mapping

    // from
    // resource
    // type name
    // to info
    static java.util.Map<String, java.util.List<String>> resExtRegistry; // mapping
                                                                         // from
                                                                         // extension
                                                                         // to
                                                                         // types

    // registry of resource importers
    static java.util.Map<String, java.util.List<TypeSpec>> resImporterRegistry; // map
                                                                                // from
                                                                                // resource
                                                                                // type
                                                                                // name
                                                                                // to
                                                                                // list
                                                                                // of
                                                                                // types
                                                                                // that
                                                                                // implement
                                                                                // IResourceImporter

    static {
        viewerRegistry = new LinkedList<RegistryPair>();
        resTypeRegistry = new HashMap<String, ResourceTypeInfo>();
        resExtRegistry = new HashMap<String, java.util.List<String>>();
        resImporterRegistry = new java.util.HashMap<String, java.util.List<TypeSpec>>();
    }

    public static OutputStream createResourceStream(final String resourceName)
            throws IOException {
        // if resourceName looks like an absolute path, leave it alone.
        // Otherwise, prepend
        // the workspace area

        String pathName = resourceName;

        if (resourceName.startsWith("file:")
                || resourceName.startsWith("http:")) {
            final URL resURL = new URL(resourceName);
            if (!resURL.getProtocol().equals("file")) {
                throw new BilabException(
                        "Resources can only be created using local file: URLs or filesystem paths");
            }
            pathName = resURL.getFile();
        }

        if (!pathName.startsWith("/")) { // not absolute, so prepend workspace
                                         // root (or something!?)

            Debug.Unimplemented("relative paths for resource creation");
            // import org.eclipse.swt.layout.FillLayout;

            // TODO: should probably create stuff using the IFile eclipse APIs
            // for
            // resources within the workspace/project
            return null;
        } else {
            // possibly (probably?) outside the project/workspace, so use the
            // Java IO API for create the file
            final String nativePathName = Util.toNativePathSeparator(pathName);

            final File file = new File(nativePathName);
            final FileOutputStream fileOutputStream = new FileOutputStream(file);

            return fileOutputStream;
        }

    }

    public static void deleteResource(final String resourceName)
            throws IOException {
        // if resourceName looks like an absolute path, leave it alone.
        // Otherwise, prepend
        // the workspace area

        String pathName = resourceName;

        if (resourceName.startsWith("file:")
                || resourceName.startsWith("http:")) {
            final URL resURL = new URL(resourceName);
            if (!resURL.getProtocol().equals("file")) {
                throw new BilabException(
                        "Resources can only be deleted using local file: URLs or filesystem paths");
            }
            pathName = resURL.getFile();
        }

        if (!pathName.startsWith("/")) { // not absolute, so prepend workspace
                                         // root (or something!?)

            Debug.Unimplemented("relative paths for resource deletion");

            // should probably be using the IFile eclipse APIs for resources
            // within the workspace/project
        } else {
            // possibly (probably?) outside the project/workspace, so use the
            // Java IO API for create the file
            final String nativePathName = Util.toNativePathSeparator(pathName);

            final File file = new File(nativePathName);
            file.delete();
        }
    }

    public static Object executeScigolSourceStream(final Scope scope,
            final URL urlStream, final String name) throws IOException,
            RecognitionException, TokenStreamException {
        final CombinedSharedInputState istate = new CombinedSharedInputState();
        final LexerSharedInputStateWrapper listate = new LexerSharedInputStateWrapper(
                istate, urlStream.openStream());
        final ParserSharedInputStateWrapper pistate = new ParserSharedInputStateWrapper(
                istate);

        final ScigolLexer lexer = new ScigolLexer(listate);
        lexer.setTokenObjectClass("scigol.CommonTokenWithLocation");
        lexer.setFilename(name);

        final ScigolParser parser = new ScigolParser(pistate);
        parser.setTokenBuffer(new TokenBuffer(lexer));
        parser.setASTNodeClass("scigol.CommonASTWithLocation");
        parser.setFilename(name);

        // parse
        parser.program();

        final AST t = parser.getAST();
        // Debug.WL("AST:\n"+t.toStringTree());

        final ScigolTreeParser treeParser = new ScigolTreeParser(scope, false);

        treeParser.setASTNodeClass("scigol.CommonASTWithLocation");
        // treeParser.setASTFactory

        return treeParser.program(t);
    }

    public static boolean existsRegisteredViewer(final TypeSpec valueType) {
        // look for matching valueType
        for (final RegistryPair viewerPair : viewerRegistry) {
            if (valueType.isA(viewerPair.valueType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean existsResourceType(final String resourceTypeName) {
        return resTypeRegistry.containsKey(resourceTypeName);
    }

    public static boolean existsViewer(final TypeSpec valueType) {
        // look for matching valueType and instantiate the corresponding viewer
        // type by fetching the single arg
        // constructor via reflection
        for (final RegistryPair viewerPair : viewerRegistry) {
            if (valueType.isA(viewerPair.valueType)) {
                return true;
            }
        }
        return false;
    }

    // a resource name is either a relative path from and of the resource
    // directories, an absolute file-system path
    // or a URL (including file: URLs)
    public static URL findResource(final String resourceName)
            throws IOException {
        // if resource name looks like an absolute path or a URL, leave it as it
        if (resourceName.startsWith("/")) {
            return Platform.resolve(new URL("file:" + resourceName));
        }
        if (resourceName.startsWith("file:")
                || resourceName.startsWith("http:")) {
            return Platform.resolve(new URL(resourceName));
        }

        final Bundle bundle = BilabPlugin.getDefault().getBundle();
        Path path = new Path(resourceName);
        URL resURL = Platform.find(bundle, path);
        if (resURL == null) {
            path = new Path("resources/" + resourceName);
            resURL = Platform.find(bundle, path);
            if (resURL == null) {
                path = new Path("libs/" + resourceName);
                resURL = Platform.find(bundle, path);
                if (resURL == null) {
                    path = new Path("../" + resourceName);
                    resURL = Platform.find(bundle, path);
                    if (resURL == null) {
                        throw new IOException("not found");
                    }
                }
            }
        }
        return Platform.resolve(resURL);
    }

    public static InputStream findResourceStream(final String resourceName)
            throws IOException {
        // try simplest way first
        final InputStream is = BilabPlugin.class
                .getResourceAsStream(resourceName);

        if (is != null) {
            return is;
        }

        // failing that, do a more exhaustive search
        final URL url = findResource(resourceName);
        return url.openStream();
    }

    /**
* Returns the shared instance.
*
* @return the shared instance
*/
    public static BilabPlugin getDefault() {
        return plugin;
    }

    // returns the filesystem path of the Plugin root directory
    // (useful for accessing external distribution files e.g. .exe's etc.)
    public static String getPluginFilesystemRoot() throws IOException {
        // this is kinda kludgey - get a handle on the libs dir and then delete
        // libs/ of the end
        // off the path

        final Bundle bundle = BilabPlugin.getDefault().getBundle();
        final URL libsURL = Platform.find(bundle, new Path("libs"));
        final String libsPath = Platform.resolve(libsURL).getFile();

        final String rootPath = libsPath + "/../..";
        final File rootAbsPath = new File(rootPath);

        return rootAbsPath.getCanonicalPath();
    }

    public static java.util.List<TypeSpec> getResourceImportersForType(
            final String resourceTypeName) {
        if (resImporterRegistry.containsKey(resourceTypeName)) {
            return resImporterRegistry.get(resourceTypeName);
        }
        return new LinkedList<TypeSpec>(); // empty
    }

    public static java.util.List<TypeSpec> getResourceImportersForTypes(
            final java.util.List<String> resourceTypeNames) {
        // get the list of importers for each type name and put them the
        // importers list for return
        final LinkedList<TypeSpec> importers = new LinkedList<TypeSpec>();
        for (final String resourceTypeName : resourceTypeNames) {
            final java.util.List<TypeSpec> imps = getResourceImportersForType(resourceTypeName);
            for (final TypeSpec importer : imps) {
                importers.add(importer);
            }
        }
        Debug.WL("importer types for resource types " + resourceTypeNames
                + " - " + importers);
        return importers;
    }

    /**
* Returns the string from the plugin's resource bundle, or 'key' if not
* found.
*/
    public static String getResourceString(final String key) {
        final ResourceBundle bundle = BilabPlugin.getDefault()
                .getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (final MissingResourceException e) {
            return key;
        }
    }

    public static String getResourceTypeDefaultExtension(
            final String resourceType) {
        final ResourceTypeInfo rinfo = resTypeRegistry.get(resourceType);
        if (rinfo == null) {
            return "dat";
        }
        if (rinfo.extensions.length > 0) {
            return rinfo.extensions[0];
        }
        return "dat";
    }

    public static java.util.List<String> getResourceTypesWithExtension(
            final String extension) {
        // Debug.WL("types for ext "+extension+" - "+resExtRegistry.get(extension));
        if (resExtRegistry.containsKey(extension)) {
            return resExtRegistry.get(extension);
        }
        return new LinkedList<String>(); // empty
    }

    public static java.util.List<String> getResourceTypesWithExtensions(
            final java.util.List<String> extensions) {
        final java.util.List<String> types = new LinkedList<String>();
        for (final String ext : extensions) {
            final java.util.List<String> typesForExt = getResourceTypesWithExtension(ext);
            for (final String type : typesForExt) {
                types.add(type);
            }
        }
        return types;
    }

    // instantiate a new object from the given resource, or null if unable to
    // locate a suitable importer
    public static Object instantiateObjectFromResource(
            final String resourceName, final String resourceType) {
        // first, get all importers for the given type (if type is "unknown" or
        // "", try to deduce it from the extension)
        final java.util.List<String> resTypes = new LinkedList<String>();

        if (resourceType.equals("unknown") || resourceType.length() == 0) {
            final String ext = Util.extension(resourceName);
            if (ext.length() != 0) {
                final java.util.List<String> extTypes = getResourceTypesWithExtension(ext); // possibly
                // more
                // that
                // one
                // type
                // with
                // this
                // extension
                for (final String type : extTypes) {
                    resTypes.add(type);
                }
            }
        } else {
            resTypes.add(resourceType);
        }

        // now, for each resource type (if any) we get the list of possible
        // importer types and try
        // each in turn until we successfully import the resource (and return null
        // if no importers succeed)
        Object imported = null;

        for (final String resType : resTypes) {

            final java.util.List<TypeSpec> importerTypes = getResourceImportersForType(resType);

            for (final TypeSpec importerType : importerTypes) {

                try {
                    imported = instantiateObjectFromResource(importerType,
                            resourceName, resType); // instantiate
                    if (imported != null) {
                        return imported;
                    }

                } catch (final BilabException e) {
                    throw e;
                } catch (final Exception e) {
                    Notify.devWarning(BilabPlugin.class,
                            "failed to instantiate object for resource of type '"
                                    + resType + "' via type '" + importerType
                                    + "' - " + e);
                }

            }

        }

        return null; // exhausted options, failed to import resource
    }

    public static Object instantiateObjectFromResource(
            final TypeSpec importerType, final String resourceName,
            String resourceType) {
        // if the resourceType is "unknown" (or "") see if we can *uniquely*
        // deduce it from the resourceName
        // (currently only by looking at the extension)
        if (resourceType.equals("unknown") || resourceType.length() == 0) {
            final String ext = Util.extension(resourceName);
            if (ext.length() != 0) {
                final java.util.List<String> types = getResourceTypesWithExtension(ext);
                if (types.size() == 1) { // yes, extension maps uniquely to a
                                         // type
                    resourceType = types.get(0);
                }
            }
        }

        Object imported = null;
        java.lang.Class sysClass = null;
        try {
            // use reflection to find & invoke the factory method
            sysClass = (java.lang.Class) importerType.getClassInfo()
                    .getSysType();
            final java.lang.Class[] argTypes = new java.lang.Class[2];
            argTypes[0] = argTypes[1] = String.class;
            final Method method = sysClass
                    .getMethod("importResource", argTypes);

            // invoke it
            final String[] args = new String[2];
            args[0] = resourceName;
            args[1] = resourceType;

            imported = method.invoke(null, args);

            return imported;

        } catch (final NoSuchMethodException e) {
            Notify.devError(
                    BilabPlugin.class,
                    "class "
                            + sysClass
                            + " marked with interace IResourceIOProvider doesn't have the required importResource(String,String) method.");
        } catch (final InvocationTargetException e) {
            throw new BilabException("error importing resource:"
                    + e.getCause().getMessage(), e.getCause());
        } catch (final Exception e) {
            Notify.devInfo(BilabPlugin.class,
                    "unable to instantiate object from resource via type "
                            + sysClass + " - " + e);
        }
        return null;
    }

    public static ViewerBase instantiateViewer(final TypeSpec valueType,
            final Composite parent) {
        // look for matching valueType and instantiate the corresponding viewer
        // type by fetching the single arg
        // constructor via reflection
        for (final RegistryPair viewerPair : viewerRegistry) {
            if (valueType.isA(viewerPair.valueType)) {
                final TypeSpec viewerType = viewerPair.viewerType;

                if (!viewerType.getClassInfo().isExternal()) {
                    Debug.Unimplemented("viewers implemented in scigol");
                } else {
                    final java.lang.Class viewerClass = (java.lang.Class) viewerType
                            .getClassInfo().getSysType();

                    // find the required constructor that takes a single
                    // Composite
                    final java.lang.Class[] argTypes = new java.lang.Class[1];
                    argTypes[0] = Composite.class;
                    java.lang.reflect.Constructor ctor = null;
                    try {
                        ctor = viewerClass.getConstructor(argTypes);
                    } catch (final NoSuchMethodException e) {
                        Debug.Assert(false,
                                "viewer type doesn't implement required constructor with Composite argument");
                    }

                    final Object[] args = new Object[1];
                    args[0] = parent;
                    try {
                        final ViewerBase instance = (ViewerBase) ctor
                                .newInstance(args);
                        return instance;
                    } catch (final Exception e) {
                        Notify.devWarning(BilabPlugin.class,
                                "failed to instantiate new viewer of type:"
                                        + viewerType + " - " + e);
                    }
                    return null; // instantiation failed, just pretend no viewer
                                 // for the type was available
                }
            }
        }

        return null;
    }

    // return platform OS (win or unix)
    public static String platformOS() {
        // just guess based on path seperator for now
        if (File.separatorChar == '/') {
            return "unix";
        }
        return "win";
    }

    public static void registerResourceImporter(final TypeSpec importer) {
        Debug.Assert((importer != null) && importer.isClass());
        if (!importer.getClassInfo().isExternal()) {
            Notify.devError(BilabPlugin.class,
                    "scigol IResourceIOProviders unsupported");
            return;
        }

        Debug.Assert(importer.isA(new TypeSpec(IResourceIOProvider.class)));

        // get the list of supported resource types by reflectively finding the
        // getSupportedResourceTypes() method
        // and calling it
        java.lang.Class sysClass = null;
        try {
            sysClass = (java.lang.Class) importer.getClassInfo().getSysType();
            final Method method = sysClass.getMethod(
                    "getSupportedResourceTypes", new java.lang.Class[0]);

            final java.util.List<String> typesSupported = (java.util.List<String>) method
                    .invoke(null, new Object[0]);
            for (final String resourceTypeName : typesSupported) {

                if (!existsResourceType(resourceTypeName)) {
                    Notify.devError(
                            BilabPlugin.class,
                            "IResourceIOProvider class "
                                    + sysClass
                                    + " supportes resource type '"
                                    + resourceTypeName
                                    + "' that hasn't been registered as a resource type");
                }

                if (!resImporterRegistry.containsKey(resourceTypeName)) {
                    final java.util.List<TypeSpec> importers = new LinkedList<TypeSpec>();
                    importers.add(importer);
                    resImporterRegistry.put(resourceTypeName, importers);
                } else { // add to existing list of importers for this type
                    final java.util.List<TypeSpec> importers = resImporterRegistry
                            .get(resourceTypeName);
                    if (!importers.contains(importer)) {
                        importers.add(importer);
                    }
                }
            }

        } catch (final NoSuchMethodException e) {
            Notify.devError(
                    BilabPlugin.class,
                    "class "
                            + sysClass
                            + " marked with interace IResourceIOProvider doesn't have the required getSupportedResourceTypes() method.");
        } catch (final Exception e) {
            Notify.devWarning(BilabPlugin.class, "error registering importer:"
                    + e);
        }
    }

    public static void registerResourceType(final String resourceTypeName,
            final String description, final String extension) {
        // first enter in into the map keyed on type name
        ResourceTypeInfo rtinfo = null;
        String[] exts = new String[1];
        exts[0] = extension;
        if (extension == null) {
            exts = new String[0];
        }
        if (!existsResourceType(resourceTypeName)) { // new type

            Notify.logInfo(BilabPlugin.class, " registering "
                    + resourceTypeName + ": " + description);

            rtinfo = new ResourceTypeInfo(resourceTypeName, description, exts);
            resTypeRegistry.put(resourceTypeName, rtinfo);
        } else {
            // just add to the list of extensions (description is ignored)
            rtinfo = resTypeRegistry.get(resourceTypeName);

            // copy the existing extension into a new array and add ant new ones
            // also
            final java.util.List<String> newexts = new LinkedList<String>();
            for (final String ext : rtinfo.extensions) {
                newexts.add(ext);
            }
            for (final String ext : exts) {
                if (!newexts.contains(ext)) {
                    newexts.add(ext);
                }
            }
            rtinfo.extensions = newexts.toArray(new String[0]); // replace old
                                                                // array with
                                                                // new one
        }

        // now enter into the map keyed by extension
        if (!resExtRegistry.containsKey(extension)) {
            final java.util.List<String> l = new LinkedList<String>();
            l.add(resourceTypeName);
            resExtRegistry.put(extension, l);
        } else {
            // just add the type to the existing list for this extension
            final java.util.List<String> l = resExtRegistry.get(extension);
            if (!l.contains(resourceTypeName)) {
                l.add(resourceTypeName);
            }
        }
    }

    // convenience
    public static void registerResourceType(final String resourceTypeName,
            final String description, final String ext1, final String ext2) {
        registerResourceType(resourceTypeName, description, ext1);
        registerResourceType(resourceTypeName, description, ext2);
    }

    public static void registerResourceType(final String resourceTypeName,
            final String description, final String ext1, final String ext2,
            final String ext3) {
        registerResourceType(resourceTypeName, description, ext1);
        registerResourceType(resourceTypeName, description, ext2);
        registerResourceType(resourceTypeName, description, ext3);
    }

    public static void registerResourceType(final String resourceTypeName,
            final String description, final String ext1, final String ext2,
            final String ext3, final String ext4) {
        registerResourceType(resourceTypeName, description, ext1);
        registerResourceType(resourceTypeName, description, ext2);
        registerResourceType(resourceTypeName, description, ext3);
        registerResourceType(resourceTypeName, description, ext4);
    }

    public static void registerViewer(final TypeSpec valueType,
            final TypeSpec viewerType) {
        Debug.Assert(viewerType.isClass());
        viewerRegistry.add(new RegistryPair(valueType, viewerType));
    }

    // !!! tmp
    public static scigol.Map resourceTypesMap() {
        final scigol.Map map = new scigol.Map();
        for (final String key : resTypeRegistry.keySet()) {
            final ResourceTypeInfo rtinfo = resTypeRegistry.get(key);
            String exts = "; exts:";
            for (final String ext : rtinfo.extensions) {
                exts += " " + ext;
            }
            map.put(key, rtinfo.description + exts);
        }
        return map;
    }

    public static String resourceURLToFilename(final URL url) {
        if (!url.getProtocol().equals("file")) {
            throw new BilabException("URL doesn't refer to a local file: "
                    + url);
        }

        return url.getFile();
    }

    // generates a unique resource name in the temporary area
    public static String uniqueTemporaryResourceName(final String resourceType) {
        final String extension = getResourceTypeDefaultExtension(resourceType);
        final String name = "temp";

        uniqueIDCount++;

        return "/C:/TEMP/" + name + uniqueIDCount + "." + extension;
    }

    // Resource bundle.
    private ResourceBundle resourceBundle;

    // Python interpreter instance
    PythonInterpreter python;

    // scigol global napespace
    protected NamespaceScope globalScope;

    /**
* The constructor.
*/
    public BilabPlugin() {
        super();
        plugin = this;

    }

    public NamespaceScope getGlobalScope() {
        return globalScope;
    }

    /**
* Returns the plugin's resource bundle,
*/
    public ResourceBundle getResourceBundle() {
        try {
            if (resourceBundle == null) {
                resourceBundle = ResourceBundle
                        .getBundle("bilab.BilabPluginResources");
            }
        } catch (final MissingResourceException x) {
            resourceBundle = null;
        }
        return resourceBundle;
    }

    protected void initializePython() {
        try {
            Notify.devInfo(this, "Instantiating Python interpreter [Jython]");
            final String pythonHome = resourceURLToFilename(findResource("libs/jython2.5.2"));
            final Properties pythonProps = new Properties();
            pythonProps.setProperty("python.home", pythonHome);
            pythonProps.setProperty("python.path", pythonHome);
            pythonProps.setProperty("python.cachedir", "cachedir");

            PythonInterpreter.initialize(System.getProperties(), pythonProps,
                    new String[0]);
            python = new PythonInterpreter();

            // python.setOut(...); /!!! redirect to bilab console
            // python.setErr(...);

        } catch (final IOException e) {
            Notify.logError(this, "unable to instantiate python interpreter");
        }
    }

    protected void loadLibrary(final String jarName) throws IOException {
        NamespaceScope
                .loadLibrary(Util
                        .toNativePathSeparator(resourceURLToFilename(findResource("libs/"
                                + jarName))));
    }

    /**
* This method is called upon plug-in activation
*/
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        // !!!!!!!!!!!!!!!!!!!
        // !!! testing area
        /*
* try {
*
* // JalView Frame f = new Frame("SeqPanel");
*
* Sequence[] seq = FormatAdapter.read(
* "U:\\dev\\GTL\\win\\BiLabRCP\\BiLab\\resources\\sequences\\cxcr_multi_seq.msf"
* ,"File","MSF"); ScoreSequence[] s = new ScoreSequence[seq.length];
* for (int i=0;i < seq.length;i++) { s[i] = new ScoreSequence(seq[i]);
* } DrawableSequence[] s1 = new DrawableSequence[seq.length]; for (int
* i=0;i < seq.length;i++) { s1[i] = new DrawableSequence(seq[i]); }
*
* AlignmentPanel ap = new AlignmentPanel(null,s1); ScorePanel sp = new
* ScorePanel(null,s); //ap.setScorePanel(sp); f.setLayout(new
* BorderLayout()); f.add("Center",ap); f.add("South",sp);
* f.resize(700,500); //f.pack(); f.show(); //!!! JalView
*
*
*
*
* //String[] args = new String[] {
* "U:\\dev\\GTL\\win\\BiLabRCP\\BiLab\\resources\\molecules\\AF084455.embl"
* }; //uk.ac.sanger.artemis.components.ArtemisMain.main(new String[0]);
*
* } catch (Exception e) {
* Notify.devWarning(this,"exception in Artemis:"+e);
* e.printStackTrace(); //Debug.WL(""+e); }
*
* //SQLiteTest.test();
*/
        /*
* try { FileSystemNotebookStore store =
* FileSystemNotebookStore.getInstance(); String loc =
* "/C:/Documents and Settings/jungd/Desktop/tmp";
* Notify.debug(this,"exists? "
* +(store.existsNotebook(loc+"/MyNotebook")));
*
* String uri = null; if (!store.existsNotebook(loc+"/MyNotebook")) {
* Notify.debug(this,"creating notebook MyNotebook in "+loc); uri =
* store.createNotebook(loc,"MyNotebook");
* Notify.debug(this,"resulting URI:"+uri);
* store.createSection(uri,"Introduction",null); } else { uri =
* store.getCurrentNotebookVersion(loc+"/MyNotebook");
* Notify.debug(this,"notebook URI:"+uri); }
*
* Notify.debug(this,"exists? "+(store.existsNotebook(uri)));
*
* String[] versions = store.listNotebookVersionURIs(uri); for(String
* ver : versions) Notify.debug(" version:"+ver);
*
* Notify.debug("current version="+store.getCurrentNotebookVersion(
* "file:///C:/Documents%20and%20Settings/jungd/Desktop/tmp/MyNotebook?version=another&page=5"
* ));
*
* Notify.debug("current section count="+store.getSectionCount(uri));
* Notify.debug("current section[1] URI="+store.getSectionURI(uri,1));
* Notify.debug("current section[2] URI="+store.getSectionURI(uri,2));
* //store.deleteNotebookAll(uri);
*
* } catch (Exception e) { e.printStackTrace();
* Notify.devError(this,"got exception:"+e.getMessage()); }
*/

        // !!!
        // !!!!!!!!! end testing area

        globalScope = NamespaceScope.newGlobalNamespaceScope();

        // load libraries
        Notify.logInfo(this, "Loading libraries");
        loadLibrary("biojava.jar");
        loadLibrary("jakarta-regexp-1.5.jar");
        loadLibrary("commons-collections-2.1.jar");
        loadLibrary("commons-net-2.2.jar");
        loadLibrary("jobcontrol.jar");
        loadLibrary("antlr-2.7.6.jar");
        loadLibrary("commons-logging-1.0.4.jar");
        loadLibrary("commons-discovery-0.2.jar");
        loadLibrary("javaws.jar");
        loadLibrary("xercesImpl.jar");
        loadLibrary("jalview-1.7.5b.jar");
        loadLibrary("Jmol.jar");
        loadLibrary("Ice.jar");
        loadLibrary("jgrapht-jdk1.5.jar");
        loadLibrary("dasobert.jar");
        loadLibrary("junit-4.8.1.jar");
        loadLibrary("jemboss.jar");
        loadLibrary("jaligner.jar");
        loadLibrary("cdk-1.2.10.jar");

        //loadLibrary("jython.jar");

        // register some select classes
        NamespaceScope.registerLibraryClass("bilab", "seq");
        NamespaceScope.registerLibraryClass("bilab", "DNA");
        NamespaceScope.registerLibraryClass("bilab", "RNA");
        NamespaceScope.registerLibraryClass("bilab", "protein");
        NamespaceScope.registerLibraryClass("bilab", "carbohydrate");
        NamespaceScope.registerLibraryClass("bilab", "alignment");
        NamespaceScope.registerLibraryClass("bilab", "molecule");
        NamespaceScope.registerLibraryClass("bilab", "seqdb");
        NamespaceScope.registerLibraryClass("bilab", "picture");
        NamespaceScope.registerLibraryClass("bilab", "Notify");
        NamespaceScope.registerLibraryClass("bilab", "Doc");
        NamespaceScope.registerLibraryClass("bilab", "Summary");
        NamespaceScope.registerLibraryClass("bilab", "Sophistication");
        NamespaceScope.registerLibraryClass("bilab", "Util");
        NamespaceScope.registerLibraryClass("bilab", "Emboss");
        //NamespaceScope.registerLibraryClass("bilab", "jython");
        NamespaceScope.registerLibraryClass("bilab", "ExternalApps");
        NamespaceScope.registerLibraryClass("bilab", "INotifier");
        NamespaceScope.registerLibraryClass("bilab", "IAnnotated");

        globalScope.addUsingNamespace("bilab"); // make java bilab package
                                                // available

        // register some value viewers
        // NB: register viewed subclasses before their more generic supers
        Notify.logInfo(this, "Registering typed viewers");
        registerViewer(new TypeSpec(DNA.class), new TypeSpec(
                ArtemisViewer.class));
        registerViewer(new TypeSpec(RNA.class), new TypeSpec(
                ArtemisViewer.class));
        registerViewer(new TypeSpec(protein.class), new TypeSpec(
                SeqStringViewer.class));
        registerViewer(new TypeSpec(carbohydrate.class), new TypeSpec(
                SeqStringViewer.class));

        registerViewer(new TypeSpec(molecule.class), new TypeSpec(
                JMolViewer.class)); // molecule viewer

        registerViewer(new TypeSpec(alignment.class), new TypeSpec(
                JalViewAlignmentViewer.class)); // multiple alignment viewer

        registerViewer(new TypeSpec(picture.class), new TypeSpec(
                PictureViewer.class));
        registerViewer(new TypeSpec(java.net.URL.class), new TypeSpec(
                HTMLViewer.class));

        Notify.logInfo(this, "Registering resource types");

        // register resource types
        registerResourceType("TEXT", "Unicode UTF-8 text", "txt");
        registerResourceType("HTML", "Hyper-Text Markup Language document",
                "html", "htm");

        registerResourceType("PNG", "Portable Network Graphics (PNG) bitmap",
                "png");
        registerResourceType("JPG",
                "Joint Photographic Experts Group (JPEG) bitmap", "jpg", "jpeg");
        registerResourceType("GIF",
                "Compuserve Graphics Interchange Format (GIF) bitmap", "gif");

        registerResourceType("Postscript", "Postscript program drawing", "ps",
                "eps");

        registerResourceType("pdb", "Protein Data Bank", "pdb", "ent");
        registerResourceType("mol",
                "Molecular Design Limited's (MDL) Mol file", "mol", "mdl");
        registerResourceType("pqs", "PQS format", "pqs");
        registerResourceType("sdf", "MDL ISIS SDF format", "sdf", "sd");
        registerResourceType("xyz",
                "Minnesota Supercomputer Center's (MSC) XYZ (XMol) format",
                "xyz");

        registerResourceType("EMBL",
                "EMBL Nucleotide Sequence Database (EMBL-Bank)", "embl");
        registerResourceType(
                "SwissProt",
                "European Bioinformatics Institute (EBI) Swiss-Prot protein database",
                "");
        registerResourceType("GenBank",
                "GenBank NCBI/NIH nucleotide sequence databse", "gb");
        registerResourceType("GenPept",
                "GenPept NCBI/NIH protein sequence database", "");

        registerResourceType("CT", "naview RNA secondary structure format",
                "ct");
        registerResourceType("ABI",
                "ABI automated chromatagraph sequencer trace format", "abi");

        registerResourceType("FASTA", "Pearson/FASTA DNA/protein sequence",
                "fa", "fsa", "fasta", "fna");
        registerResourceType("BLAST", "BLAST similarity search results",
                "blast");

        registerResourceType("CLUSTALW",
                "CLUSTALW Multiple sequence alignment", "aln");
        registerResourceType("MSF", "Pileup/GCG Multiple sequence alignment",
                "msf");
        registerResourceType("PFAM", "PFAM multiple alignment", "");
        registerResourceType("BLC", "AMPS multiple alignment", "");

        registerResourceType("MEV",
                "TIGR MultiExperimentViewer Microarray data", "mev");
        registerResourceType("Genepix", "Genepix Microarray data", "grp");
        registerResourceType("Affymetrix", "Affymetrix Microarray data", "txt");

        Notify.logInfo(this, "Registering resource importers");

        // register resource importers
        // NB: register subclasses before their more generic supers
        registerResourceImporter(new TypeSpec(MoleculeImpl.class));
        registerResourceImporter(new TypeSpec(seq.class));
        registerResourceImporter(new TypeSpec(alignment.class));
        registerResourceImporter(new TypeSpec(picture.class));

        initializePython();

        // execute bilab.sg
        try {
            Notify.userInfo(this, "executing bilab.sg library");
            executeScigolSourceStream(globalScope,
                    findResource("libs/bilab.sg"), "bilab.sg");
        } catch (final Throwable e) {
            Notify.userWarning(this, "error executing bilab.sg library:" + e);
        }

        // bring in some extra default namespaces
        globalScope.addUsingNamespace("bilab.lib");

        // execute sample.sg
        try {
            Notify.userInfo(this, "executing sample.sg");
            executeScigolSourceStream(globalScope,
                    findResource("libs/sample.sg"), "sample.sg");
        } catch (final Throwable e) {
            Notify.userWarning(this, "error executing sample.sg:" + e);
        }

    }

    /**
* This method is called when the plug-in is stopped
*/
    public void stop(final BundleContext context) throws Exception {
        python.cleanup();

        super.stop(context);
        plugin = null;
        resourceBundle = null;
    }
    
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

}


