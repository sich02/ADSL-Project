package it.unicam.cs.asdl2425.mp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;


/**
 * Una classe che rappresenta una lista concatenata con il calcolo degli hash
 * MD5 per ciascun elemento. Ogni nodo della lista contiene il dato originale di
 * tipo generico T e il relativo hash calcolato utilizzando l'algoritmo MD5.
 *
 * <p>
 * La classe supporta le seguenti operazioni principali:
 * <ul>
 * <li>Aggiungere un elemento in testa alla lista</li>
 * <li>Aggiungere un elemento in coda alla lista</li>
 * <li>Rimuovere un elemento dalla lista in base al dato</li>
 * <li>Recuperare una lista ordinata di tutti gli hash contenuti nella
 * lista</li>
 * <li>Costruire una rappresentazione testuale della lista</li>
 * </ul>
 *
 * <p>
 * Questa implementazione include ottimizzazioni come il mantenimento di un
 * riferimento all'ultimo nodo della lista (tail), che rende l'inserimento in
 * coda un'operazione O(1).
 *
 * <p>
 * La classe utilizza la classe HashUtil per calcolare l'hash MD5 dei dati.
 *
 * @param <T>
 *                il tipo generico dei dati contenuti nei nodi della lista.
 *
 * @author Luca Tesei, Marco Caputo (template) **SIMONE ANTONINI
 *         simone01.antonini@studenti.unicam.it ** (implementazione)
 *
 */
public class HashLinkedList<T> implements Iterable<T> {
    private Node head; // Primo nodo della lista

    private Node tail; // Ultimo nodo della lista

    private int size; // Numero di nodi della lista

    private int numeroModifiche; // Numero di modifiche effettuate sulla lista
    // per l'implementazione dell'iteratore
    // fail-fast

    public HashLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.numeroModifiche = 0;
    }

    /**
     * Restituisce il numero attuale di nodi nella lista.
     *
     * @return il numero di nodi nella lista.
     */
    public int getSize() {
        return size;
    }

    /**
     * Rappresenta un nodo nella lista concatenata.
     */
    private class Node {
        String hash; // Hash del dato

        T data; // Dato originale

        Node next;

        Node(T data) {
            this.data = data;
            this.hash = HashUtil.dataToHash(data);
            this.next = null;
        }
    }

    /**
     * Aggiunge un nuovo elemento in testa alla lista.
     *
     * @param data
     *                 il dato da aggiungere.
     */
    public void addAtHead(T data) {
        // Creiamo un nuovo nodo con il dato fornito
        Node newNode = new Node(data);
        // Se la lista è vuota, sia head che tail puntano al nuovo nodo
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            // Altrimenti, il nuovo nodo diventa la nuova head
            newNode.next = head;
            head = newNode;
        }
        // Aggiorniamo il contatore dei nodi e il numero di modifiche
        size++;
        numeroModifiche++;
    }

    /**
     * Aggiunge un nuovo elemento in coda alla lista.
     *
     * @param data
     *                 il dato da aggiungere.
     */
    public void addAtTail(T data) {
        // Creiamo un nuovo nodo con il dato fornito
        Node newNode = new Node(data);
        // Se la lista è vuota, sia head che tail puntano al nuovo nodo
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            // Altrimenti, colleghiamo il nuovo nodo alla fine della lista
            tail.next = newNode;
            tail = newNode;
        }
        // Aggiorniamo il contatore dei nodi e il numero di modifiche
        size++;
        numeroModifiche++;
    }

    /**
     * Restituisce un'ArrayList contenente tutti gli hash nella lista in ordine.
     *
     * @return una lista con tutti gli hash della lista.
     */
    public ArrayList<String> getAllHashes() {
        // Creiamo una nuova lista per memorizzare gli hash
        ArrayList<String> hashes = new ArrayList<>();
        // Iteriamo su tutti i nodi della lista
        for (Node current = head; current != null; current = current.next) {
            // Aggiungiamo l'hash del nodo corrente alla lista
            hashes.add(current.hash);
        }
        // Restituiamo la lista di hash
        return hashes;
    }

    /**
     * Costruisce una stringa contenente tutti i nodi della lista, includendo
     * dati e hash. La stringa dovrebbe essere formattata come nel seguente
     * esempio:
     *
     * <pre>
     *     Dato: StringaDato1, Hash: 5d41402abc4b2a76b9719d911017c592
     *     Dato: SteringaDato2, Hash: 7b8b965ad4bca0e41ab51de7b31363a1
     *     ...
     *     Dato: StringaDatoN, Hash: 2c6ee3d301aaf375b8f026980e7c7e1c
     * </pre>
     *
     * @return una rappresentazione testuale di tutti i nodi nella lista.
     */
    public String buildNodesString() {
        // Utilizziamo uno StringBuilder per costruire la rappresentazione testuale
        StringBuilder sb = new StringBuilder();
        // Iteriamo su tutti i nodi della lista
        for (Node current = head; current != null; current = current.next) {
            // Aggiungiamo i dati e gli hash dei nodi alla stringa
            sb.append("Dato: ").append(current.data)
                    .append(", Hash: ").append(current.hash).append("\n");
        }
        // Restituiamo la stringa costruita
        return sb.toString();
    }

    /**
     * Rimuove il primo elemento nella lista che contiene il dato specificato.
     *
     * @param data
     *                 il dato da rimuovere.
     * @return true se l'elemento è stato trovato e rimosso, false altrimenti.
     */
    public boolean remove(T data) {
        // Se la lista è vuota, restituiamo false
        if (head == null) return false;

        // Controlliamo se il dato da rimuovere è nella head
        if (head.data.equals(data)) {
            // Spostiamo head al nodo successivo
            head = head.next;
            size--;
            numeroModifiche++;
            // Se head diventa null, anche tail deve diventare null
            if (head == null) tail = null;
            return true;
        }

        // Iteriamo sulla lista per trovare il nodo da rimuovere
        Node current = head;
        while (current.next != null) {
            if (current.next.data.equals(data)) {
                // Colleghiamo il nodo corrente al successivo di quello da rimuovere
                current.next = current.next.next;
                size--;
                numeroModifiche++;
                // Se rimuoviamo l'ultimo nodo, aggiorniamo tail
                if (current.next == null) tail = current;
                return true;
            }
            current = current.next;
        }
        // Se non troviamo il dato, restituiamo false
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    /**
     * Classe che realizza un iteratore fail-fast per HashLinkedList.
     */
    private class Itr implements Iterator<T> {
        private Node current;
        private int expectedNumeroModifiche;

        private Itr() {
            // Inizializziamo il puntatore al primo nodo e il numero di modifiche attese
            current = head;
            expectedNumeroModifiche = numeroModifiche;
        }

        @Override
        public boolean hasNext() {
            // Verifichiamo se esiste un nodo successivo
            return current != null;
        }

        @Override
        public T next() {
            // Controlliamo se ci sono state modifiche concorrenti
            if (expectedNumeroModifiche != numeroModifiche) {
                throw new ConcurrentModificationException();

            }
            // Se non ci sono più elementi, solleviamo un'eccezione
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // Restituiamo il dato corrente e passiamo al nodo successivo
            T data = current.data;
            current = current.next;
            return data;
        }
    }
}
