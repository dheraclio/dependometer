/*
 * Copyright 2009 Valtech GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.valtech.source.dependometer.app.typedefprovider.filebased.cpp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.ICodeReaderCache;
import org.eclipse.core.runtime.CoreException;

/**
 * Provides access to C++ compilation units stored in the file system.
 *
 * @author Carsten Kaiser (carsten.kaiser@valtech.de)
 * @version 1.0
 */
public class FileCodeReaderFactory implements ICodeReaderFactory {

    /**
     * Cache file system directory entries only.
     *
     * Tried also caching file content, but effect was negative.
     *
     * @author Bernhard Rümenapp
     */
    public static class LittleCodeReaderCache implements ICodeReaderCache {

        // private static Logger s_Logger = Logger.getLogger(LittleCodeReaderCache.class.getName());
        private String charSet;

        /**
         * This directory entry cache prevents the include handler from
         * searching every include on every path, that leaded to quadratic cost
         * in file system lookup.
         */
        private Map<String, Set<String>> m_positives = new HashMap<String, Set<String>>();

        /**
         * Ctor.
         *
         * @param charSet character encoding of code files
         */
        public LittleCodeReaderCache(String charSet) {

        }

        /**
         * Ctor. Uses default character encoding for code files.
         */
        public LittleCodeReaderCache() {
        }

        /**
         * Create reader for a file.
         *
         * @param key full canonical file path.
         *
         * The file path will be traversed from the highest directory and
         * matched against the entry cache, to quickly sort out non-existent
         * paths.
         * @return Header for file
         *
         * @see
         * org.eclipse.cdt.core.parser.ICodeReaderCache#get(java.lang.String)
         */
        @Override
        public CodeReader get(String key) {
            key = key.intern();

            final char separator = '/';
            for (int pos = key.indexOf(separator); pos >= 0; pos = key.indexOf(separator, pos)) {
                String directory = key.substring(0, pos).intern();
                if (directory.length() > 0) {
                    if (!m_positives.containsKey(directory)) {
                        File dir = new File(directory);
                        if (dir.isDirectory()) {
                            /*
                             * Create cached entries only for readable directories. You CAN have non-readable directories with
                             * readable contained directories on unix type systems.
                             */
                            if (dir.canRead()) {
                                File[] files = new File(directory).listFiles();
                                Set<String> set = new HashSet<String>(files.length);
                                for (File file : files) {
                                    set.add(file.getName().intern());
                                }
                                m_positives.put(directory, set);
                            }
                        }
                    }
                }

                while (key.charAt(pos) == separator) {
                    pos++;
                }

                int stop = key.indexOf(separator, pos);
                if (stop <= 0) {
                    stop = key.length();
                }

                String name = key.substring(pos, stop).intern();

                Set<String> files = m_positives.get(directory);
                if (files != null && !files.contains(name)) {
               // The current directory is cached
                    // and the current name is not in the cache,
                    // so we are on a wrong path.
                    return null;
                }
            }

            return createFileReader(key, charSet);
        }

        /**
         * Gets code reader for file. Uses the same implementation as in Eclipse
         * InternalParserUtil.createFileReader(key). Just added the charSet
         * param.
         *
         * @param finalPath file path
         * @param charSet encoding of the file
         * @return code reader
         */
        public static CodeReader createFileReader(String finalPath, String charSet) {
            File includeFile = new File(finalPath);
            if (includeFile.exists() && includeFile.isFile()) {
                try {
               // use the canonical path so that in case of non-case-sensitive OSs
                    // the CodeReader always has the same name as the file on disk with
                    // no differences in case.

                    CodeReader codeReader;
                    if (charSet == null) {
                        codeReader = new CodeReader(includeFile.getCanonicalPath());
                    } else {
                        codeReader = new CodeReader(includeFile.getCanonicalPath(), charSet);
                    }

                    return codeReader;
                } catch (IOException e) {
                }
            }
            return null;
        }

        /**
         * Dummy implementation.
         *
         * @param key source file name
         * @return unused, so i hope.
         */
        @Override
        public CodeReader remove(String key) {
            return null;
        }

        /**
         * Dummy implementation.
         *
         * @return unknown unit
         * @see org.eclipse.cdt.core.parser.ICodeReaderCache#getCurrentSpace()
         */
        @Override
        public int getCurrentSpace() {
            return 1;
        }

        /**
         * Free the directory cache. Please call after all parsing done.
         *
         * @see org.eclipse.cdt.core.parser.ICodeReaderCache#flush()
         */
        @Override
        public void flush() {
            m_positives.clear();
        }

        public String getCharSet() {
            return charSet;
        }

        @Override
        public CodeReader get(String string, IIndexFileLocation iifl) throws CoreException, IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    /**
     * Singleton factory instance
     */
    private static FileCodeReaderFactory instance;

    /**
     * Code reader cache
     */
    private ICodeReaderCache cache = null;

    /**
     * Private constructor
     *
     * @param cache Code reader cache to use
     */
    private FileCodeReaderFactory(ICodeReaderCache cache) {
        this.cache = cache;
    }

    /**
     * @see
     * org.eclipse.cdt.core.dom.ICodeReaderFactory#createCodeReaderForInclusion(java.lang.String)
     */
    @Override
    public CodeReader createCodeReaderForInclusion(String path) {
        return cache.get(path);
    }

    /**
     * @see
     * org.eclipse.cdt.core.dom.ICodeReaderFactory#createCodeReaderForTranslationUnit(java.lang.String)
     */
    @Override
    public CodeReader createCodeReaderForTranslationUnit(String path) {
        return cache.get(path);
    }

    /**
     * @see org.eclipse.cdt.core.dom.ICodeReaderFactory#getCodeReaderCache()
     */
    @Override
    public ICodeReaderCache getCodeReaderCache() {
        return cache;
    }

    /**
     * @see org.eclipse.cdt.core.dom.ICodeReaderFactory#getUniqueIdentifier()
     */
    @Override
    public int getUniqueIdentifier() {
        return 3;
    }

    /**
     * Returns the singleton factory instance.
     *
     * @param charSet file encoding
     *
     * @return Factory
     */
    public static FileCodeReaderFactory getInstance(String charSet) {
        if (instance == null) {
            instance = new FileCodeReaderFactory(new FileCodeReaderFactory.LittleCodeReaderCache(charSet));
        }

        return instance;
    }

    /**
     * Returns the singleton factory instance. Uses default encoding.
     *
     * @return factory.
     */
    public static FileCodeReaderFactory getInstance() {
        return getInstance(null);
    }
}
