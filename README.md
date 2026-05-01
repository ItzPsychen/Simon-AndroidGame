# Simon - Android Game

A variant of the **Simon** game, developed as a native Android application for the "Embedded Systems Programming" course (2025-26).

---   

## 🛠 Requirements

### 1. Main Game Screen
* [x] **Color Matrix (3x2)**: Fixed grid with Red (`R`), Green (`G`), Blue (`B`), Magenta (`M`), Yellow (`Y`) and Cyan (`C`).
* [x] **Dynamic Display**: Text area showing the pressed sequence using English initials (also colored).
* [ ] **Button Logic**: `Start Game` (starts the game), `Pause` (pauses the game) and `End Game` (terminates and stores).
* [ ] **Show Game Sequence**: The game once started, round by round, shows a sequence that the player has to replicate (after each the sequence gets longer by `1`).
* [ ] **Button Sounds**: During the game, colored buttons make different sounds (useful to better memorize the sequence).

### 2. Games History Screen
* [ ] **Dynamic List**: Displays all created games, with the final error highlighted.
* [ ] ${\color{green}[extra]}$ **Top 3 Scores**: On top are displayed the best scores.
* [x] **Item Details**: Number of presses on the left, full sequence (truncated with `...`) on the right.
* [x] **Navigation**: System "Back" button returns to the main screen.

### 3. Settings Screen
* [x] ${\color{green}[extra]}$ **Sound Effects**: Slider that allows the user to set the volume.
* [x] ${\color{green}[extra]}$ **Functionalities**: `Colorblind Mode` enables the letters on each colored button, `Language` changes the language (`IT/EN`) and `Theme` allows to select light or dark mode.
* [x] ${\color{green}[extra]}$ **Action Buttons**: `Reset to Default` brings back the default settings and `Delete All` that clears all the game history (a confirmation pop-up would appear).

### 4. Layout and Localization
* [x] **Adaptive Layout**: Different structures for Portrait and Landscape modes.
* [x] **Localization**: Support for Italian (`IT`) and English (`EN`).
* [x] **Instance State**: Current sequence is preserved during screen rotation and other actions.
* [ ] **Game Data**: All data about the game history is saved using SqliteDatabase
