package it.unicam.cs.asdl2425.mp1;

import java.util.Objects; // Per equals e hashCode
import java.util.Iterator; // Per iterare attraverso la prova

/**
 * Una classe che rappresenta una prova di Merkle per un determinato albero di
 * Merkle ed un suo elemento o branch. Oggetti di questa classe rappresentano un
 * processo di verifica auto-contenuto, dato da una sequenza di oggetti
 * MerkleProofHash che rappresentano i passaggi necessari per validare un dato
 * elemento o branch in un albero di Merkle decisi al momento di costruzione
 * della prova.
 *
 * @author Luca Tesei, Marco Caputo (template) **SIMONE ANTONINI
 *         simone01.antonini@studenti.unicam.it DELLO STUDENTE** (implementazione)
 */
public class MerkleProof {

    /**
     * La prova di Merkle, rappresentata come una lista concatenata di oggetti
     * MerkleProofHash.
     */
    private final HashLinkedList<MerkleProofHash> proof;

    /**
     * L'hash della radice dell'albero di Merkle per il quale la prova è stata
     * costruita.
     */
    private final String rootHash;

    /**
     * Lunghezza massima della prova, dato dal numero di hash che la compongono
     * quando completa. Serve ad evitare che la prova venga modificata una volta
     * che essa sia stata completamente costruita.
     */
    private final int length;

    /**
     * Costruisce una nuova prova di Merkle per un dato albero di Merkle,
     * specificando la radice dell'albero e la lunghezza massima della prova. La
     * lunghezza massima della prova è il numero di hash che la compongono
     * quando completa, oltre il quale non è possibile aggiungere altri hash.
     *
     * @param rootHash
     *                     l'hash della radice dell'albero di Merkle.
     * @param length
     *                     la lunghezza massima della prova.
     */
    public MerkleProof(String rootHash, int length) {
        if (rootHash == null)
            throw new IllegalArgumentException("The root hash is null");
        this.proof = new HashLinkedList<>();
        this.rootHash = rootHash;
        this.length = length;
    }

    /**
     * Restituisce la massima lunghezza della prova, dato dal numero di hash che
     * la compongono quando completa.
     *
     * @return la massima lunghezza della prova.
     */
    public int getLength() {
        return length;
    }

    /**
     * Aggiunge un hash alla prova di Merkle, specificando se esso dovrebbe
     * essere concatenato a sinistra o a destra durante la verifica della prova.
     * Se la prova è già completa, ovvero ha già raggiunto il massimo numero di
     * hash deciso alla sua costruzione, l'hash non viene aggiunto e la funzione
     * restituisce false.
     *
     * @param hash
     *                   l'hash da aggiungere alla prova.
     * @param isLeft
     *                   true se l'hash dovrebbe essere concatenato a sinistra,
     *                   false altrimenti.
     * @return true se l'hash è stato aggiunto con successo, false altrimenti.
     */
    public boolean addHash(String hash, boolean isLeft) {
        // Controlla che l'hash non sia nullo
        if (hash == null) {
            throw new IllegalArgumentException("The hash cannot be null");
        }
        // Controlla se è possibile aggiungere un nuovo hash (la prova non è completa)
        if (proof.getSize() < length) {
            // Aggiunge un nuovo oggetto MerkleProofHash alla fine della lista
            proof.addAtTail(new MerkleProofHash(hash, isLeft));
            return true;  // L'hash è stato aggiunto con successo
        }
        return false;  // La prova è completa e non è possibile aggiungere altri hash
    }

    /**
     * Rappresenta un singolo step di una prova di Merkle per la validazione di
     * un dato elemento.
     */
    public static class MerkleProofHash {
        /**
         * L'hash dell'oggetto.
         */
        private final String hash;

        /**
         * Indica se l'hash dell'oggetto dovrebbe essere concatenato a sinistra
         * durante la verifica della prova.
         */
        private final boolean isLeft;

        public MerkleProofHash(String hash, boolean isLeft) {
            if (hash == null)
                throw new IllegalArgumentException("The hash cannot be null");

            this.hash = hash;
            this.isLeft = isLeft;
        }

        /**
         * Restituisce l'hash dell'oggetto MerkleProofHash.
         *
         * @return l'hash dell'oggetto MerkleProofHash.
         */
        public String getHash() {
            return hash;
        }

        /**
         * Restituisce true se, durante la verifica della prova, l'hash
         * dell'oggetto dovrebbe essere concatenato a sinistra, false
         * altrimenti.
         *
         * @return true se l'hash dell'oggetto dovrebbe essere concatenato a
         *         sinistra, false altrimenti.
         */
        public boolean isLeft() {
            return isLeft;
        }

        @Override
        public boolean equals(Object obj) {
            /*
             * Due MerkleProofHash sono uguali se hanno lo stesso hash e lo
             * stesso flag isLeft
             */
            // Controlla se l'oggetto da confrontare è lo stesso
            if (this == obj) {
                return true;
            }
            // Controlla se l'oggetto è nullo o di tipo diverso
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            // Cast a MerkleProofHash e confronto di hash e isLeft
            MerkleProofHash other = (MerkleProofHash) obj;
            return hash.equals(other.hash) && isLeft == other.isLeft;
        }

        @Override
        public String toString() {
            // Rappresentazione dell'oggetto come "hash" seguito da L o R
            return hash + (isLeft ? "L" : "R");
        }

        @Override
        public int hashCode() {
            /*
             * Calcola l'hash code combinando l'hash della stringa e il flag
             * isLeft (1 per true, 0 per false)
             */
            return 31 * hash.hashCode() + (isLeft ? 1 : 0);
        }
    }

    /**
     * Valida un dato elemento per questa prova di Merkle. La verifica avviene
     * combinando l'hash del dato con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, e controllando che l'hash finale coincida con quello
     * del nodo radice dell'albero di Merkle orginale.
     *
     * @param data
     *                 l'elemento da validare.
     * @return true se il dato è valido secondo la prova; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se il dato è null.
     */
    public boolean proveValidityOfData(Object data) {
        // Controlla che il dato non sia nullo
        if (data == null) {
            throw new IllegalArgumentException("The data cannot be null");
        }
        // Calcola l'hash iniziale del dato fornito
        String currentHash = HashUtil.dataToHash(data);
        // Itera attraverso la lista di MerkleProofHash per combinare gli hash
        for (MerkleProofHash step : proof) {
            // Concatena gli hash a sinistra o destra a seconda del flag isLeft
            currentHash = (step.isLeft())
                    ? HashUtil.computeMD5((step.getHash() + currentHash).getBytes())
                    : HashUtil.computeMD5((currentHash + step.getHash()).getBytes());
        }
        // Confronta l'hash finale con l'hash della radice
        return currentHash.equals(rootHash);
    }

    /**
     * Valida un dato branch per questa prova di Merkle. La verifica avviene
     * combinando l'hash del branch con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, e controllando che l'hash finale coincida con quello
     * del nodo radice dell'albero di Merkle orginale.
     *
     * @param branch
     *                   il branch da validare.
     * @return true se il branch è valido secondo la prova; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se il branch è null.
     */
    public boolean proveValidityOfBranch(MerkleNode branch) {
        // Controlla che il branch non sia nullo
        if (branch == null) {
            throw new IllegalArgumentException("The branch cannot be null");
        }
        // Ottiene l'hash iniziale del branch fornito
        String currentHash = branch.getHash();
        // Itera attraverso la lista di MerkleProofHash per combinare gli hash
        for (MerkleProofHash step : proof) {
            // Concatena gli hash a sinistra o destra a seconda del flag isLeft
            currentHash = (step.isLeft())
                    ? HashUtil.computeMD5((step.getHash() + currentHash).getBytes())
                    : HashUtil.computeMD5((currentHash + step.getHash()).getBytes());
        }
        // Confronta l'hash finale con l'hash della radice
        return currentHash.equals(rootHash);
    }
}
