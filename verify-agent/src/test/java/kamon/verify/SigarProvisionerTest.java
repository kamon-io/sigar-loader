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

package kamon.verify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.LogManager;

import kamon.sigar.SigarProvisioner;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/** Verify sigar java agent operation. */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SigarProvisionerTest {

	static {
		/** Redirect JUL to SLF4J. */
		LogManager.getLogManager().reset();
		SLF4JBridgeHandler.install();
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void V1_Provisioned_By_Java_Agent() throws Exception {

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
	public void V2_Provision_Again() throws Exception {

		assertTrue(SigarProvisioner.isNativeLoaded());

		SigarProvisioner.provision(new File("target/native1"));
		SigarProvisioner.provision(new File("target/native2"));
		SigarProvisioner.provision(new File("target/native3"));

	}

	@Test
	public void V3_Multiple_Sigar_Instances() throws Exception {

		assertTrue(SigarProvisioner.isNativeLoaded());

		final Sigar sigar1 = new Sigar();
		final Sigar sigar2 = new Sigar();
		final Sigar sigar3 = new Sigar();

		final long pid1 = sigar1.getPid();
		final long pid2 = sigar2.getPid();
		final long pid3 = sigar3.getPid();

		assertTrue(pid1 > 0);
		assertTrue(pid2 > 0);
		assertTrue(pid3 > 0);

		assertEquals(pid1, pid2);
		assertEquals(pid2, pid3);
		assertEquals(pid3, pid1);

		sigar1.close();
		sigar2.close();
		sigar3.close();

	}

}
