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
- una zona attiva le inoltra alla centrale, che controlla anche che il mittente appartenga alle zone selezionate; 
- alla fine del delay di uscita la centrale invia `ActiveZone` alle sole zone scelte;
- il disarmo annulla i timer e invia `DeactiveZone` a tutte le zone; 
- lo spostamento di un sensore aggiorna la mappa della centrale e il riferimento alla zona conservato dal sensore.

`UIActor` collega la logica alla GUI JavaFX. 
- accoda con `stash` i messaggi ricevuti prima di `BindUi`;
- raccoglie le cifre del PIN, una ad una; 
- inoltra le richieste alla centrale; 
- gestisce le modifiche ai controlli grafici passando da `Platform.runLater`; 

Per quanto riguarda la GUI, invece:
- gli aggiornamenti della mappa con le zone e i sensori contengono copie immutabili, così non modifica lo stato della centrale;
- in stato di `ALARM`, l'indicatore in alto a destra lampeggia di rosso per simulare la sirena

## Esercizio 2

### Analisi

### Sviluppo
