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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class JDK {

	public static void makeFieldPublicVariable(final Field field)
			throws Exception {

		field.setAccessible(true);

		final Field modifiersField = Field.class.getDeclaredField("modifiers");

		modifiersField.setAccessible(true);

		int mask = field.getModifiers();
		mask &= ~Modifier.FINAL;
		mask |= Modifier.PUBLIC;

		modifiersField.setInt(field, mask);

	}

	@SuppressWarnings("unchecked")
	public static <T> T getHiddenFiled(final Field field, final Object instance)
			throws Exception {

		makeFieldPublicVariable(field);

		return (T) field.get(instance);

	}

	public static void setHiddenFiled(final Field field, final Object instance,
			final Object value) throws Exception {

		makeFieldPublicVariable(field);

		field.set(instance, value);

	}

	public static Hashtable<String, URLStreamHandler> getHandlersFromURL()
			throws Exception {

		final Field field = URL.class.getDeclaredField("handlers");

		return getHiddenFiled(field, null);

	}

	public static void handlerAdd(final String protocol,
			final URLStreamHandler handler) throws Exception {

		final Hashtable<String, URLStreamHandler> handlers = getHandlersFromURL();

		handlers.put(protocol, handler);

	}

	public static void handlerRemove(final String protocol) throws Exception {

		final Hashtable<String, URLStreamHandler> handlers = getHandlersFromURL();

		handlers.remove(protocol);

	}

	public static void setEnv(final String name, final String value) {

		final Map<String, String> oldenv = System.getenv();

		final Map<String, String> newenv = new HashMap<String, String>();

		newenv.putAll(oldenv);

		if (value == null) {
			newenv.remove(name);
		} else {
			newenv.put(name, value);
		}

		setEnv(newenv);

	}

	public static void setEnv(final Map<String, String> newenv) {
		try {

			final Class<?> processEnvironmentClass = Class
					.forName("java.lang.ProcessEnvironment");

			final Field theEnvironmentField = processEnvironmentClass
					.getDeclaredField("theEnvironment");

			theEnvironmentField.setAccessible(true);

			@SuppressWarnings("unchecked")
			final Map<String, String> env = (Map<String, String>) theEnvironmentField
					.get(null);

			env.putAll(newenv);

			final Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
					.getDeclaredField("theCaseInsensitiveEnvironment");

			theCaseInsensitiveEnvironmentField.setAccessible(true);

			@SuppressWarnings("unchecked")
			final Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField
					.get(null);

			cienv.putAll(newenv);

		} catch (final NoSuchFieldException e) {
			try {

				final Class<?>[] classes = Collections.class
						.getDeclaredClasses();

				final Map<String, String> env = System.getenv();

				for (final Class<?> cl : classes) {

					if ("java.util.Collections$UnmodifiableMap".equals(cl
							.getName())) {

						final Field field = cl.getDeclaredField("m");

						field.setAccessible(true);

						final Object obj = field.get(env);

						@SuppressWarnings("unchecked")
						final Map<String, String> map = (Map<String, String>) obj;

						map.clear();

						map.putAll(newenv);

					}

				}
			} catch (final Exception e2) {
				e2.printStackTrace();
			}
		} catch (final Exception e1) {
			e1.printStackTrace();
		}

	}

}
