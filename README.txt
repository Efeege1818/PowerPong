# ConnectFourToxic

ConnectFourToxic is a digital two-player Connect Four game enhanced with an additional strategic rule: **toxic fields**.

## Team
- Muhammed Yunus Guelbasar  
- Can Avsar  
- Tibet Tuerkmen  
- Anxhela Alimadhi  

---

## Basic Idea

The goal is to be the first player to align four of their own chips horizontally, vertically, or diagonally.

The board is **6 rows × 7 columns**. Players take turns dropping a chip into a column. The chip falls to the lowest free position.

### Toxic Fields & Decay Mechanism
- At the start of the game, some fields are randomly marked as **toxic** (visible to both players).
- If a chip lands on a toxic field, a **decay timer** starts.
- After each move, all decay timers on toxic chips decrease by 1.
- When a timer reaches 0, the chip **disappears** and chips above fall down (**gravity**).

---

## Requirements

### Must-Have
- Drop a chip into a chosen column
- Retrieve the full board state
- Detect win conditions
- Randomly initialize toxic fields at game start
- Decrease decay timers and remove expired chips
- Apply gravity after decay (chips fall down)
- Alternate turns between players
- Reset the game to the initial state

### Nice-to-Have
- Visual effects for decaying chips
- Difficulty modes / adjustable toxicity levels
- Multiplayer / online mode

---

## Project Structure

- `apis/` – public interfaces & API contracts
- `components/` – game logic implementation (service, rules, toxic/decay)
- `ui/` – JavaFX demo UI to showcase gameplay & toxic effects

---

## How to Run (JavaFX Demo UI)

### Run UI
```bash
./gradlew :ui:run
