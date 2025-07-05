package it.unicam.cs.asdl2425.mp1;

import java.util.Objects;

/**
 * Rappresenta un nodo di un albero di Merkle.
 *
 * @author Luca Tesei, Marco Caputo (template) **SIMONE ANTONINI
 *         simone01.antonini@studenti.unicam.it ** (implementazione)
 */
public class MerkleNode {
    private final String hash; // Hash associato al nodo.

    private final MerkleNode left; // Figlio sinistro del nodo.

    private final MerkleNode right; // Figlio destro del nodo.

    /**
     * Costruisce un nodo Merkle foglia con un valore di hash, quindi,
     * corrispondente all'hash di un dato.
     *
     * @param hash
     *                 l'hash associato al nodo.
     */
    public MerkleNode(String hash) {
        this(hash, null, null);
    }

    /**
     * Costruisce un nodo Merkle con un valore di hash e due figli, quindi,
     * corrispondente all'hash di un branch.
     *
     * @param hash
     *                  l'hash associato al nodo.
     * @param left
     *                  il figlio sinistro.
     * @param right
     *                  il figlio destro.
     */
    public MerkleNode(String hash, MerkleNode left, MerkleNode right) {
        this.hash = hash;
        this.left = left;
        this.right = right;
    }

    /**
     * Restituisce l'hash associato al nodo.
     *
     * @return l'hash associato al nodo.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Restituisce il figlio sinistro del nodo.
     *
     * @return il figlio sinistro del nodo.
     */
    public MerkleNode getLeft() {
        return left;
    }

    /**
     * Restituisce il figlio destro del nodo.
     *
     * @return il figlio destro del nodo.
     */
    public MerkleNode getRight() {
        return right;
    }

    /**
     * Restituisce true se il nodo è una foglia, false altrimenti.
     *
     * @return true se il nodo è una foglia, false altrimenti.
     */
    public boolean isLeaf() {
        // Verifica che entrambi i figli del nodo siano nulli
        return (left == null && right == null);
    }

    @Override
    public String toString() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        // Controlla se l'oggetto da confrontare è lo stesso (riferimento)
        if (this == obj) {
            return true;
        }
        // Se l'oggetto è null o di una classe diversa, non sono uguali
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // Cast dell'oggetto a MerkleNode e confronto degli hash
        MerkleNode other = (MerkleNode) obj;
        return Objects.equals(hash, other.hash);

        /* Due nodi sono uguali se hanno lo stesso hash */
    }


    @Override
    public int hashCode() {
        // Calcola l'hash code basato sull'hash del nodo, coerentemente con equals
        return Objects.hashCode(hash);
    }
}
