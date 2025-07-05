package it.unicam.cs.asdl2425.mp2traccia;

import java.util.HashSet;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 *
 * Classe singoletto che implementa l'algoritmo di Kruskal per trovare un
 * Minimum Spanning Tree di un grafo non orientato, pesato e con pesi non
 * negativi. L'algoritmo implementato si avvale della classe
 * {@code ForestDisjointSets<GraphNode<L>>} per gestire una collezione di
 * insiemi disgiunti di nodi del grafo.
 *
 * @author Luca Tesei (template) **SIMONE ANTONINI simone01.antonini@studenti.unicam.it** (implementazione)
 *
 * @param <L>
 *                tipo delle etichette dei nodi del grafo
 *
 */
public class KruskalMSP<L> {

    /*
     * Struttura dati per rappresentare gli insiemi disgiunti utilizzata
     * dall'algoritmo di Kruskal.
     */
    private ForestDisjointSets<GraphNode<L>> disjointSets;

    /**
     * Costruisce un calcolatore di un albero di copertura minimo che usa
     * l'algoritmo di Kruskal su un grafo non orientato e pesato.
     */
    public KruskalMSP() {
        this.disjointSets = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Utilizza l'algoritmo goloso di Kruskal per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. L'albero restituito non è radicato, quindi è rappresentato
     * semplicemente con un sottoinsieme degli archi del grafo.
     *
     * @param g
     *              un grafo non orientato, pesato, con pesi non negativi
     * @return l'insieme degli archi del grafo g che costituiscono l'albero di
     *         copertura minimo trovato
     * @throw NullPointerException se il grafo g è null
     * @throw IllegalArgumentException se il grafo g è orientato, non pesato o
     *        con pesi negativi
     */



    public Set<GraphEdge<L>> computeMSP(Graph<L> g) {
        this.disjointSets.clear(); //svuota gli insiemi siagiunti usati per costruire l'MST

        //controllo che il grafonon sia nullo
        if (g == null) {
            throw new NullPointerException("Il grafo non può essere nullo.");
        }
        //controllo che il grafo non sia orientato
        if (g.isDirected()) {
            throw new IllegalArgumentException("Il grafo deve essere non orientato.");
        }

        Set<GraphEdge<L>> edges = g.getEdges();
        //controllo che i pesi degli archi siano non negativi
        for (GraphEdge<L> edge : edges) {
            if (!edge.hasWeight() || edge.getWeight() < 0) {
                throw new IllegalArgumentException("Gli archi devono avere un peso non negativo.");
            }
        }

        Set<GraphEdge<L>> result = new HashSet<>();
        //ciclo che determina quali archi si devono includere nell' MST
        boolean loop = true;
        while (loop) {
            GraphEdge<L> target = null;

            for (GraphEdge<L> edge : edges) {
                if (!checkEdge(edge)) {
                    continue;
                }
                if (target == null || target.getWeight() > edge.getWeight()) {
                    target = edge;
                }
            }
            if (target == null) {
                loop = false;
            } else {
                edges.remove(target);
                result.add(target);
                if (!disjointSets.isPresent(target.getNode1())) {
                    disjointSets.makeSet(target.getNode1());
                }
                if (!disjointSets.isPresent(target.getNode2())) {
                    disjointSets.makeSet(target.getNode2());
                }
                disjointSets.union(target.getNode1(), target.getNode2());
            }
        }

        return result;
    }



    /**
     * Verifica la validità di un arco nel grafo.
     * L'arco è considerato valido se almeno uno dei due nodi non è presente
     * nei set disgiunti (disjointSets) o se i due nodi appartengono a insiemi diversi.
     *
     * @param edge L'arco da verificare.
     * @return true se l'arco è valido, false altrimenti.
     */
    private boolean checkEdge(GraphEdge<L> edge) {
        if (!(disjointSets.isPresent(edge.getNode1()) && disjointSets.isPresent(edge.getNode2()))) {
            return true;
        }
        return !disjointSets.findSet(edge.getNode1()).equals(disjointSets.findSet(edge.getNode2()));
    }

}
