# Documentazione

## Autori

- Mattia Senni (mattia.senni@studio.unibo.it)
- Fabio Fattori (fabio.fattori3@studio.unibo.it)
- Francesco Tonelli (francesco.tonelli9@studio.unibo.it)

## Esercizio 1

### Analisi

Il sistema può essere descritto, almeno a livello della centralina, come una macchina a stati, la cui coordinazione avviene però tramite invii asincroni di messaggi e stati locali degli attori coinvolti (in questo caso i sensori, il tastierino e la centralina stessa). 
- La centralina elabora un messaggio alla volta nella propria mailbox; 
- I messaggi vengono elaborati in ordine FIFO;
- I ritardi sono rappresentati da messaggi programmati, evitando di bloccare un thread durante l'attesa.

### Sviluppo

`ControlUnitActor` conserva lo stato globale, il PIN, i ritardi e l'associazione tra zone e sensori. 
- usa `become` per installare gli handler ammessi in ciascuno stato;
- limita le modifiche alla configurazione dei delay solo in stato `DISARMED`, per evitare casi di inconsistenza;
- accetta ritardi di uscita tra 20 e 60 secondi e di ingresso tra 10 e 30 secondi, come indicato dalla consegna;
- valida i nomi delle nuove zone, rendendoli univoci;
- elimina solo zone vuote, sempre per evitare inconsistenze.

Ogni sensore è un `SensorActor` e inoltra le rilevazioni al proprio `ZoneActor`. 
- una zona attiva le inoltra alla centrale, che controlla anche che il mittente appartenga a una delle zone selezionate; 
- alla fine del delay di uscita la centrale invia `ActiveZone` alle sole zone scelte;
- il disarmo annulla i timer e invia `DeactiveZone` a tutte le zone; 
- lo spostamento di un sensore aggiorna la mappa della centrale e il riferimento alla zona conservato dal sensore.

`UIActor` collega la logica alla GUI JavaFX. 
- accoda con `stash` i messaggi ricevuti prima di `BindUi`;
- raccoglie le cifre del PIN, una per volta; 
- inoltra le richieste alla centrale; 
- gestisce le modifiche ai controlli grafici passando da `Platform.runLater`.

Per quanto riguarda la GUI, infine:
- gli aggiornamenti della mappa con le zone e i sensori contengono copie immutabili, così non modifica lo stato della centrale;
- in stato di `ALARM`, l'indicatore in alto a destra lampeggia di rosso per simulare la sirena.

## Esercizio 2

### Analisi

In questo problema, la principale difficoltà a livello di concorrenza è far scambiare ai due avversari le proprie mosse senza blocchi reciproci, coordinando i vincitori per formare le coppie del turno successivo. Di conseguenza: 
- ogni coppia deve completare lo scambio di scelta e numero prima di calcolare l'esito;
- i canali non bufferizzati richiedono un ordine complementare tra invio e ricezione;
- i giocatori che proseguono devono conoscere i risultati delle altre partite del turno per individuare il prossimo avversario.

### Sviluppo

Di seguito, vengono elencati gli aspetti principali dell'applicativo sviluppato:
- il numero di match è fissato dal codice, a riga 117, dalla variabile `m`, la quale è però modificabile a piacimento per variare numero di turni e giocatori;
- il programma calcola il numero di giocatori, crea i canali e avvia una goroutine per ciascun Player; 
- scelte, numeri ed esiti sono comunicati tramite canali, senza condividere lo stato delle partite;
- i canali `players` sono non bufferizzati. Il giocatore con ID minore invia per primo, mentre l'altro riceve per primo, evitando lo stallo;
- i due giocatori scelgono parità opposte e scambiano un numero casuale tra 0 e 5. La parità della somma determina il vincitore;
- il vincitore invia un `MatchResult` agli altri giocatori e raccoglie gli esiti delle restanti partite del turno. I canali `matchResults` sono bufferizzati perché i giocatori eliminati non ricevono più messaggi;
- a ogni turno la dimensione del gruppo raddoppia. Il vincitore cerca nei risultati l'avversario della metà opposta del gruppo, mentre chi perde termina la propria esecuzione;
- dopo la finale, il vincitore invia il proprio ID sul canale `done`. Il processo principale riceve l'ID e stampa il risultato.
