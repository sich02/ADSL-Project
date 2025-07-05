/**
 *
 */
package it.unicam.cs.asdl2425.mp2traccia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;



/**
 * Classe che implementa un grafo non orientato tramite matrice di adiacenza.
 * Non sono accettate etichette dei nodi null e non sono accettate etichette
 * duplicate nei nodi (che in quel caso sono lo stesso nodo).
 *
 * I nodi sono indicizzati da 0 a nodeCoount() - 1 seguendo l'ordine del loro
 * inserimento (0 è l'indice del primo nodo inserito, 1 del secondo e così via)
 * e quindi in ogni istante la matrice di adiacenza ha dimensione nodeCount() *
 * nodeCount(). La matrice, sempre quadrata, deve quindi aumentare di dimensione
 * ad ogni inserimento di un nodo. Per questo non è rappresentata tramite array
 * ma tramite ArrayList.
 *
 * Gli oggetti GraphNode<L>, cioè i nodi, sono memorizzati in una mappa che
 * associa ad ogni nodo l'indice assegnato in fase di inserimento. Il dominio
 * della mappa rappresenta quindi l'insieme dei nodi.
 *
 * Gli archi sono memorizzati nella matrice di adiacenza. A differenza della
 * rappresentazione standard con matrice di adiacenza, la posizione i,j della
 * matrice non contiene un flag di presenza, ma è null se i nodi i e j non sono
 * collegati da un arco e contiene un oggetto della classe GraphEdge<L> se lo
 * sono. Tale oggetto rappresenta l'arco. Un oggetto uguale (secondo equals) e
 * con lo stesso peso (se gli archi sono pesati) deve essere presente nella
 * posizione j, i della matrice.
 *
 * Questa classe non supporta i metodi di cancellazione di nodi e archi, ma
 * supporta tutti i metodi che usano indici, utilizzando l'indice assegnato a
 * ogni nodo in fase di inserimento.
 *
 * @author Luca Tesei (template) **SIMONE ANTONINI simone01.antonini@studenti.unicam.it** (implementazione)
 *
 */
public class AdjacencyMatrixUndirectedGraph<L> extends Graph<L> {
    /*
     * Le seguenti variabili istanza sono protected al solo scopo di agevolare
     * il JUnit testing
     */

    // Insieme dei nodi e associazione di ogni nodo con il proprio indice nella
    // matrice di adiacenza
    protected Map<GraphNode<L>, Integer> nodesIndex;

    // Matrice di adiacenza, gli elementi sono null o oggetti della classe
    // GraphEdge<L>. L'uso di ArrayList permette alla matrice di aumentare di
    // dimensione gradualmente ad ogni inserimento di un nuovo nodo.
    protected ArrayList<ArrayList<GraphEdge<L>>> matrix;

    /**
     * Crea un grafo vuoto.
     */
    public AdjacencyMatrixUndirectedGraph() {
        this.matrix = new ArrayList<ArrayList<GraphEdge<L>>>();
        this.nodesIndex = new HashMap<GraphNode<L>, Integer>();
    }

    @Override
    /**
     * Restituisce il numero totale di nodi presenti nel grafo.
     * Il conteggio si basa sulla dimensione della mappa nodesIndex,
     * che memorizza gli indici associati ai nodi.
     *
     * @return Il numero di nodi nel grafo.
     */
    public int nodeCount() {
        return this.nodesIndex.size();
    }


    /**
     * Calcola e restituisce il numero totale di archi presenti nel grafo.
     * Gli archi vengono contati iterando sulla parte triangolare inferiore
     * della matrice di adiacenza, evitando di contarli due volte.
     *
     * @return Il numero totale di archi nel grafo.
     */
    @Override
    public int edgeCount() {
        int count = 0;
        for (int row = 0; row < nodeCount(); row++) {
            for (int col = 0; col < row; col++) {
                if (matrix.get(row).get(col) != null) {
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * Cancella tutti i dati del grafo, rimuovendo sia i nodi che gli archi.
     * Viene svuotata la mappa degli indici dei nodi e la matrice di adiacenza.
     */
    @Override
    public void clear() {
        nodesIndex.clear();
        matrix.clear();
    }

    /**
     * Indica se il grafo è diretto.
     * In questo caso, restituisce sempre false perché il grafo è non orientato.
     *
     * @return false, poiché il grafo non è diretto.
     */
    @Override
    public boolean isDirected() {
        return false;
    }

    /**
     * Restituisce l'insieme dei nodi presenti nel grafo.
     * L'insieme è ottenuto utilizzando le chiavi della mappa nodesIndex,
     * che rappresentano i nodi del grafo.
     *
     * @return Un insieme contenente tutti i nodi del grafo.
     */
    @Override
    public Set<GraphNode<L>> getNodes() {

        return this.nodesIndex.keySet();
    }

    /**
     * Aggiunge un nodo al grafo.
     * Se il nodo è già presente o è nullo, non viene aggiunto.
     * Altrimenti, il nodo viene inserito nella struttura dati e la matrice
     * di adiacenza viene aggiornata per tenere conto del nuovo nodo.
     *
     * @param node Il nodo da aggiungere al grafo.
     * @return true se il nodo è stato aggiunto correttamente, false se il nodo era già presente.
     * @throws NullPointerException se il nodo passato è null.
     */
    @Override
    public boolean addNode(GraphNode<L> node) {
        if (node == null) {
            throw new NullPointerException();
        }

        if (this.nodesIndex.containsKey(node)) {
            return false;
        }

        this.nodesIndex.put(node, this.nodesIndex.size());

        for (ArrayList<GraphEdge<L>> row : matrix) {
            row.add(null);
        }

        ArrayList<GraphEdge<L>> newRow = new ArrayList<>();
        for (int i = 0; i < nodesIndex.size(); i++) {
            newRow.add(null);
        }
        this.matrix.add(newRow);

        return true;
    }


    /**
     * Rimuove un nodo dal grafo e aggiorna di conseguenza la matrice di adiacenza.
     * Se il nodo non esiste o è null, gestisce i casi specifici (eccezione o ritorno false).
     *
     * @param node Il nodo da rimuovere.
     * @return true se il nodo è stato rimosso correttamente, false se il nodo non esiste.
     * @throws NullPointerException se il nodo passato è null.
     */
    @Override
    public boolean removeNode(GraphNode<L> node) {
        if (node == null) {
            throw new NullPointerException();
        }

        if (!nodesIndex.containsKey(node)) {
            return false;
        }

        int removedIndex = nodesIndex.remove(node);

        Set<GraphNode<L>> nodesToShift = new HashSet<>();
        for (Entry<GraphNode<L>, Integer> entry : nodesIndex.entrySet()) {
            if (entry.getValue() > removedIndex) {
                nodesToShift.add(entry.getKey());
            }
        }

        for (GraphNode<L> n : nodesToShift) {
            int oldIndex = nodesIndex.remove(n);
            nodesIndex.put(n, oldIndex - 1);
        }

        for (ArrayList<GraphEdge<L>> row : matrix) {
            row.remove(removedIndex);
        }

        matrix.remove(removedIndex);
        return true;
    }


    /**
     * Verifica se il nodo specificato è presente nel grafo.
     * La presenza del nodo è determinata dalla sua esistenza come chiave
     * nell'indice dei nodi (nodesIndex).
     *
     * @param node Il nodo da verificare.
     * @return true se il nodo è presente nel grafo, false altrimenti.
     * @throws NullPointerException Se il nodo passato è nullo.
     */
    @Override
    public boolean containsNode(GraphNode<L> node) {
        if (node == null) {
            throw new NullPointerException("Il nodo non può essere nullo.");
        }
        return this.nodesIndex.containsKey(node);
    }


    /**
     * Restituisce il nodo del grafo associato all'etichetta specificata.
     * Se il nodo con l'etichetta fornita esiste, viene restituito il nodo corrispondente.
     * Se non viene trovato alcun nodo con quella etichetta, il metodo restituisce null.
     *
     * @param label L'etichetta del nodo da cercare.
     * @return Il nodo corrispondente all'etichetta, o null se il nodo non esiste.
     * @throws NullPointerException Se l'etichetta passata è nulla.
     */
    @Override
    public GraphNode<L> getNodeOf(L label) {
        if (label == null) {
            throw new NullPointerException("L'etichetta non può essere nulla.");
        }

        GraphNode<L> node = new GraphNode<L>(label);

        for (GraphNode<L> n : this.nodesIndex.keySet()) {
            if (n.equals(node)) {
                return n;
            }
        }

        return null;
    }


    /**
     * Restituisce l'indice del nodo associato alla specifica etichetta nel grafo.
     * Se il nodo non è presente nel grafo, viene lanciata un'eccezione IllegalArgumentException.
     * Se l'etichetta fornita è nulla, viene lanciata un'eccezione NullPointerException.
     *
     * @param label L'etichetta del nodo di cui ottenere l'indice.
     * @return L'indice del nodo associato all'etichetta fornita.
     * @throws NullPointerException Se l'etichetta fornita è nulla.
     * @throws IllegalArgumentException Se il nodo associato all'etichetta non è presente nel grafo.
     */
    @Override
    public int getNodeIndexOf(L label) {
        if (label == null) {
            throw new NullPointerException("L'etichetta non può essere nulla.");
        }
        GraphNode<L> node = new GraphNode<L>(label);
        if (!nodesIndex.containsKey(node)) {
            throw new IllegalArgumentException("Nodo non trovato nel grafo.");
        }
        return nodesIndex.get(node);
    }


    /**
     * Restituisce il nodo associato all'indice specificato.
     * Se l'indice è valido, il nodo corrispondente viene estratto
     * dalla mappa `nodesIndex` e restituito. Se l'indice non è valido,
     * viene lanciata un'eccezione `IndexOutOfBoundsException`.
     *
     * @param i L'indice del nodo da recuperare.
     * @return Il nodo corrispondente all'indice, oppure null se non trovato.
     * @throws IndexOutOfBoundsException Se l'indice è fuori dal range valido.
     */
    @Override
    public GraphNode<L> getNodeAtIndex(int i) {
        if (i < 0 || i >= this.nodesIndex.size()) {
            throw new IndexOutOfBoundsException("Indice fuori dai limiti.");
        }
        for (Entry<GraphNode<L>, Integer> e : this.nodesIndex.entrySet()) {
            if (e.getValue() == i) {
                return e.getKey();
            }
        }
        return null;
    }

    /**
     * Restituisce l'insieme dei nodi adiacenti a un nodo specificato nel grafo.
     * I nodi adiacenti sono determinati esaminando tutti gli archi collegati
     * al nodo fornito. Se un nodo è collegato all'altro tramite un arco,
     * viene aggiunto all'insieme risultante.
     *
     * @param node Il nodo di cui si vogliono ottenere i nodi adiacenti.
     * @return Un insieme contenente tutti i nodi adiacenti al nodo specificato.
     * @throws NullPointerException Se il nodo passato è nullo.
     */
    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(GraphNode<L> node) {
        HashSet<GraphNode<L>> set = new HashSet<>();
        Set<GraphEdge<L>> edges = getEdgesOf(node);

        for (GraphEdge<L> edge : edges) {
            set.add(edge.getNode1().equals(node) ? edge.getNode2() : edge.getNode1());
        }

        return set;
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    /**
     * Restituisce l'insieme di tutti gli archi presenti nel grafo.
     * Gli archi sono recuperati dalla matrice di adiacenza (matrix)
     * e aggiunti a un insieme (set) per garantire l'unicità.
     *
     * @return Un insieme contenente tutti gli archi del grafo.
     */
    @Override
    public Set<GraphEdge<L>> getEdges() {
        Set<GraphEdge<L>> set = new HashSet<>();
        for (int i = 0; i < nodeCount(); i++) {
            for (int j = 0; j < i; j++) {
                GraphEdge<L> edge = matrix.get(i).get(j);
                if (edge != null) {
                    set.add(edge);
                }
            }
        }
        return set;
    }


    /**
     * Aggiunge un arco non orientato al grafo.
     * L'arco viene aggiunto solo se entrambi i nodi sono presenti nel grafo,
     * se l'arco non è già presente e se l'arco è non orientato.
     *
     * @param edge L'arco da aggiungere al grafo.
     * @return true se l'arco è stato aggiunto con successo, false se l'arco era già presente.
     * @throws NullPointerException    Se l'arco specificato è nullo.
     * @throws IllegalArgumentException Se l'arco è orientato o uno dei nodi associati non è presente nel grafo.
     */
    @Override
    public boolean addEdge(GraphEdge<L> edge) {
        if (edge == null) {
            throw new NullPointerException("L'arco non può essere nullo.");
        }

        if (edge.isDirected() ||
                !(this.nodesIndex.containsKey(edge.getNode1()) && this.nodesIndex.containsKey(edge.getNode2()))) {
            throw new IllegalArgumentException("L'arco deve essere non orientato e i nodi devono esistere nel grafo.");
        }
        int firstIndex = nodesIndex.get(edge.getNode1());
        int secondIndex = nodesIndex.get(edge.getNode2());

        if (this.matrix.get(firstIndex).get(secondIndex) != null) {
            return false;
        }

        this.matrix.get(firstIndex).set(secondIndex, edge);
        this.matrix.get(secondIndex).set(firstIndex, edge);

        return true;
    }


    /**
     * Rimuove un arco specificato dal grafo, se presente.
     * La rimozione consiste nel settare a null le celle corrispondenti
     * all'arco nella matrice di adiacenza.
     *
     * @param edge L'arco da rimuovere.
     * @return true se l'arco è stato rimosso con successo, false se l'arco non era presente.
     * @throws NullPointerException Se l'arco passato è nullo.
     */
    @Override
    public boolean removeEdge(GraphEdge<L> edge) {
        if (containsEdge(edge)) {
            int firstIndex = nodesIndex.get(edge.getNode1());
            int secondIndex = nodesIndex.get(edge.getNode2());

            this.matrix.get(firstIndex).set(secondIndex, null);
            this.matrix.get(secondIndex).set(firstIndex, null);

            return true;
        }

        return false;
    }


    /**
     * Verifica se l'arco specificato è presente nel grafo.
     * La presenza dell'arco è determinata dalla matrice di adiacenza (matrix),
     * che rappresenta le connessioni tra i nodi.
     *
     * @param edge L'arco da verificare.
     * @return true se l'arco è presente nel grafo, false altrimenti.
     * @throws NullPointerException    Se l'arco passato è nullo.
     * @throws IllegalArgumentException Se uno o entrambi i nodi dell'arco non sono presenti nel grafo.
     */
    @Override
    public boolean containsEdge(GraphEdge<L> edge) {
        if (edge == null) {
            throw new NullPointerException("L'arco non può essere nullo.");
        }

        if (!(this.nodesIndex.containsKey(edge.getNode1())
                && this.nodesIndex.containsKey(edge.getNode2()))) {
            throw new IllegalArgumentException("Uno o entrambi i nodi dell'arco non sono presenti nel grafo.");
        }

        int firstIndex = nodesIndex.get(edge.getNode1());
        int secondIndex = nodesIndex.get(edge.getNode2());

        return this.matrix.get(firstIndex).get(secondIndex) != null;
    }


    /**
     * Restituisce l'insieme degli archi associati a un determinato nodo del grafo.
     * Gli archi sono prelevati dalla riga corrispondente nella matrice di adiacenza,
     * dove ogni posizione non nulla rappresenta un arco.
     *
     * @param node Il nodo per il quale si vogliono ottenere gli archi.
     * @return Un insieme contenente tutti gli archi collegati al nodo specificato.
     * @throws NullPointerException     Se il nodo passato è nullo.
     * @throws IllegalArgumentException Se il nodo non è presente nel grafo.
     */
    @Override
    public Set<GraphEdge<L>> getEdgesOf(GraphNode<L> node) {
        if (node == null) {
            throw new NullPointerException("Il nodo non può essere nullo.");
        }

        if (!this.nodesIndex.containsKey(node)) {
            throw new IllegalArgumentException("Il nodo specificato non è presente nel grafo.");
        }

        int index = this.nodesIndex.get(node);
        HashSet<GraphEdge<L>> set = new HashSet<>();

        for (GraphEdge<L> edge : this.matrix.get(index)) {
            if (edge != null) {
                set.add(edge);
            }
        }

        return set;
    }


    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

}