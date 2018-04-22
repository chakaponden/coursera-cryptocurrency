import java.util.ArrayList;

public class TxHandler {

	// trusted validated transactions pool
	public UTXOPool trustedPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
    	trustedPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
    	double sumInput = 0.0;
    	double sumOutput = 0.0;
    	int index = 0;
    	ArrayList<UTXO> approvedUTXO = new ArrayList<UTXO>();
    	for(Transaction.Input transactionIn : tx.getInputs())
    	{
    		UTXO currentUTXO = new UTXO(transactionIn.prevTxHash, transactionIn.outputIndex);
    		// check for (3): no UTXO is claimed multiple times by {@code tx}
    		// check for (1): all outputs claimed by {@code tx} must be in the current UTXO pool
    		// check for (2): the signatures on each input of {@code tx} must be valid
    		if(approvedUTXO.contains(currentUTXO) ||
    				!trustedPool.contains(currentUTXO) ||
    				Crypto.verifySignature(trustedPool.getTxOutput(currentUTXO).address,
    						tx.getRawDataToSign(index),
    	    				transactionIn.signature))
    		{
    			return false;
    		}
    		sumInput += trustedPool.getTxOutput(currentUTXO).value;
    		++index;
    	}
       	for(Transaction.Output transactionOut : tx.getOutputs())
    	{
    		// check for (4): all of {@code tx}s output values must be non-negative
    		if(transactionOut.value < 0)
    		{
    			return false;
    		}
    		sumOutput += transactionOut.value;
    	}
		// check for (5): the sum of {@code tx}s input values is greater
    	// than or equal to the sum of its output values
    	if(sumInput < sumOutput)
    	{
    		return false;
    	}
    	return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
    	ArrayList<Transaction> validTxs = new ArrayList<Transaction>();
    	for(Transaction currentTx : possibleTxs)
    	{
    		if(handleTx(currentTx))
    		{
    			// mark currentTx as validated transaction
    			validTxs.add(currentTx);
    		}
    	}
    	Transaction[] validTxsTmp = new Transaction[validTxs.size()];
    	validTxsTmp = validTxs.toArray(validTxsTmp);
		return validTxsTmp;
    }

    /**
     * Handles transaction, checking transaction for correctness,
     * updating the current UTXO pool as appropriate.
     * @return true if: {@code tx} is valid and false otherwise.
     */
    public boolean handleTx(Transaction tx) {
    	if(!isValidTx(tx))
    	{
    		return false;
    	}
    	// TODO: update UTXO pool
    	return true;
    }

}
