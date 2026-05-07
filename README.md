# Simon - Android Game

A variant of the classic **Simon** game, developed as a native Android application for the "Embedded Systems Programming" course (2025-26).

---

## List of Requirements (Summary)

### 1. Main Game Screen
* [x] **Color Matrix (3x2)**: A fixed grid featuring Red (`R`), Green (`G`), Blue (`B`), Magenta (`M`), Yellow (`Y`), and Cyan (`C`).
* [ ] **Dynamic Display Area**: A multiline text area that displays the sequence of buttons pressed using their initials. The text is color-coded to match the corresponding buttons.
* [x] **Button Logic**:
    * `Start Game`: Initializes the session and plays the first sequence.
    * `Pause`: Suspends the game and displays the current sequence without replaying it from the beginning.
    * `End Game`: Terminates the session and saves the progress to history without recording a mistake.
* [x] **Sequence Progression**: Round by round, the game displays an increasingly long sequence (incremented by `1`) that the player must replicate.
* [x] **Audio Feedback**: Colored buttons produce distinct tones to assist with memorization; functional buttons (Start, Pause, End) remain silent.

### 2. Games History Screen
* [x] **Dynamic List**: Displays all recorded game sessions. If a game ended due to an error, the incorrect move is highlighted.
* [x] **Top 3 Scores**: The three highest scores are prominently displayed at the top of the screen.
* [ ] **Item Details**: Each entry shows the numerical score (total correct presses) on the left and the full sequence on the right. Long sequences are truncated with an ellipsis (`...`) but can be expanded by tapping the item.
* [x] **Navigation**: The system "Back" button or a dedicated menu button returns the user to the Main Screen.

### 3. Settings Screen
* [x] **Sound Effects**: A slider to adjust the application volume.
* [x] **Game Speed**: A slider with discrete values (`0.25x`, `0.5x`, `1.0x`, `2.0x`, `4.0x`) to control the playback speed of the visual sequence.
* [ ] **Consecutive Repetitions**: A toggle (checkbox) to enable or disable the occurrence of the same color appearing twice in a row.
* [x] **Accessibility & Customization**:
    * `Colorblind Mode`: Toggles visible initials on the colored buttons.
    * `Language`: Switches the UI between Italian (`IT`) and English (`EN`).
    * `Theme`: Allows the user to select between Light and Dark modes.
* [x] **Data Management**:
    * `Reset to Default`: Restores all original settings.
    * `Delete All`: Clears the entire game history (requires a second tap on a `Confirm` button).

### 4. Layout and Localization
* [ ] **Adaptive Layout**: Optimized UI structures for both Portrait and Landscape orientations, ensuring the design scales across various screen dimensions.
* [x] **Localization**: Full support for Italian (`IT`) and English (`EN`).
* [x] **State Preservation**: The current game state and sequence are preserved during configuration changes (e.g., screen rotation).
* [ ] **Persistent Storage**: All game history data is managed and saved using a `SQLiteDatabase`.
