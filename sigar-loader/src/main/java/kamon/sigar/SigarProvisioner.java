/* =========================================================================================
 * Copyright © 2013-2014 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package kamon.sigar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarLoader;

/**
 * Sigar native library extractor and loader.
 */
public class SigarProvisioner {

	/** The logger. */
	private static final Logger logger = Logger
			.getLogger(SigarProvisioner.class.getName());

	/**
	 * Location of native libraries.
	 * 
	 * <pre>
	 * when stored in loader.jar:     /${LIB_DIR}/libsigar-xxx.so
	 * after default load/extract:    ${user.dir}/${LIB_DIR}/libsigar-xxx.so
	 * </pre>
	 */
	public static final String LIB_DIR = "native";

	/** The Constant EOF. */
	static final int EOF = -1;
	
	/** The Constant SIZE. */
	static final int SIZE = 64 * 1024;

	/**
	 *  Perform stream copy.
	 *
	 * @param input The input stream.
	 * @param output The output stream.
	 * @throws Exception the exception
	 */
	public static void transfer(final InputStream input,
			final OutputStream output) throws Exception {
		final byte[] data = new byte[SIZE];
		while (true) {
			final int count = input.read(data, 0, SIZE);
			if (count == EOF) {
				break;
			}
			output.write(data, 0, count);
		}
	}

	/** Environment property which provides sigar extract location. */
	public static final String ENVIRONMENT_VARIABLE = "KAMON_SIGAR_FOLDER";

	/** System property which provides sigar extract location. */
	public static final String SYSTEM_PROPERTY = "kamon.sigar.folder";

	/**
	 * 
	 * Configured sigar library extract location. <br>
	 * Priority 1) is user provided comman line agent options. <br>
	 * Priority 2) is user provided o/s environment variable. <br>
	 * Priority 3) is user provided java system property. <br>
	 * Priority 4) is hard coded location. <br>
	 *
	 * @param options
	 *            Command line agent options.
	 * @return Configured sigar library extract location.
	 */
	public static String defaultLocation(final String options) {

		/** Priority 1) is user provided agent options. */
		if (options != null) {
			final String[] optionArray = options.split(",");
			for (final String option : optionArray) {
				if (option.startsWith(SYSTEM_PROPERTY)) {
					return option.substring(1 + option.indexOf("="));
				}
			}
			throw new IllegalArgumentException(
					"Missing extract location option: " + SYSTEM_PROPERTY);
		}

		/** Priority 2) is user provided o/s environment variable. */
		final String variable = System.getenv(ENVIRONMENT_VARIABLE);
		if (variable != null) {
			return variable;
		}

		/** Priority 3) is user provided java system property. */
		final String property = System.getProperty(SYSTEM_PROPERTY);
		if (property != null) {
			return property;
		}

		/** Priority 4) is hard coded location. */
		return System.getProperty("user.dir") + File.separator + LIB_DIR;

	}

	/**
	 *  Extract and load native sigar library in the default folder.
	 *
	 * @throws Exception the exception
	 */
	public static void provision() throws Exception {
		provision(new File(defaultLocation(null)));
	}

	/**
	 *  Verify if sigar native library is loaded and operational.
	 *
	 * @return true, if is native loaded
	 */
	public static boolean isNativeLoaded() {
		try {
			final Sigar sigar = new Sigar();
			sigar.getPid();
			sigar.close();
			return true;
		} catch (final Throwable e) {
			return false;
		}
	}

	/**
	 *  Extract and load native sigar library in the provided folder.
	 *
	 * @param folder the folder
	 * @throws Exception the exception
	 */
	public static synchronized void provision(final File folder)
			throws Exception {

		if (isNativeLoaded()) {
			logger.warning("Sigar library is already provisioned.");
			return;
		}

		if (!folder.exists()) {
			folder.mkdirs();
		}

		final SigarLoader sigarLoader = new SigarLoader(Sigar.class);

		/** Library name for given architecture. */
		final String libraryName = sigarLoader.getLibraryName();

		/** Library location embedded in the jar class path. */
		final String sourcePath = "/" + LIB_DIR + "/" + libraryName;

		/** Absolute path to the extracted library the on file system. */
		final File targetPath = new File(folder, libraryName).getAbsoluteFile();

		/** Extract library form the jar to the local file system. */
		final InputStream sourceStream = SigarProvisioner.class
				.getResourceAsStream(sourcePath);
		final OutputStream targetStream = new FileOutputStream(targetPath);
		transfer(sourceStream, targetStream);
		sourceStream.close();
		targetStream.close();

		/** Load library via absolute path. */
		final String libraryPath = targetPath.getAbsolutePath();
		System.load(libraryPath);

		/** Tell sigar loader that the library is already loaded. */
		System.setProperty("org.hyperic.sigar.path", "-");
		sigarLoader.load();

		logger.info("Sigar library provisioned: " + libraryPath);

	}

}
