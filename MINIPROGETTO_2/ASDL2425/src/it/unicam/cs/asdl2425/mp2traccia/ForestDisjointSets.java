package it.unicam.cs.asdl2425.mp2traccia;

import java.security.KeyStore.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Implementazione dell'interfaccia <code>DisjointSets<E></code> tramite una
 * foresta di alberi ognuno dei quali rappresenta un insieme disgiunto. Si
 * vedano le istruzioni o il libro di testo Cormen et al. (terza edizione)
 * Capitolo 21 Sezione 3.
 *
 * @author Antonini Simone
 *
 * @param <E>
 *                il tipo degli elementi degli insiemi disgiunti
 */
public class ForestDisjointSets<E> implements DisjointSets<E> {

    /*
     * Mappa che associa ad ogni elemento inserito il corrispondente nodo di un
     * albero della foresta. La variabile è protected unicamente per permettere
     * i test JUnit.
     */
    protected Map<E, Node<E>> currentElements;

    /*
     * Classe interna statica che rappresenta i nodi degli alberi della foresta.
     * Gli specificatori sono tutti protected unicamente per permettere i test
     * JUnit.
     */
    protected static class Node<E> {
        /*
         * L'elemento associato a questo nodo
         */
        protected E item;

        /*
         * Il parent di questo nodo nell'albero corrispondente. Nel caso in cui
         * il nodo sia la radice allora questo puntatore punta al nodo stesso.
         */
        protected Node<E> parent;

        /*
         * Il rango del nodo definito come limite superiore all'altezza del
         * (sotto)albero di cui questo nodo è radice.
         */
        protected int rank;

        /**
         * Costruisce un nodo radice con parent che punta a se stesso e rango
         * zero.
         *
         * @param item
         *                 l'elemento conservato in questo nodo
         *
         */
        public Node(E item) {
            this.item = item;
            this.parent = this;
            this.rank = 0;
        }

    }

    /**
     * Costruisce una foresta vuota di insiemi disgiunti rappresentati da
     * alberi.
     */
    public ForestDisjointSets() {

        currentElements = new HashMap<>();
    }

    /**
     * Verifica se l'elemento specificato è presente nella collezione.
     * La presenza dell'elemento è determinata dalla sua esistenza come chiave
     * nella mappa currentElements.
     *
     * @param e L'elemento da verificare.
     * @return true se l'elemento è presente nella collezione, false altrimenti.
     * @throws NullPointerException Se l'elemento passato è nullo.
     */
    @Override
    public boolean isPresent(E e) {
        if (e == null) {
            throw new NullPointerException("L'elemento non può essere nullo.");
        }

        return this.currentElements.containsKey(e);
    }


    /*
     * Crea un albero della foresta consistente di un solo nodo di rango zero il
     * cui parent è se stesso.
     */

    /**
     * Crea un nuovo insieme disgiunto contenente l'elemento specificato.
     * Ogni elemento è rappresentato da un nodo con rank iniziale pari a 0,
     * e il nodo è impostato come proprio genitore (parent).
     *
     * @param e L'elemento da aggiungere come nuovo insieme.
     * @throws NullPointerException Se l'elemento passato è nullo.
     * @throws IllegalArgumentException Se l'elemento è già presente.
     */
    @Override
    public void makeSet(E e) {
        if (e == null) {
            throw new NullPointerException("L'elemento non può essere nullo.");
        }
        if (isPresent(e)) {
            throw new IllegalArgumentException("L'elemento è già presente nell'insieme.");
        }
        Node<E> n = new Node<>(e);
        n.rank = 0;
        n.parent = n;
        this.currentElements.put(e, n);
    }


    /*
     * L'implementazione del find-set deve realizzare l'euristica
     * "compressione del cammino". Si vedano le istruzioni o il libro di testo
     * Cormen et al. (terza edizione) Capitolo 21 Sezione 3.
     */

    /**
     * Trova e restituisce il rappresentante dell'insieme a cui appartiene l'elemento specificato.
     * L'algoritmo utilizza la tecnica del path compression per ottimizzare la struttura del grafo
     * durante il processo di ricerca.
     *
     * @param e L'elemento di cui trovare il rappresentante.
     * @return Il rappresentante dell'insieme se l'elemento è presente; null se l'elemento non è presente.
     * @throws NullPointerException Se l'elemento passato è nullo.
     */
    @Override
    public E findSet(E e) {
        if (e == null) {
            throw new NullPointerException("L'elemento non può essere nullo.");
        }
        if (!isPresent(e)) {
            return null;
        }

        Node<E> node = this.currentElements.get(e);
        Node<E> rep = getRepresentative(node);
        node.parent = rep;
        return rep.item;
    }


    /*
     * L'implementazione dell'unione deve realizzare l'euristica
     * "unione per rango". Si vedano le istruzioni o il libro di testo Cormen et
     * al. (terza edizione) Capitolo 21 Sezione 3. In particolare, il
     * rappresentante dell'unione dovrà essere il rappresentante dell'insieme il
     * cui corrispondente albero ha radice con rango più alto. Nel caso in cui
     * il rango della radice dell'albero di cui fa parte e1 sia uguale al rango
     * della radice dell'albero di cui fa parte e2 il rappresentante dell'unione
     * sarà il rappresentante dell'insieme di cui fa parte e2.
     */

    /**
     * Unisce due elementi specificati in un'unica componente connessa.
     * Se gli elementi appartengono a componenti diverse, queste vengono unite,
     * aggiornando il rappresentante e il rank degli elementi coinvolti.
     *
     * @param e1 Il primo elemento da unire.
     * @param e2 Il secondo elemento da unire.
     * @throws NullPointerException Se uno o entrambi gli elementi passati sono nulli.
     * @throws IllegalArgumentException Se uno o entrambi gli elementi non sono presenti nella struttura.
     */
    @Override
    public void union(E e1, E e2) {

        if (e1 == null || e2 == null) {
            throw new NullPointerException("Gli elementi non possono essere nulli.");
        }


        if (!(isPresent(e1) && isPresent(e2))) {
            throw new IllegalArgumentException("Gli elementi devono essere presenti nella struttura.");
        }

        Node<E> rep1 = getRepresentative(this.currentElements.get(e1));
        Node<E> rep2 = getRepresentative(this.currentElements.get(e2));

        if (rep1.rank > rep2.rank) {
            rep2.parent = rep1;
            int rank = rep2.rank + 1;
            if (rep1.rank < rank) {
                rep1.rank = rank;
            }
        } else {
            rep1.parent = rep2;
            int rank = rep1.rank + 1;
            if (rep2.rank < rank) {
                rep2.rank = rank;
            }
        }
    }


    /**
     * Restituisce l'insieme degli elementi attualmente rappresentativi (radici)
     * nell'insieme disgiunto.
     * Un elemento è considerato rappresentativo se il valore del nodo
     * associato è uguale al suo genitore (parent).
     *
     * @return Un insieme contenente tutti gli elementi rappresentativi.
     */
    @Override
    public Set<E> getCurrentRepresentatives() {
        Set<E> representatives = new HashSet<>();

        // Itera attraverso le coppie chiave-valore della mappa currentElements
        for (Map.Entry<E, Node<E>> entry : this.currentElements.entrySet()) {
            if (entry.getValue().equals(entry.getValue().parent)) {
                representatives.add(entry.getKey());
            }
        }

        return representatives;
    }


    /**
     * Restituisce l'insieme corrente di elementi che appartengono allo stesso insieme
     * del dato elemento specificato. L'insieme è determinato dal rappresentante dell'elemento
     * all'interno della struttura.
     *
     * @param e L'elemento di cui si vuole ottenere l'insieme associato.
     * @return Un insieme contenente tutti gli elementi che appartengono allo stesso insieme
     *         dell'elemento specificato.
     * @throws NullPointerException     Se l'elemento specificato è nullo.
     * @throws IllegalArgumentException Se l'elemento specificato non è presente nella struttura.
     */
    @Override
    public Set<E> getCurrentElementsOfSetContaining(E e) {
        if (e == null) {
            throw new NullPointerException("L'elemento non può essere nullo.");
        }
        if (!isPresent(e)) {
            throw new IllegalArgumentException("L'elemento specificato non è presente nella struttura.");
        }

        Set<E> set = new HashSet<>();
        Node<E> rep = getRepresentative(this.currentElements.get(e));

        // Itera sugli elementi presenti nella struttura
        for (java.util.Map.Entry<E, Node<E>> entry : this.currentElements.entrySet()) {
            if (getRepresentative(entry.getValue()).equals(rep)) {
                set.add(entry.getKey());
            }
        }

        return set;
    }


    /**
     * Metodo privato per ottenere la radice di un nodo
     * @param n nodo di cui trovare la radice
     * @return nodo radice
     */
    private Node<E> getRepresentative(Node<E> n){
        while(!n.equals(n.parent)){
            n = n.parent;
        }
        return n;
    }

    /**
     * Pulisce tutti gli elementi correnti nel grafo.
     * Questo metodo rimuove tutte le voci presenti nella collezione currentElements,
     * riportando lo stato del grafo a vuoto.
     */
    @Override
    public void clear() {

        this.currentElements.clear();
    }

}