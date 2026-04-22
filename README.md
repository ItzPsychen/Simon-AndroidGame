# Simon - Android Game

Variante del gioco **Simon**, sviluppato come applicazione usando Android Studio.

## Requisiti della Consegna

### 1. Schermata 1: Input Sequenza
* [x] **Matrice Colori (3x2)**: Griglia fissa con i colori Rosso (R), Verde (G), Blu (B), Magenta (M), Giallo (Y) e Ciano (C).
* [x] **Display Dinamico**: Area di testo colorato che mostra la sequenza premuta usando le iniziali inglesi (`R, G, B, M, Y, C`)
* [x] **Logica Pulsanti**:
    * **Cancella**: Azzera la sequenza corrente e pulisce l'area di testo.
    * **Fine Partita**: Termina la sequenza memorizzandola ed eliminandola.

### 2. Schermata 2: Storico Partite
* [x] **Lista Dinamica**: Mostra l'elenco di tutte le sequenze create fino a quel momento.
* [x] **Dettagli Elemento**:
    * A sinistra: Numero di rettangoli premuti.
    * A destra: La sequenza completa (trocata con `...` se necessario).
* [x] **Troncamento Grafico**: Le sequenze troppo lunghe vengono troncate automaticamente con un indicatore visivo.
* [x] **Navigazione**: Il tasto "Back" di sistema (o lo stesso tasto usato) riporta alla Schermata 1 per iniziare una nuova sequenza.

### 3. Layout Adattivo (Responsività)
L'app cambia struttura in base all'orientamento del dispositivo:
* [x] **Portrait (Verticale)**: Dall'alto l'Area di Testo, la Matrice 3x2 di pulsanti e i Pulsanti.
* [x] **Landscape (Orizzontale)**: Da sinistra la Matrice rimane 3x2, seguita dall'Area Testo e i Pulsanti sulla destra.

### 4. Localizzazione
Supporto multilingua che prevede la possibilità di essere cambiato a piacimento tra:
* [x] IT **Italiano**
* [x] EN **Inglese**

### 5. Gestione dello Stato (Instance State)
L'applicazione gestisce correttamente la rotazione dello schermo:
* La sequenza in corso nella Schermata 1 viene preservata durante il cambio di orientamento.
* La lista delle partite nella Schermata 2 rimane disponibile finché l'app non viene terminata.
* *Nota: i dati non si azzerano alla chiusura definitiva dell'app.*
