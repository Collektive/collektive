---
title: Collektive & Alchemist Simulator
---

# How to simulate

## Reproduce with containers (recommended)

1. Install docker and docker-compose.
2. Run `docker-compose up`.

## Reproduce natively

1. Install a Gradle-compatible version of Java. Use the [Gradle/Java compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html) to learn which is the compatible version range. The version of Gradle used in this experiment can be found in the `gradle-wrapper.properties` file located in the `gradle/wrapper` folder.
2. Install the version of Python indicated in `.python-version` file (or use pyenv) and run:
```bash
pip install --upgrade pip
pip install -r requirements.txt
```
3. Launch either:
    - `./gradlew run<SimulationName>Batch` on Linux, MacOS, or Windows if a bash-compatible shell is available;
    - `gradlew.bat run<SimulationName>Batch` on Windows cmd.
4. Once the experiment is finished, the results will be available in the `data` folder if so indicated in the file `.yml` relating to the simulation launched.
5. Run the Python script in the `plotter` folder to generate the graphs from the data in the `data` folder.

## How to run `Tutorial > Basic Usage` examples

### Natively
You need to clone the [repository](https://github.com/Collektive/collektive-examples) on your pc, moving into the root folder and running the following command:

```bash
./gradlew run<ExampleName>Batch
```

Where `<ExampleName>` is the name of the example you want to run in batch mode.

#### Running graphical simulations

It is possible to run also the graphical simulations with the [Alchemist simulator](https://alchemistsimulator.github.io).

You can list the available simulations by running the following command:

```bash
./gradlew tasks --all
```

And it will list all the available tasks, including the ones for the graphical simulations in the section "Run Alchemist tasks", or:

```bash
./gradlew run<ExampleName>Graphic
```

Where `<ExampleName>` is the name of the example you want to run in Graphic mode.

*Note: to run a program with the Graphics mode, it is essential that the environment variable `env:CI` is set to false.*

This variable determines whether the system is operating in a Continuous Integration (CI) environment. If the variable is set to true, certain arguments in the YAML files defined in the build.gradle are overridden. For instance, [in this case](https://github.com/angelacorte/collektive-stdlib-simulations/blob/c0730883e27299c7bb7daa5ea86035c77965bb26/build.gradle.kts#L108), a termination condition for the simulation is added after 2 simulated seconds. 

To configure the environment variable, the following command is used:

#### Linux or MacOS
```bash
export CI=true
```
```bash
export CI=false
```

#### Windows
```power-shell
$env:CI="true" 
```
```power-shell
$env:CI="false" 
```

## How to add new task

To add a new experiment, simply create a new `.yml` file in the same folder as the other experiments. Gradle will automatically generate the tasks required to run the experiment.