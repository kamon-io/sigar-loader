
### Kamon Sigar Loader

Provides convenient self-contained [Sigar](https://github.com/hyperic/sigar) 
classes with native library deployment and provisioning mechanism with JDK-only dependencies
for the following common use cases:
* Java Agent: automatic extract/load at JVM start time
* Programmatically: embedded library extraction
* Framework Contract: OSGI bundle activation

#### To load as JVM java agent:
```
#
# Extract to default location: ${user.dir}/native
java -javaagent:/path/to/sigar-loader.jar ...

#
# Extract to provided library extract location.
java -javaagent:/path/to/sigar-loader.jar=kamon.sigar.folder=/path/to/library/extract/folder ...
```
Sigar loader agent can also be provided via
[JVM attach API](https://docs.oracle.com/javase/7/docs/jdk/api/attach/spec/com/sun/tools/attach/VirtualMachine.html)

#### To load programmatically from your code:
```
// Required imports.
		import java.io.File;
		import org.hyperic.sigar.Sigar;
		import kamon.sigar.SigarProvisioner;

// Extract to default location: ${user.dir}/native 
		SigarProvisioner.provision();
		final Sigar sigar = new Sigar();

// Extract to user provided library extract location.
		final File location = new File("target/native");
		SigarProvisioner.provision(location);
		final Sigar sigar = new Sigar();
```

#### To load during OSGI bundle activation:
```
karaf@root()> bundle:install mvn:io.kamon/sigar-loader/1.6.5-rev001
```

#### Default extract location

Default library extract location used by
* ```java -javaagent:/path/to/sigar-loader.jar``` and
* ```SigarProvisioner.provision()```

will be selected in the following priority order:
 1. environment variable ```KAMON_SIGAR_FOLDER```
 2. system property ```kamon.sigar.folder```
 3. hard coded ```${user.dir}/native```

#### Extract location in OSGI

Bundle activator will use framework persistent bundle storage location.

#### Repeated provision attempts

Sigar loader ensures that native library is loaded exactly once.
