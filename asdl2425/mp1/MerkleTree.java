package it.unicam.cs.asdl2425.mp1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// TODO inserire solo gli import della Java SE che si ritengono necessari

/**
 * Un Merkle Tree, noto anche come hash tree binario, è una struttura dati per
 * verificare in modo efficiente l'integrità e l'autenticità dei dati
 * all'interno di un set di dati più ampio. Viene costruito eseguendo l'hashing
 * ricorsivo di coppie di dati (valori hash crittografici) fino a ottenere un
 * singolo hash root. In questa implementazione la verifica di dati avviene
 * utilizzando hash MD5.
 *
 * @author Luca Tesei, Marco Caputo (template) **INSERIRE NOME, COGNOME ED EMAIL
 *         xxxx@studenti.unicam.it DELLO STUDENTE** (implementazione)
 *
 * @param <T>
 *                il tipo di dati su cui l'albero è costruito.
 */
public class MerkleTree<T> {
    /**
     * Nodo radice dell'albero.
     */
    private final MerkleNode root;

    /**
     * Larghezza dell'albero, ovvero il numero di nodi nell'ultimo livello.
     */
    private final int width;

    /**
     * Costruisce un albero di Merkle a partire da un oggetto HashLinkedList,
     * utilizzando direttamente gli hash presenti nella lista per costruire le
     * foglie. Si noti che gli hash dei nodi intermedi dovrebbero essere
     * ottenuti da quelli inferiori concatenando hash adiacenti due a due e
     * applicando direttmaente la funzione di hash MD5 al risultato della
     * concatenazione in bytes.
     *
     * @param hashList
     *                     un oggetto HashLinkedList contenente i dati e i
     *                     relativi hash.
     * @throws IllegalArgumentException
     *                                      se la lista è null o vuota.
     */
    public MerkleTree(HashLinkedList<T> hashList) {
        // TODO implementare
        if (hashList == null || hashList.getSize() == 0) {
            throw new IllegalArgumentException("The list cannot be null or empty");
        }

        // creo i nodi foglia
        ArrayList<MerkleNode> currentLevel = new ArrayList<>();
        for (String hash : hashList.getAllHashes()) {
            currentLevel.add(new MerkleNode(hash));
        }

        // costruisco l'albero livello per livello
        while (currentLevel.size() > 1) {
            ArrayList<MerkleNode> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i += 2) {
                MerkleNode left = currentLevel.get(i);
                MerkleNode right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : null;
                String parentHash = HashUtil.computeMD5(
                        (left.getHash() + (right != null ? right.getHash() : "")).getBytes());
                nextLevel.add(new MerkleNode(parentHash, left, right));
            }
            currentLevel = nextLevel;
        }

        // imposto radice e altezza
        this.root = currentLevel.get(0);
        this.width = hashList.getSize();
    }

    /**
     * Restituisce il nodo radice dell'albero.
     *
     * @return il nodo radice.
     */
    public MerkleNode getRoot() {
        return root;
    }

    /**
     * Restituisce la larghezza dell'albero.
     *
     * @return la larghezza dell'albero.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Restituisce l'altezza dell'albero.
     *
     * @return l'altezza dell'albero.
     */
    public int getHeight() {
        // TODO implementare
        if (root == null) return -1; // l'altezza di un albero vuoto è -1
        return calculateHeight(root) - 1; // Subtract 1 to match the expected height
    }

    /**
     * Restituisce l'indice di un dato elemento secondo l'albero di Merkle
     * descritto da un dato branch. Gli indici forniti partono da 0 e
     * corrispondono all'ordine degli hash corrispondenti agli elementi
     * nell'ultimo livello dell'albero da sinistra a destra. Nel caso in cui il
     * branch fornito corrisponda alla radice di un sottoalbero, l'indice
     * fornito rappresenta un indice relativo a quel sottoalbero, ovvero un
     * offset rispetto all'indice del primo elemento del blocco di dati che
     * rappresenta. Se l'hash dell'elemento non è presente come dato
     * dell'albero, viene restituito -1.
     *
     * @param branch
     *                   la radice dell'albero di Merkle.
     * @param data
     *                   l'elemento da cercare.
     * @return l'indice del dato nell'albero; -1 se l'hash del dato non è
     *         presente.
     * @throws IllegalArgumentException
     *                                      se il branch o il dato sono null o
     *                                      se il branch non è parte
     *                                      dell'albero.
     */
    public int getIndexOfData(MerkleNode branch, T data) {
        // TODO implementare
        if (branch == null || data == null) {
            throw new IllegalArgumentException("Branch and data cannot be null");
        }
        if (!validateBranch(branch)) {
            throw new IllegalArgumentException("Branch is not part of this tree");
        }

        String targetHash = HashUtil.dataToHash(data);
        return findIndexInBranch(branch, targetHash, 0, calculateBranchWidth(branch));
    }

    /**
     * Restituisce l'indice di un elemento secondo questo albero di Merkle. Gli
     * indici forniti partono da 0 e corrispondono all'ordine degli hash
     * corrispondenti agli elementi nell'ultimo livello dell'albero da sinistra
     * a destra (e quindi l'ordine degli elementi forniti alla costruzione). Se
     * l'hash dell'elemento non è presente come dato dell'albero, viene
     * restituito -1.
     *
     * @param data
     *                 l'elemento da cercare.
     * @return l'indice del dato nell'albero; -1 se il dato non è presente.
     * @throws IllegalArgumentException
     *                                      se il dato è null.
     */
    public int getIndexOfData(T data) {
        // TODO implementare
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return findIndex(root, HashUtil.dataToHash(data), 0, width);
    }

    /**
     * Sottopone a validazione un elemento fornito per verificare se appartiene
     * all'albero di Merkle, controllando se il suo hash è parte dell'albero
     * come hash di un nodo foglia.
     *
     * @param data
     *                 l'elemento da validare
     * @return true se l'hash dell'elemento è parte dell'albero; false
     *         altrimenti.
     */
    public boolean validateData(T data) {
        // TODO implementare
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return containsHash(root, HashUtil.dataToHash(data));
    }

    /**
     * Sottopone a validazione un dato sottoalbero di Merkle, corrispondente
     * quindi a un blocco di dati, per verificare se è valido rispetto a questo
     * albero e ai suoi hash. Un sottoalbero è valido se l'hash della sua radice
     * è uguale all'hash di un qualsiasi nodo intermedio di questo albero. Si
     * noti che il sottoalbero fornito può corrispondere a una foglia.
     *
     * @param branch
     *                   la radice del sottoalbero di Merkle da validare.
     * @return true se il sottoalbero di Merkle è valido; false altrimenti.
     */
    public boolean validateBranch(MerkleNode branch) {
        // TODO implementare
        if (branch == null) {
            throw new IllegalArgumentException("Branch cannot be null");
        }
        return containsHash(root, branch.getHash());
    }

    /**
     * Sottopone a validazione un dato albero di Merkle per verificare se è
     * valido rispetto a questo albero e ai suoi hash. Grazie alle proprietà
     * degli alberi di Merkle, ciò può essere fatto in tempo costante.
     *
     * @param otherTree
     *                      il nodo radice dell'altro albero di Merkle da
     *                      validare.
     * @return true se l'altro albero di Merkle è valido; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se l'albero fornito è null.
     */
    public boolean validateTree(MerkleTree<T> otherTree) {
        // TODO implementare
        if (otherTree == null) {
            throw new IllegalArgumentException("Other tree cannot be null");
        }
        return this.root.getHash().equals(otherTree.getRoot().getHash());
    }

    /**
     * Trova gli indici degli elementi di dati non validi (cioè con un hash
     * diverso) in un dato Merkle Tree, secondo questo Merkle Tree. Grazie alle
     * proprietà degli alberi di Merkle, ciò può essere fatto confrontando gli
     * hash dei nodi interni corrispondenti nei due alberi. Ad esempio, nel caso
     * di un singolo dato non valido, verrebbe percorso un unico cammino di
     * lunghezza pari all'altezza dell'albero. Gli indici forniti partono da 0 e
     * corrispondono all'ordine degli elementi nell'ultimo livello dell'albero
     * da sinistra a destra (e quindi l'ordine degli elementi forniti alla
     * costruzione). Se l'albero fornito ha una struttura diversa, possibilmente
     * a causa di una quantità diversa di elementi con cui è stato costruito e,
     * quindi, non rappresenta gli stessi dati, viene lanciata un'eccezione.
     *
     * @param otherTree
     *                      l'altro Merkle Tree.
     * @throws IllegalArgumentException
     *                                      se l'altro albero è null o ha una
     *                                      struttura diversa.
     * @return l'insieme di indici degli elementi di dati non validi.
     */
    public Set<Integer> findInvalidDataIndices(MerkleTree<T> otherTree) {
        if (otherTree == null) {
            throw new IllegalArgumentException("Other tree cannot be null");
        }
        if (this.width != otherTree.getWidth()) {
            throw new IllegalArgumentException("The trees have different structures");
        }

        Set<Integer> invalidIndices = new HashSet<>();
        compareNodes(root, otherTree.getRoot(), 0, width, invalidIndices);
        return invalidIndices;
    }

    /**
     * Restituisce la prova di Merkle per un dato elemento, ovvero la lista di
     * hash dei nodi fratelli di ciascun nodo nel cammino dalla radice a una
     * foglia contenente il dato. La prova di Merkle dovrebbe fornire una lista
     * di oggetti MerkleProofHash tale per cui, combinando l'hash del dato con
     * l'hash del primo oggetto MerkleProofHash in un nuovo hash, il risultato
     * con il successivo e così via fino all'ultimo oggetto, si possa ottenere
     * l'hash del nodo padre dell'albero. Nel caso in cui non ci, in determinati
     * step della prova non ci siano due hash distinti da combinare, l'hash deve
     * comunque ricalcolato sulla base dell'unico hash disponibile.
     *
     * @param data
     *                 l'elemento per cui generare la prova di Merkle.
     * @return la prova di Merkle per il dato.
     * @throws IllegalArgumentException
     *                                      se il dato è null o non è parte
     *                                      dell'albero.
     */
    public MerkleProof getMerkleProof(T data) {

        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        String targetHash = HashUtil.dataToHash(data);
        MerkleProof proof = new MerkleProof(root.getHash(), getHeight());
        if (!buildProof(root, targetHash, proof)) {
            throw new IllegalArgumentException("Data not found in the tree");
        }
        return proof;
    }

    /**
     * Restituisce la prova di Merkle per un dato branch, ovvero la lista di
     * hash dei nodi fratelli di ciascun nodo nel cammino dalla radice al dato
     * nodo branch, rappresentativo di un blocco di dati. La prova di Merkle
     * dovrebbe fornire una lista di oggetti MerkleProofHash tale per cui,
     * combinando l'hash del branch con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, si possa ottenere l'hash del nodo padre dell'albero.
     * Nel caso in cui non ci, in determinati step della prova non ci siano due
     * hash distinti da combinare, l'hash deve comunque ricalcolato sulla base
     * dell'unico hash disponibile.
     *
     * @param branch
     *                   il branch per cui generare la prova di Merkle.
     * @return la prova di Merkle per il branch.
     * @throws IllegalArgumentException
     *                                      se il branch è null o non è parte
     *                                      dell'albero.
     */
    public MerkleProof getMerkleProof(MerkleNode branch) {

        if (branch == null || !validateBranch(branch)) {
            throw new IllegalArgumentException("Branch is null or not part of this tree");
        }

        // Create a proof with the root hash and correct height
        MerkleProof proof = new MerkleProof(root.getHash(), calculateProofHeight(branch));
        buildBranchProof(root, branch, proof);
        return proof;
    }

    private int calculateHeight(MerkleNode node) {
        if (node == null) return 0;
        if (node.isLeaf()) return 1;
        return 1 + Math.max(calculateHeight(node.getLeft()), calculateHeight(node.getRight()));
    }

    private int findIndex(MerkleNode node, String targetHash, int offset, int branchWidth) {
        if (node == null) return -1;
        if (node.isLeaf() && node.getHash().equals(targetHash)) return offset;

        int halfWidth = branchWidth / 2;
        int leftIndex = findIndex(node.getLeft(), targetHash, offset, halfWidth);
        if (leftIndex != -1) return leftIndex;

        return findIndex(node.getRight(), targetHash, offset + halfWidth, halfWidth);
    }

    private boolean containsHash(MerkleNode node, String targetHash) {
        if (node == null) return false;
        if (node.getHash().equals(targetHash)) return true;
        return containsHash(node.getLeft(), targetHash) || containsHash(node.getRight(), targetHash);
    }

    private void findInvalidIndices(MerkleNode node1, MerkleNode node2, int offset, int branchWidth, Set<Integer> invalidIndices) {
        if (node1 == null || node2 == null) return;

        if (node1.isLeaf() && node2.isLeaf()) {
            if (!node1.getHash().equals(node2.getHash())) {
                invalidIndices.add(offset);
            }
            return;
        }

        int halfWidth = branchWidth / 2;
        findInvalidIndices(node1.getLeft(), node2.getLeft(), offset, halfWidth, invalidIndices);
        findInvalidIndices(node1.getRight(), node2.getRight(), offset + halfWidth, halfWidth, invalidIndices);
    }

    private boolean buildProof(MerkleNode node, String targetHash, MerkleProof proof) {
        if (node == null) return false;
        if (node.getHash().equals(targetHash)) return true;

        if (buildProof(node.getLeft(), targetHash, proof)) {
            // Add empty string if right node is null, otherwise add its hash
            proof.addHash(node.getRight() != null ? node.getRight().getHash() : "", false);
            return true;
        }

        if (buildProof(node.getRight(), targetHash, proof)) {
            // Add empty string if left node is null, otherwise add its hash
            proof.addHash(node.getLeft() != null ? node.getLeft().getHash() : "", true);
            return true;
        }

        return false;
    }

    private boolean buildBranchProof(MerkleNode current, MerkleNode targetBranch, MerkleProof proof) {
        if (current == null || current == targetBranch) {
            return true;
        }

        if (buildBranchProof(current.getLeft(), targetBranch, proof)) {
            // Add empty string if right node is null, otherwise add its hash
            proof.addHash(current.getRight() != null ? current.getRight().getHash() : "", false);
            return true;
        }

        if (buildBranchProof(current.getRight(), targetBranch, proof)) {
            if (current.getLeft() != null) {
                proof.addHash(current.getLeft().getHash(), true);
            }
            return true;
        }

        return false;
    }

    private int findIndexInBranch(MerkleNode node, String targetHash, int offset, int branchWidth) {
        if (node == null) {
            return -1;
        }

        // If this is a leaf node
        if (node.isLeaf()) {
            if (node.getHash().equals(targetHash)) {
                return offset;
            }
            return -1;
        }

        // Calculate the theoretical width at this level
        int halfWidth = branchWidth / 2;

        // Search left subtree
        int leftIndex = findIndexInBranch(node.getLeft(), targetHash, offset, halfWidth);
        if (leftIndex != -1) {
            return leftIndex;
        }

        // Search right subtree
        return findIndexInBranch(node.getRight(), targetHash, offset + halfWidth, halfWidth);
    }

    private int calculateBranchWidth(MerkleNode branch) {
        if (branch == null) {
            return 0;
        }

        // For leaf nodes, return 1
        if (branch.isLeaf()) {
            return 1;
        }

        // For internal nodes, calculate the theoretical full width
        int height = calculateHeight(branch) - 1;
        return (int) Math.pow(2, height);
    }

    // Add new helper method to calculate proof height
    private int calculateProofHeight(MerkleNode branch) {
        int branchHeight = 0;
        MerkleNode current = root;
        String branchHash = branch.getHash();

        while (current != null && !current.getHash().equals(branchHash)) {
            branchHeight++;
            if (current.getLeft() != null && containsHash(current.getLeft(), branchHash)) {
                current = current.getLeft();
            } else if (current.getRight() != null && containsHash(current.getRight(), branchHash)) {
                current = current.getRight();
            } else {
                break;
            }
        }
        return branchHeight;
    }

    private void compareNodes(MerkleNode node1, MerkleNode node2, int offset, int branchWidth, Set<Integer> invalidIndices) {
        // Se entrambi i nodi sono null, sono strutturalmente equivalenti
        if (node1 == null && node2 == null) {
            return;
        }
        // Se uno dei nodi è null ma l'altro no, c'è una discrepanza strutturale
        if (node1 == null || node2 == null) {
            invalidIndices.add(offset);
            return;
        }

        // Se entrambi sono foglie, confronta gli hash
        if (node1.isLeaf() && node2.isLeaf()) {
            if (!node1.getHash().equals(node2.getHash())) {
                invalidIndices.add(offset); // Aggiungi l'indice al set
            }
            return;
        }

        // Se uno è foglia e l'altro no, c'è una discrepanza strutturale
        if (node1.isLeaf() || node2.isLeaf()) {
            throw new IllegalArgumentException("The trees have different structures");
        }

        // Divide la larghezza del ramo per navigare nei sottoalberi
        int halfWidth = branchWidth / 2;

        // Confronta il sottoalbero sinistro
        compareNodes(node1.getLeft(), node2.getLeft(), offset, halfWidth, invalidIndices);

        // Confronta il sottoalbero destro
        compareNodes(node1.getRight(), node2.getRight(), offset + halfWidth, halfWidth, invalidIndices);
    }

    // TODO inserire eventuali metodi privati per fini di implementazione
}
