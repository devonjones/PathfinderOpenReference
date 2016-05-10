package org.evilsoft.pathfinder.reference.utils;

import android.os.Environment;
import android.os.StatFs;

/**
 * This class is designed to get available space in various storage of android. 
 * It contains methods which provide you the available space in different units e.g
 * bytes, KB, MB, GB. OR you can get the number of available blocks in storage.
 *
 * Based on the implementation at:
 *   http://stackoverflow.com/questions/2941552/android-sd-card-free-space
 */
public class AvailableSpaceHandler {

	/**
	 * Number of bytes in one KB = 2<sup>10</sup>
	 */
	public final static long SIZE_KB = 1024L;

	/**
	 * Number of bytes in one MB = 2<sup>20</sup>
	 */
	public final static long SIZE_MB = SIZE_KB * SIZE_KB;

	/**
	 * Number of bytes in one GB = 2<sup>30</sup>
	 */
	public final static long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;

	private AvailableSpaceHandler() {
	}

	/**
	 * @return Number of bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInBytes() {
		return getAvailableSpaceInBytes(Environment.getExternalStorageDirectory().getPath());
	}
	
	/**
	 * @return Number of bytes available on external storage
	 */
	public static long getInternalAvailableSpaceInBytes() {	
		return getAvailableSpaceInBytes(Environment.getDataDirectory().getPath());
	}
	
	/**
	 * @return Number of kilobytes available on internal storage
	 */
	public static long getInternalAvailableSpaceInKB() {
		return getInternalAvailableSpaceInBytes() / SIZE_KB;
	}
	
	/**
	 * @return Number of kilobytes available on external storage
	 */
	public static long getExternalAvailableSpaceInKB() {
		return getExternalAvailableSpaceInBytes() / SIZE_KB;
	}
	
	/**
	 * @return Number of megabytes available on internal storage
	 */
	public static long getInternalAvailableSpaceInMB() {
		return getInternalAvailableSpaceInBytes() / SIZE_MB;
	}
	
	/**
	 * @return Number of megabytes available on external storage
	 */
	public static long getExternalAvailableSpaceInMB() {
		return getExternalAvailableSpaceInBytes() / SIZE_MB;
	}
	
	/**
	 * @return gigabytes of bytes available on internal storage
	 */
	public static long getInternalAvailableSpaceInGB() {
		return getInternalAvailableSpaceInBytes() / SIZE_GB;
	}
	
	/**
	 * @return gigabytes of bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInGB() {
		return getExternalAvailableSpaceInBytes() / SIZE_GB;
	}
	
	/**
	 * @return Total number of available blocks on internal storage
	 */
	public static long getInternalStorageAvailableBlocks() {
		return getAvailableSpaceInBlocks(Environment.getDataDirectory().getPath());
	}
	
	/**
	 * @return Total number of available blocks on external storage
	 */
	public static long getExternalStorageAvailableBlocks() {
		return getAvailableSpaceInBlocks(Environment.getExternalStorageDirectory().getPath());
	}
	
	public static long getAvailableSpaceInBytes(String path) {
		StatFs stat = new StatFs(path);
		return getAvailableSpaceInBlocks(path) * (long) stat.getBlockSize();
	}
	
	public static long getAvailableSpaceInBlocks(String path) {
		long availableBlocks = -1L;
		try {
			StatFs stat = new StatFs(path);
			availableBlocks = stat.getAvailableBlocks();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return availableBlocks;
	}
}
