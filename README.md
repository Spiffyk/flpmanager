# FLP Manager

A simple workspace-based manager for Image-Line's FL Studio project files written in Java using the JavaFX GUI toolkit.

The project has started as a seminar work for the *Introduction to User Interfaces* subject at the *University of West Bohemia*.

## Usage

### Pre-requisites

 * **Java 8** (or higher)
 * **JavaFX 8** (on Windows, this is normally bundled with Java installation)
 * **FL Studio** (it runs without it but is not of much use then)

### Running

Once the `.jar` file is downloaded, on *Windows*, double-clicking it should be enough to run it. On **nix* systems (like *Linux* or *Mac OS*) you may need to set `chmod +x` for the file to be able to run.

## Development

### Pre-requisites

 * Everything that is needed to run the program
 * **Apache Maven**
 
### Building

To build the project, run:

```
$ mvn clean install -U
$ mvn compile
```

To package the project as a `.jar`, run:

```
$ mvn package
```

The executable `.jar` is then located at `flpmanager/bundle/target/flpmanager-<version>-r<revision>.jar`.

## Legal

The software is distributed under the MIT License.

FLP Manager is in no way affiliated with or endorsed by Image-Line Software.
