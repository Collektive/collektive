---
title: Collektive with Alchemist Simulator
---

# How to simulate

## Reproduce with containers (not recommended with the graphical simulation)

1. Install docker and docker-compose.
2. Run `docker-compose up`.

TODO explanation of next steps

## Reproduce natively

1. Install a Gradle-compatible version of Java. Use the [Gradle/Java compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html) to learn which is the compatible version range. The version of Gradle used in this experiment can be found in the `gradle-wrapper.properties` file located in the `gradle/wrapper` folder.
2. Install the version of Python indicated in `.python-version` file (or use pyenv) and run:
    ```bash
    pip install --upgrade pip
    pip install -r requirements.txt
    ```
    (only needed for the data plotter).
3. Launch either:
    - `./gradlew run<SimulationName>Batch` on Linux, MacOS, or Windows if a bash-compatible shell is available;
    - `gradlew.bat run<SimulationName>Batch` on Windows cmd.
4. Once the experiment is finished, the results will be available in the `data` folder if the alchemist extractors are defined in the YAML configuration file.
5. Run the Python script in the `plotter` folder to generate the graphs from the data in the `data` folder.

## How to run `Tutorial > Basic Usage` example

### Natively
You need to clone the [repository](https://github.com/Collektive/collektive-examples) on your pc, moving into the root folder and running the following command:

```bash
./gradlew run<ExampleName>Batch
```

Where `<ExampleName>` is the name of the example you want to run in batch mode.

#### Running graphical simulations

It is possible to run also the graphical simulations with the Alchemist simulator.

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

### With containers

TODO 

## How to add new task

To add a new experiment, simply create a new `.yml` file in the same folder as the other experiments. Gradle will automatically generate the tasks required to run the experiment.

The [Using Collektive with Alchemist Simulator](tutorials/collektive-and-alchemist.mdx) section illustrates the process of creating a YAML configuration file and using it with Collektive. 

For more details, refer to the [Alchemist Simulator](https://alchemistsimulator.github.io/) documentation.