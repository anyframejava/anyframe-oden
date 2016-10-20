/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle.core.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.prefs.BackingStore;
import org.apache.felix.prefs.BackingStoreManager;
import org.apache.felix.prefs.PreferencesDescription;
import org.apache.felix.prefs.PreferencesImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * To save Felix's Preferences Service's data to the specific location not cache,
 * this class modified the org.apache.felix.prefs.imple.DataFileBackingStoreImpl.
 */
public class SymNameBackingStoreImpl implements BackingStore {

    /** The bundle context. */
    protected BundleContext bundleContext;
    
    /** The root directory (or null if not available) */
    protected File rootDirectory;

    /**
     * modified
     * @param context
     */
	protected void activate(ComponentContext context){
		this.bundleContext = context.getBundleContext();
		
		String root = this.bundleContext.getProperty("prefs.root");
		if(root == null)
			root = this.bundleContext.getProperty("felix.cache.rootdir"); 
		this.rootDirectory = new File(root);
	}
    
    /**
     * @see org.apache.felix.sandbox.preferences.impl.StreamBackingStoreImpl#checkAccess()
     */
    protected void checkAccess() throws BackingStoreException {
        if ( this.rootDirectory == null ) {
            throw new BackingStoreException("Saving of data files to the bundle context is currently not supported.");
        }
    }

    /**
     * @see org.apache.felix.sandbox.preferences.impl.StreamBackingStoreImpl#getOutputStream(org.apache.felix.sandbox.preferences.PreferencesDescription)
     */
    protected OutputStream getOutputStream(PreferencesDescription desc) throws IOException {
        final File file = this.getFile(desc);
        return new FileOutputStream(file);
    }

    /**
     * @see org.apache.felix.prefs.BackingStore#availableBundles()
     */
    public Long[] availableBundles() {
         // If the root directory is not available, then we do nothing!
        try {
            this.checkAccess();
        } catch (BackingStoreException ignore) {
            return new Long[0];
        }
        final Set bundleIds = new HashSet();
        final File[] children = this.rootDirectory.listFiles();
        for( int i=0; i<children.length; i++ ) {
            final File current = children[i];

            final PreferencesDescription desc = this.getDescription(current);
            if ( desc != null ) {
                bundleIds.add(desc.getBundleId());
            }
        }
        return (Long[])bundleIds.toArray(new Long[bundleIds.size()]);
    }

    protected PreferencesDescription getDescription(File file) {
        final String fileName = file.getName();
        // parse the file name to get: bundle id, user|system identifer
        if ( fileName.endsWith(".ser") ) {
            final String name = fileName.substring(0, fileName.length() - 4);
            final Long bundleId;
            final String identifier;
            int pos = name.indexOf("_");
            if ( pos != -1 ) {
                identifier = name.substring(pos+1);
                bundleId = getBundleId(name.substring(0, pos));
            } else {
                bundleId = getBundleId(name);
                identifier = null;
            }
            return new PreferencesDescription(bundleId, identifier);
        }
        return null;
    }

    protected Long getBundleId(String name) {
    	for(Bundle bnd : bundleContext.getBundles()){
    		if(name.equals(bnd.getSymbolicName())){
    			return bnd.getBundleId();
    		}
    	}
    	return -1L;
    }
    
    /**
     * @see org.apache.felix.prefs.BackingStore#remove(java.lang.Long)
     */
    public void remove(Long bundleId) throws BackingStoreException {
        this.checkAccess();
        final File[] children = this.rootDirectory.listFiles();
        for( int i=0; i<children.length; i++ ) {
            final File current = children[i];

            final PreferencesDescription desc = this.getDescription(current);
            if ( desc != null ) {
                if ( desc.getBundleId().equals(bundleId) ) {
                    current.delete();
                }
            }
        }
    }

    /**
     * @see org.apache.felix.prefs.BackingStore#loadAll(org.apache.felix.prefs.BackingStoreManager, java.lang.Long)
     */
    public PreferencesImpl[] loadAll(BackingStoreManager manager, Long bundleId) throws BackingStoreException {
        this.checkAccess();
        final List list = new ArrayList();
        final File[] children = this.rootDirectory.listFiles();
        for( int i=0; i<children.length; i++ ) {
            final File current = children[i];

            final PreferencesDescription desc = this.getDescription(current);
            if ( desc != null ) {
                if ( desc.getBundleId().equals(bundleId) ) {
                    final PreferencesImpl root = new PreferencesImpl(desc, manager);
                    try {
                        final FileInputStream fis = new FileInputStream(current);
                        this.read(root, fis);
                        fis.close();
                    } catch (IOException ioe) {
                        throw new BackingStoreException("Unable to load preferences.", ioe);
                    }
                    list.add(root);
                }
            }
        }
        return (PreferencesImpl[])list.toArray(new PreferencesImpl[list.size()]);
    }

    /**
     * @see org.apache.felix.prefs.BackingStore#load(org.apache.felix.prefs.BackingStoreManager, org.apache.felix.prefs.PreferencesDescription)
     */
    public PreferencesImpl load(BackingStoreManager manager, PreferencesDescription desc) throws BackingStoreException {
        this.checkAccess();
        final File file = this.getFile(desc);
        if ( file.exists() ) {
            try {
                final PreferencesImpl root = new PreferencesImpl(desc, manager);
                final FileInputStream fis = new FileInputStream(file);
                this.read(root, fis);
                fis.close();

                return root;
            } catch (IOException ioe) {
                throw new BackingStoreException("Unable to load preferences.", ioe);
            }
        }
        return null;
    }

    /**
     * Get the file fo the preferences tree.
     * @param desc
     * @return
     */
    protected File getFile(PreferencesDescription desc) {
    	final String name = bundleContext.getBundle(desc.getBundleId()).getSymbolicName();
        final StringBuffer buffer = new StringBuffer(name);
        if ( desc.getIdentifier() != null ) {
            buffer.append('_');
            buffer.append(desc.getIdentifier());
        }
        buffer.append(".ser");
        final File file = new File(this.rootDirectory, buffer.toString());
        return file;
    }
    
    /**
     * Read the preferences recursively from the input stream.
     * @param prefs
     * @param is
     * @throws IOException
     */
    protected void read(PreferencesImpl prefs, InputStream is)
    throws IOException {
        this.readPreferences(prefs, is);
        final ObjectInputStream ois = new ObjectInputStream(is);
        final int numberOfChilren = ois.readInt();
        for(int i=0; i<numberOfChilren; i++) {
            int length = ois.readInt();
            final byte[] name = new byte[length];
            ois.readFully(name);
            final PreferencesImpl impl = (PreferencesImpl)prefs.node(new String(name, "utf-8"));
            this.read(impl, is);
        }
    }
    
    /**
     * Load this preferences from an input stream.
     * Currently the prefs are read from an object input stream and
     * the serialization is done by hand.
     * The changeSet is neither updated nor cleared in order to provide
     * an update/sync functionality. This has to be done at a higher level.
     */
    protected void readPreferences(PreferencesImpl prefs, InputStream in) throws IOException {
        final ObjectInputStream ois = new ObjectInputStream(in);
        final int size = ois.readInt();
        for(int i=0; i<size; i++) {
            int keyLength = ois.readInt();
            int valueLength = ois.readInt();
            final byte[] key = new byte[keyLength];
            final byte[] value = new byte[valueLength];
            ois.readFully(key);
            ois.readFully(value);
            prefs.getProperties().put(new String(key, "utf-8"), new String(value, "utf-8"));
        }
    }
    
    /**
     * @see org.apache.felix.prefs.BackingStore#update(org.apache.felix.prefs.PreferencesImpl)
     */
    public void update(PreferencesImpl prefs) throws BackingStoreException {
        final PreferencesImpl root = this.load(prefs.getBackingStoreManager(), prefs.getDescription());
        if ( root != null ) {
            // and now update
            if ( root.nodeExists(prefs.absolutePath()) ) {
                final PreferencesImpl updated = (PreferencesImpl)root.node(prefs.absolutePath());
                prefs.update(updated);
            }
        }
    }
    
    /**
     * @see org.apache.felix.prefs.BackingStore#store(org.apache.felix.prefs.PreferencesImpl)
     */
    public void store(PreferencesImpl prefs) throws BackingStoreException {
        // do we need to store at all?
        if ( !this.hasChanges(prefs) ) {
            return;
        }
        this.checkAccess();
        // load existing data
        final PreferencesImpl savedData = this.load(prefs.getBackingStoreManager(), prefs.getDescription());
        if ( savedData != null ) {
            // merge with saved version
            final PreferencesImpl n = savedData.getOrCreateNode(prefs.absolutePath());
            n.applyChanges(prefs);
            prefs = n;
        }
        final PreferencesImpl root = prefs.getRoot();
        try {
            final OutputStream os = this.getOutputStream(root.getDescription());
            this.write(root, os);
            os.close();
        } catch (IOException ioe) {
            throw new BackingStoreException("Unable to store preferences.", ioe);
        }
    }
    
    /**
     * Has the tree changes?
     */
    protected boolean hasChanges(PreferencesImpl prefs) {
        if ( prefs.getChangeSet().hasChanges() ) {
            return true;
        }
        final Iterator i = prefs.getChildren().iterator();
        while ( i.hasNext() ) {
            final PreferencesImpl current = (PreferencesImpl) i.next();
            if ( this.hasChanges(current) ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Write the preferences recursively to the output stream.
     * @param prefs
     * @param os
     * @throws IOException
     */
    protected void write(PreferencesImpl prefs, OutputStream os)
    throws IOException {
        this.writePreferences(prefs, os);
        final ObjectOutputStream oos = new ObjectOutputStream(os);
        final Collection children = prefs.getChildren();
        oos.writeInt(children.size());
        oos.flush();
        final Iterator i = children.iterator();
        while ( i.hasNext() ) {
            final PreferencesImpl child = (PreferencesImpl) i.next();
            final byte[] name = child.name().getBytes("utf-8");
            oos.writeInt(name.length);
            oos.write(name);
            oos.flush();
            this.write(child, os);
        }
    }
    
    /**
     * Save this preferences to an output stream.
     * Currently the prefs are written through an object output
     * stream with handmade serialization of strings.
     * The changeSet is neither updated nor cleared in order to provide
     * an update/sync functionality. This has to be done at a higher level.
     */
    protected void writePreferences(PreferencesImpl prefs, OutputStream out) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        final int size = prefs.getProperties().size();
        oos.writeInt(size);
        final Iterator i = prefs.getProperties().entrySet().iterator();
        while ( i.hasNext() ) {
             final Map.Entry entry = (Map.Entry)i.next();
             final byte[] key = entry.getKey().toString().getBytes("utf-8");
             final byte[] value = entry.getValue().toString().getBytes("utf-8");
             oos.writeInt(key.length);
             oos.writeInt(value.length);
             oos.write(key);
             oos.write(value);
        }
        oos.flush();
    }
}
