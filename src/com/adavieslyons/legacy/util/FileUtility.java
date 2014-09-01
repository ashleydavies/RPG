package com.adavieslyons.legacy.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * @author Ashley
 */
public final class FileUtility {
	public static FileUtility instance = new FileUtility();

	private FileUtility() {

	}

	public File getFileStartingWith(String directory, final String startsWith) {
		return getFilesStartingWith(directory, startsWith)[0];
	}

	public File[] getFilesStartingWith(String directory, final String startsWith) {
		File root = new File(directory);

		FilenameFilter beginsWith = new FilenameFilter() {
			@Override
			public boolean accept(File directory, String filename) {
				return filename.startsWith(startsWith);
			}
		};

		File[] files = root.listFiles(beginsWith);

		return files;
	}

	public String IDFormat(int num) {
		return IDFormat(num, 5);
	}

	public String IDFormat(int num, int chars) {
		return IDFormat(Integer.toString(num), chars);
	}

	public String IDFormat(String numstr, int chars) {
		while (numstr.length() < chars) {
			numstr = "0" + numstr;
		}

		return numstr;
	}
}
