package it.unicam.cs.asdl2425.mp2traccia;

import java.util.HashSet;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe singoletto che realizza un calcolatore delle componenti connesse di un
 * grafo non orientato utilizzando una struttura dati efficiente (fornita dalla
 * classe {@ForestDisjointSets<GraphNode<L>>}) per gestire insiemi disgiunti di
 * nodi del grafo che sono, alla fine del calcolo, le componenti connesse.
 *
 * @author Luca Tesei (template) **SIMONE ANTONINI simone01.antonini@studenti.unicam.it** (implementazione)
 *
 * @param <L>
 *                il tipo delle etichette dei nodi del grafo
 */
public class UndirectedGraphConnectedComponentsComputer<L> {

    /*
     * Struttura dati per gli insiemi disgiunti.
     */
    private ForestDisjointSets<GraphNode<L>> f;

    /**
     * Crea un calcolatore di componenti connesse.
     */
    public UndirectedGraphConnectedComponentsComputer() {
        this.f = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Calcola le componenti connesse di un grafo non orientato utilizzando una
     * collezione di insiemi disgiunti.
     *
     * @param g
     *              un grafo non orientato
     * @return un insieme di componenti connesse, ognuna rappresentata da un
     *         insieme di nodi del grafo
     * @throws NullPointerException
     *                                      se il grafo passato è nullo
     * @throws IllegalArgumentException
     *                                      se il grafo passato è orientato
     */


    public Set<Set<GraphNode<L>>> computeConnectedComponents(Graph<L> g) {
        // Resetta la struttura dati Union-Find
        f.clear();

        // Controlla che il grafo non sia nullo
        if (g == null) {
            throw new NullPointerException("Il grafo non può essere nullo.");
        }

        // Controlla che il grafo non sia orientato
        if (g.isDirected()) {
            throw new IllegalArgumentException("Il metodo supporta solo grafi non orientati.");
        }

        for (GraphNode<L> node : g.getNodes()) {
            f.makeSet(node);
        }
        for (GraphEdge<L> edge : g.getEdges()) {
            f.union(edge.getNode1(), edge.getNode2());
        }

        Set<Set<GraphNode<L>>> sets = new HashSet<>();
        Set<GraphNode<L>> roots = f.getCurrentRepresentatives();

        for (GraphNode<L> root : roots) {
            sets.add(f.getCurrentElementsOfSetContaining(root));
        }
        return sets;
    }

}
