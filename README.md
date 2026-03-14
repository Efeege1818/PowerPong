# PowerPong

This repository now builds PowerPong as its own standalone application.

## Modules

- `apis`: PowerPong API contracts and shared exceptions
- `components`: PowerPong game logic implementation
- `javafx`: PowerPong desktop application

Only the PowerPong source sets are compiled by Gradle.

## Run

```powershell
.\gradlew :javafx:run
```

## Build

```powershell
.\gradlew build
```
