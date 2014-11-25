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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Sigar OSGI bundle activator.
 * 
 * Provision native sigar library in the persistent bundle storage location.
 */
public class SigarActivator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		final File storage = context.getDataFile("");
		final File location = new File(storage, SigarProvisioner.LIB_DIR);
		SigarProvisioner.provision(location);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
	}

}
