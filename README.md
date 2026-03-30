# Simon - Android Prototype 🎮

Prototipo di una variante del gioco **Simon**, sviluppato come applicazione usando Android Studio.

## Requisiti della Consegna

### 1. Schermata 1: Input Sequenza
* **Matrice Colori (3x2)**: Griglia fissa con i colori Rosso (R), Verde (G), Blu (B), Magenta (M), Giallo (Y) e Ciano (C).
* **Display Dinamico**: Area di testo multiriga che mostra la sequenza premuta usando le iniziali inglesi (`R, G, B, M, Y, C`)
* **Logica Pulsanti**:
    * **Cancella**: Azzera la sequenza corrente e pulisce l'area di testo.
    * **Fine Partita**: Termina la sequenza (anche se vuota/zero elementi), la memorizza e apre la Schermata 2.

### 2. Schermata 2: Storico Partite
* **Lista Dinamica**: Mostra l'elenco di tutte le partite concluse dall'avvio dell'app.
* **Dettagli Elemento**:
    * A sinistra: Numero di rettangoli premuti.
    * A destra: La sequenza completa.
* **Troncamento Grafico**: Le sequenze troppo lunghe vengono troncate automaticamente con un indicatore visivo.
* **Navigazione**: Il tasto "Back" di sistema riporta alla Schermata 1 per iniziare una nuova partita.

### 3. Layout Adattivo (Responsività)
L'app cambia struttura in base all'orientamento del dispositivo:
* **Portrait (Verticale)**: Matrice, Area Testo e Pulsanti sono impilati verticalmente.
* **Landscape (Orizzontale)**: La Matrice rimane 3x2 sulla sinistra, mentre l'Area Testo e i Pulsanti si dispongono sulla destra.

### 4. Localizzazione
Supporto multilingua completo per:
* IT **Italiano**
* EN **Inglese**

### 5. Gestione dello Stato (Instance State)
L'applicazione gestisce correttamente la rotazione dello schermo:
* La sequenza in corso nella Schermata 1 viene preservata durante il cambio di orientamento.
* La lista delle partite nella Schermata 2 rimane disponibile finché l'app non viene terminata.
* *Nota: i dati si azzerano alla chiusura definitiva dell'app.*
