package com.xerox.StringMover;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class DirectoryScanner {
	
	private static Log log = LogFactory.getFactory().getInstance(DirectoryScanner.class);

	static public Collection getFilesInDirectory(File dir, String[] exts) {
		// caution: exts may be null
		Collection files = FileUtils.listFiles(dir, exts, false);
		if( log.isDebugEnabled() ) {
			log.debug("Files in " + dir + ": " + files);
		}
		return files;
	}
	
	static public Collection getFilesInDirectoryFIFO(File dir, String[] exts) {
		Collection col= getFilesInDirectory(dir, exts);
		List<File> files = new ArrayList<File>(col);
		
		Collections.sort(files, new Comparator<File>(){
		    public int compare(File f1, File f2)
		    {
		        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
		    } });
		
		return files;
	}
}
