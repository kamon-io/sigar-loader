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
import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

/**
 * Sigar agent, extractor, loader.
 * 
 * To load as JVM agent:
 * 
 * <pre>
 * java -javaagent:/path/to/kamon-sigar-loader.jar=kamon.sigar.folder=/path/to/sigar/extract/folder ...
 * </pre>
 */
public class SigarAgent {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(SigarAgent.class
			.getName());

	/** Agent command line options. */
	private static volatile String options;

	/** Agent injected JVM instrumentation. */
	private static volatile Instrumentation instrumentation;

	/**
	 *  Contract: starting agents after JVM startup.
	 *
	 * @param options Agent command line options.
	 * @param instrumentation Injected JVM instrumentation instance.
	 * @throws Exception the exception
	 */
	public static void agentmain(final String options,
			final Instrumentation instrumentation) throws Exception {
		logger.info("Sigar loader via agent-main.");
		configure(options, instrumentation);
	}

	/**
	 *  Contract: starting agents via command-line interface.
	 *
	 * @param options Agent command line options.
	 * @param instrumentation Injected JVM instrumentation instance.
	 * @throws Exception the exception
	 */
	public static void premain(final String options,
			final Instrumentation instrumentation) throws Exception {
		logger.info("Sigar loader via pre-main.");
		configure(options, instrumentation);
	}

	/**
	 *  Agent mode configuration.
	 *
	 * @param options Agent command line options.
	 * @param instrumentation Injected JVM instrumentation instance.
	 * @throws Exception the exception
	 */
	public static synchronized void configure(final String options,
			final Instrumentation instrumentation) throws Exception {

		if (SigarAgent.instrumentation != null) {
			logger.severe("Duplicate agent setup attempt.");
			return;
		}

		SigarAgent.options = options;
		SigarAgent.instrumentation = instrumentation;

		logger.info("Sigar loader options: " + options);

		final File folder = new File(SigarProvisioner.defaultLocation(options));

		SigarProvisioner.provision(folder);

	}

	/**
	 *  Agent command line options.
	 *
	 * @return Agent command line options. 
	 */
	public static String options() {
		return options;
	}

	/**
	 *  Injected JVM instrumentation instance.
	 *
	 * @return Injected JVM instrumentation instance.
	 */
	public static Instrumentation instrumentation() {
		return instrumentation;
	}

}
