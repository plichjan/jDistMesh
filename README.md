# jDistMesh

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.22-blue.svg)](https://kotlinlang.org/)

**jDistMesh** is a Kotlin implementation of [DistMesh](http://persson.berkeley.edu/distmesh/) – a simple generator of unstructured triangular meshes. 

Originally developed as a Java port for a diploma thesis at CTU FEL, this version has been modernized to Kotlin and uses Gradle for build management.

## 🚀 Quick Start

### Prerequisites
- JDK 21 or newer

### Build and Run
To launch the GUI viewer:
```bash
./gradlew run
```

To run tests:
```bash
./gradlew test
```

## ✨ Features
- **2D Mesh Generation**: Pure Kotlin implementation of the DistMesh2D algorithm.
- **Interactive Viewer**: GUI for visualizing the triangulation process in real-time.
- **Scripting Support**: Ability to define mesh parameters (distance functions, edge length functions) using Kotlin Scripts (`.main.kts`).
- **Delaunay Triangulation**: Includes a custom 2D Delaunay implementation.

## 📂 Project Structure
- `src/main/kotlin`: Core algorithm and GUI viewer.
- `src/main/scripts`: Example mesh definitions using Kotlin Script.
- `src/main/matlab`: Original MATLAB implementation for reference.

## 🛠 Scripting Interface
The application can execute external scripts to define complex geometries. Example scripts can be found in `src/main/scripts/`.

## 📜 License
This project is licensed under the **MIT License**. See [LICENSE.txt](LICENSE.txt) for details.

## 🙏 Acknowledgments
This project is a port of the original **DistMesh** by Per-Olof Persson and Gilbert Strang.
- Original MATLAB code: [persson.berkeley.edu/distmesh/](http://persson.berkeley.edu/distmesh/)
- Delaunay Triangulation: Derived from Paul Chew's implementation.
