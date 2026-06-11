# AutoSecMut

AutoSecMut is a tool for mutating Spring Security configurations, enabling the analysis and mutation of security-related settings in Java applications.

## Build

To build the project, execute:

```bash
mvn clean package
```

## Execution

After building the project, execute the generated JAR file and provide the path to the target repository as an argument:

```bash
java -jar target/mutate_security_configs-1.0-SNAPSHOT.jar <repository-path>
```

Example:

```bash
java -jar target/mutate_security_configs-1.0-SNAPSHOT.jar /path/to/repository
```

## Verbose Mode

Verbose output can be enabled using the `-v` flag:

```bash
java -jar target/mutate_security_configs-1.0-SNAPSHOT.jar <repository-path> -v
```

When enabled, the tool provides detailed information about each processing step during execution.

## Report Generation

After execution, a report file is automatically generated in the root directory of the analyzed repository.

Report files are named using a timestamp to avoid overwriting previous executions:

```text
dd-MM-yyyy-HH-mm-ss
```

Example:

```text
11-06-2026-14-30-45
```

# The experimental environment

The experimental environment was automatically configured using a Bash script. The script installed Eclipse Temurin JDK 21, Git, Maven, and Gradle through the Debian package manager and the Adoptium repository. It also configured the JAVA\_HOME environment variable and updated the system PATH accordingly. After installation, the script collected information about the software and hardware environment, including Java, Maven, and Gradle versions, operating system details, CPU specifications, and memory configuration. The environment setup script was executed using the following commands:

chmod +x setup.sh

./setup.sh

For each subject repository, the test suite was executed before running the mutation analysis. The execution command depended on the build system adopted by the project (e.g., Maven or Gradle).

To prevent interruptions caused by network failures or terminal timeouts, all experiments were executed within a GNU Screen session using the command `screen -S tcc-run`.

The execution time of AutoMutSec was measured using the Linux `time` utility. The following command was executed:

`\{ time java -jar target/mutate\_security\_configs-1.0-SNAPSHOT.jar <repository-path>; \} 2> time.txt`

where `target/mutate\_security\_configs-1.0-SNAPSHOT.jar` corresponds to the compiled AutoMutSec artifact and `<repository-path>` is the path to the target repository. The execution time was recorded in the file `time.txt`.

For comparison with PIT, the mutation analysis was executed using the following command:

`\{ time mvn -Ppitest clean test org\.pitest\:pitest-maven:mutationCoverage\; \} 2> time.txt`

The PIT dependency was added to each project before execution. The execution time was collected using the same procedure adopted for AutoMutSec.

Code coverage metrics were obtained from JaCoCo reports generated during test execution whenever JaCoCo was already configured as a project dependency. The instruction and line coverage values reported by JaCoCo were used in the analysis.
