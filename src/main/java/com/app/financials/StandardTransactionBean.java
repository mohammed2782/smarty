package com.app.financials;

public class StandardTransactionBean {
//	select trans_id, trans_operationentity, trans_entity_id, trans_operationcat,
//	trans_operationcode, trans_initiated_in_branch_id, trans_createdby,trans_createddt, trans_rmk, 
//	trans_amount_iqd, trans_amount_usd, 
//	trans_entity_share_iqd,
//	trans_receipts_amt_iqd, trans_receipts_amt_usd,
//	trans_amount_paid_actually_iqd, trans_amount_paid_actually_usd,
//	trans_credit_iqd, trans_debit_iqd,
//	trans_credit_usd, trans_debit_usd, 
//	trans_payer_box, trans_payer_box_transactionid,
//	trans_did_branch_receive, trans_receiver_branch_id, trans_receiver_rmk , trans_receiver_box, trans_receiver_box_transactionid,
//	trans_amount_received_actually_iqd, trans_amount_received_actually_usd,
//	trans_deleted, trans_deletedby, trans_deleteddt
//	 From p_fin_transactions where trans_id = ? and trans_operationentity=? and trans_entity_id=?
	private int Id;
	private String entityString;
	private FinOperationEntity entity;
	private int entityId; // agent id, customer id, branch id, pickup agent id..etc
	private long entityShareIqd;
	
	private FinOperationCategory category;
	private String categoryString;
	
	private FinOperationCode code;
	private String codeString;
	
	private int initiatedInBranchId;
	private String initiatedInBranchName;
	private int receiverBranchId;
	private String receiverBranchName;
	
	
	private long transactionAmountIqd;
	private long transactionAmountUsd;
	
	private long receiptsAmtIqd;
	private long receiptsAmtUsd;
	
	private long amountPaidActuallyIqd;
	private long amountPaidActuallyUsd;
	
	private long amountRecievedActuallyIqd;
	private long amountRecievedActuallyUsd;
	
	private long creditIqd;
	private long creditUsd;
	
	private long debitIqd;
	private long debitUsd;
	
	private int PayerBox;
	private int PayerBoxTransactionId;
	
	private boolean branchReceivedPayment;
	private int receiverBox;
	private int receiverBoxTransactionId;
	private String receiverRemarks;
	private int receivedBy;
	private String receivedDateTime;
	
	private String whichScreen;
	
	private String remarks;
	
	private String createdDateTime;
	private int createdBy;
	
	private boolean deleted;
	private int deletedBy;
	private String deletedDateTime;
	
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public int getDeletedBy() {
		return deletedBy;
	}
	public void setDeletedBy(int deletedBy) {
		this.deletedBy = deletedBy;
	}
	public String getDeletedDateTime() {
		return deletedDateTime;
	}
	public void setDeletedDateTime(String deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public long getReceiptsAmtIqd() {
		return receiptsAmtIqd;
	}
	public void setReceiptsAmtIqd(long receiptsAmtIqd) {
		this.receiptsAmtIqd = receiptsAmtIqd;
	}
	public long getReceiptsAmtUsd() {
		return receiptsAmtUsd;
	}
	public void setReceiptsAmtUsd(long receiptsAmtUsd) {
		this.receiptsAmtUsd = receiptsAmtUsd;
	}
	public long getAmountPaidActuallyIqd() {
		return amountPaidActuallyIqd;
	}
	public void setAmountPaidActuallyIqd(long amountPaidActuallyIqd) {
		this.amountPaidActuallyIqd = amountPaidActuallyIqd;
	}
	public long getAmountPaidActuallyUsd() {
		return amountPaidActuallyUsd;
	}
	public void setAmountPaidActuallyUsd(long amountPaidActuallyUsd) {
		this.amountPaidActuallyUsd = amountPaidActuallyUsd;
	}
	public long getAmountRecievedActuallyIqd() {
		return amountRecievedActuallyIqd;
	}
	public void setAmountRecievedActuallyIqd(long amountRecievedActuallyIqd) {
		this.amountRecievedActuallyIqd = amountRecievedActuallyIqd;
	}
	public long getAmountRecievedActuallyUsd() {
		return amountRecievedActuallyUsd;
	}
	public void setAmountRecievedActuallyUsd(long amountRecievedActuallyUsd) {
		this.amountRecievedActuallyUsd = amountRecievedActuallyUsd;
	}
	public long getCreditIqd() {
		return creditIqd;
	}
	public void setCreditIqd(long creditIqd) {
		this.creditIqd = creditIqd;
	}
	public long getCreditUsd() {
		return creditUsd;
	}
	public void setCreditUsd(long creditUsd) {
		this.creditUsd = creditUsd;
	}
	public long getDebitIqd() {
		return debitIqd;
	}
	public void setDebitIqd(long debitIqd) {
		this.debitIqd = debitIqd;
	}
	public long getDebitUsd() {
		return debitUsd;
	}
	public void setDebitUsd(long debitUsd) {
		this.debitUsd = debitUsd;
	}
	public int getPayerBox() {
		return PayerBox;
	}
	public void setPayerBox(int payerBox) {
		PayerBox = payerBox;
	}
	public int getPayerBoxTransactionId() {
		return PayerBoxTransactionId;
	}
	public void setPayerBoxTransactionId(int payerBoxTransactionId) {
		PayerBoxTransactionId = payerBoxTransactionId;
	}
	public int getReceiverBox() {
		return receiverBox;
	}
	public void setReceiverBox(int receiverBox) {
		this.receiverBox = receiverBox;
	}
	public int getReceiverBoxTransactionId() {
		return receiverBoxTransactionId;
	}
	public void setReceiverBoxTransactionId(int receiverBoxTransactionId) {
		this.receiverBoxTransactionId = receiverBoxTransactionId;
	}
	public FinOperationEntity getEntity() {
		return entity;
	}
	public FinOperationCategory getCategory() {
		return category;
	}
	public FinOperationCode getCode() {
		return code;
	}
	
	public String getEntityString() {
		return entityString;
	}
	public void setEntityString(String entityString) {
		this.entityString = entityString;
	}
	public String getCategoryString() {
		return categoryString;
	}
	public void setCategoryString(String categoryString) {
		this.categoryString = categoryString;
	}
	public String getCodeString() {
		return codeString;
	}
	public void setCodeString(String codeString) {
		this.codeString = codeString;
	}
	public void setEntity(FinOperationEntity entity) {
		this.entity = entity;
	}
	public void setCategory(FinOperationCategory category) {
		this.category = category;
	}
	public void setCode(FinOperationCode code) {
		this.code = code;
	}
	public String getWhichScreen() {
		return whichScreen;
	}
	public void setWhichScreen(String whichScreen) {
		this.whichScreen = whichScreen;
	}
	public int getReceiverBranchId() {
		return receiverBranchId;
	}
	public void setReceiverBranchId(int receiverBranchId) {
		this.receiverBranchId = receiverBranchId;
	}
	public long getTransactionAmountIqd() {
		return transactionAmountIqd;
	}
	public void setTransactionAmountIqd(long transactionAmountIqd) {
		this.transactionAmountIqd = transactionAmountIqd;
	}
	public long getTransactionAmountUsd() {
		return transactionAmountUsd;
	}
	public void setTransactionAmountUsd(long transactionAmountUsd) {
		this.transactionAmountUsd = transactionAmountUsd;
	}
	public int getEntityId() {
		return entityId;
	}
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public long getEntityShareIqd() {
		return entityShareIqd;
	}
	public void setEntityShareIqd(long entityShareIqd) {
		this.entityShareIqd = entityShareIqd;
	}
	public int getInitiatedInBranchId() {
		return initiatedInBranchId;
	}
	public void setInitiatedInBranchId(int initiatedInBranchId) {
		this.initiatedInBranchId = initiatedInBranchId;
	}
	public boolean isBranchReceivedPayment() {
		return branchReceivedPayment;
	}
	public void setBranchReceivedPayment(boolean branchReceivedPayment) {
		this.branchReceivedPayment = branchReceivedPayment;
	}
	public String getReceiverRemarks() {
		return receiverRemarks;
	}
	public void setReceiverRemarks(String receiverRemarks) {
		this.receiverRemarks = receiverRemarks;
	}
	public int getReceivedBy() {
		return receivedBy;
	}
	public void setReceivedBy(int receivedBy) {
		this.receivedBy = receivedBy;
	}
	public String getReceivedDateTime() {
		return receivedDateTime;
	}
	public void setReceivedDateTime(String receivedDateTime) {
		this.receivedDateTime = receivedDateTime;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public String getInitiatedInBranchName() {
		return initiatedInBranchName;
	}
	public void setInitiatedInBranchName(String initiatedInBranchName) {
		this.initiatedInBranchName = initiatedInBranchName;
	}
	public String getReceiverBranchName() {
		return receiverBranchName;
	}
	public void setReceiverBranchName(String receiverBranchName) {
		this.receiverBranchName = receiverBranchName;
	}
}
