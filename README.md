# Project Background👨‍🎓
This project originated from a university project in which multiple groups each developed their own game.
At the end, all games were integrated into one larger application that made it possible to play them all in a single program.

I extracted our game, PowerPong, from that shared application and uploaded it here as a standalone project on GitHub to present the work our team contributed.
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
