package com.hhoss.lang;

import java.io.File;

/**
 * file delete util 
 * @author kejun
 *
 */
public class FileUtil {
	
	public static void delete(File file){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f:files){
				delete(f);
			}
		}
		file.delete();
	}

}
