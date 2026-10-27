# Esercizio 1 - Smart Home Alarm System

## Analisi del problema

Il sistema coordina eventi dei sensori, comandi del tastierino e scadenze temporali. Una rilevazione produce effetti diversi secondo lo stato corrente: viene ignorata in DISARMED e EXIT_DELAY, avvia ENTRY_DELAY in ARMED e non modifica il conto alla rovescia in ENTRY_DELAY. La scadenza di ingresso porta ad ALARM. Il PIN corretto avvia l'inserimento da DISARMED e disinserisce il sistema dagli altri stati.

Le rilevazioni, il PIN e i timeout possono arrivare contemporaneamente. La centrale li elabora in ordine di mailbox, senza bloccare il dispatcher durante le attese. Se PIN e scadenza sono concorrenti, l'ordine di elaborazione decide se si passa prima dall'allarme; il PIN corretto consente comunque di tornare a DISARMED.

## Strategia ad attori

ControlUnitActor conserva lo stato globale, il PIN, i ritardi e l'associazione fra sensori e zone. Usa become per installare il comportamento corrispondente ai cinque stati. Ogni zona è un ZoneActor e ogni sensore è un SensorActor, entrambi figli della centrale. Un sensore inoltra la rilevazione alla propria zona; soltanto una zona attiva la inoltra alla centrale. La centrale verifica anche che il mittente sia una delle zone selezionate.

La configurazione viene copiata all'avvio. Gli aggiornamenti inviati alla GUI contengono copie immutabili delle mappe e delle liste. Lo stato degli attori resta confinato alle rispettive mailbox. UIActor raccoglie le cinque cifre del PIN e inoltra i comandi; gli aggiornamenti dei controlli JavaFX vengono eseguiti tramite Platform.runLater sul thread della GUI. Prima del collegamento dei controlli, UIActor accoda i messaggi con stash.

## Ritardi e coerenza della configurazione

Lo scheduler Pekko invia un messaggio alla centrale allo scadere di ciascun ritardo. I valori iniziali sono 30 secondi; l'uscita è configurabile tra 20 e 60 secondi e l'ingresso tra 10 e 30 secondi. Il disinserimento cancella entrambi i timer. I timeout già accodati vengono elaborati secondo il comportamento corrente dell'attore, senza identificatori di generazione.

Le richieste di creazione, eliminazione e riassegnazione, insieme alle modifiche dei ritardi, hanno un handler soltanto in DISARMED e vengono ignorate negli altri stati. La configurazione rimane così stabile durante un ciclo di sorveglianza. Nomi non validi o duplicati non creano attori. Una zona può essere eliminata soltanto se vuota. Lo spostamento di un sensore aggiorna sia la mappa della centrale sia il riferimento alla zona conservato dal sensore; i successivi messaggi della centrale verso quel sensore rispettano l'ordine di invio.

## Inserimento totale e parziale

La GUI propone una casella per ciascuna zona e un comando per selezionarle tutte. Il PIN corretto inserisce esclusivamente le zone selezionate; una selezione vuota viene rifiutata. Durante EXIT_DELAY la centrale conserva la selezione e le zone restano inattive. All'ingresso in ARMED invia ActiveZone alle sole zone selezionate e aggiorna la GUI, che le mostra come Active. Al disinserimento tutte le zone vengono disattivate, evitando che una selezione precedente rimanga attiva nel ciclo successivo.

Porte, finestre e rilevatori di movimento sono rappresentati da attori sensore con nomi distinti. Il menu di ciascun sensore permette di generare una rilevazione simulata. In ALARM l'indicatore diventa rosso e la GUI ripete un segnale acustico di sistema fino al PIN corretto. L'audio dipende dalle impostazioni del computer. La simulazione conserva la configurazione in memoria durante l'esecuzione.

## Esecuzione

Il progetto richiede JDK 21 e Maven. Il comando mvn javafx:run avvia l'applicazione; il PIN della simulazione è 12345. Il menu contestuale delle zone consente di attivare i sensori e spostarli. I controlli +/- configurano i ritardi.
