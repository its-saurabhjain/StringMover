package com.xerox.StringMover;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class DirectoryWatcher {

	static public Collection getFilesInDirectory(File dir, String[] exts) {
		Collection files = FileUtils.listFiles(dir, exts, false);
		System.out.println(files);
		return files;
	}
}
