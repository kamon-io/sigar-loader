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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;
import java.util.logging.LogManager;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/** Verify sigar loader operation. */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SigarProvisionerTest {

	static {
		/** Redirect JUL to SLF4J. */
		LogManager.getLogManager().reset();
		SLF4JBridgeHandler.install();
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void Default_Locaion_Via_Command_Options() {
		final String folder = UUID.randomUUID().toString();
		final String options = SigarProvisioner.SYSTEM_PROPERTY + "=" + folder;
		assertEquals(SigarProvisioner.defaultLocation(options), folder);
	}

	@Test
	public void Default_Locaion_Via_Environment_Variable() {
		assertNull(System.getenv(SigarProvisioner.ENVIRONMENT_VARIABLE));
		final String variable = UUID.randomUUID().toString();
		JDK.setEnv(SigarProvisioner.ENVIRONMENT_VARIABLE, variable);
		assertEquals(SigarProvisioner.defaultLocation(null), variable);
		JDK.setEnv(SigarProvisioner.ENVIRONMENT_VARIABLE, null);
		assertNull(System.getenv(SigarProvisioner.ENVIRONMENT_VARIABLE));
	}

	@Test
	public void Default_Locaion_Via_System_Property() {
		assertNull(System.getProperty(SigarProvisioner.SYSTEM_PROPERTY));
		final String property = UUID.randomUUID().toString();
		System.setProperty(SigarProvisioner.SYSTEM_PROPERTY, property);
		assertEquals(SigarProvisioner.defaultLocation(null), property);
		System.clearProperty(SigarProvisioner.SYSTEM_PROPERTY);
		assertNull(System.getProperty(SigarProvisioner.SYSTEM_PROPERTY));
	}

	@Test(expected = Throwable.class)
	public void T1_Native_Is_Absent() throws Exception {
		final Sigar sigar = new Sigar();
		assertTrue(sigar.getPid() > 0);
		sigar.close();
	}

	@Test
	public void T2_Provision_Once() throws Exception {

		assertFalse(SigarProvisioner.isNativeLoaded());

		final File location = new File("target/native");
		SigarProvisioner.provision(location);

		assertTrue(SigarProvisioner.isNativeLoaded());

		final Sigar sigar = new Sigar();
		assertTrue(sigar.getPid() > 0);

		logger.info("Process id: {}", sigar.getPid());
		logger.info("Host FQDN: {}", sigar.getFQDN());
		logger.info("CPU info: {}", sigar.getCpu());
		logger.info("MEM info: {}", sigar.getMem());
		logger.info("CPU load: {}", sigar.getLoadAverage()[0]);

		final CpuPerc cpuPerc = sigar.getCpuPerc();
		logger.info("CPU combined: {}", cpuPerc.getCombined());
		logger.info("CPU stolen: {}", cpuPerc.getStolen());
		
		sigar.close();

	}

	@Test
	public void T3_Provision_Again() throws Exception {

		assertTrue(SigarProvisioner.isNativeLoaded());

		SigarProvisioner.provision(new File("target/native1"));
		SigarProvisioner.provision(new File("target/native2"));
		SigarProvisioner.provision(new File("target/native3"));

	}

}
