package com.app.cases;

public class CaseInformation {
	
	private String receiverName;
	private String receiverHp1;
	private String receiverHp2;
	
	private String senderHp;
	private String senderName;
	private int senderId;
	private int senderPmtId;
	private int masterSenderId;
	private String masterSenderHp1;
	private int caseid;
	
	private String productInfo;
	private String productCodes;
	private int qty;
	private int rtnQty;
	private int partialRtn_Qty;
	private String state;
	private String advancedPaymentStatus;
	private int advancedPmtId;
	private int district;
	private String rural;
	private String locationDetails;
	private String rmk;
	private String fragile;
	
	private double receiptAmtIqd;
	private double receiptAmtUsd;
	private double receiptAmtB4Change;
	private double receiptAmtUsdB4Change;
	
	private double shipmentCharge;
	private double netPrice;
	private double agentShare;

	private String whenItWasScannedByBarCodel;
	private int pickupAgent;
	private int pickUpAgentPmtId;
	private String branchCode;
	private String custReceiptNoOri;
	private int smarty_new_row_seq;
	private String status;
	private String createddt;
	
	private String shipmentChargesPaidByCustomer;
	private String shipmentChargesPaidBysender;
	private String changedPrice;
	private String changedPriceUsd;
	
	private int dlvAgentManifestId;
	
	private int dlvAgentId;
	
	private String dlvAgentManifestDate;
	private String assignedAgentName;
	private String assignedAgentCode;
	private int agentRtnId;
	private int dlvAgentPmtId;
	
	private String action;
	private int actionTakenBy;
	private int origintingBranch;
	private String originatinBranchName;
	private int currentBranch;
	private String currentBranchName;
	private String stepName;
	private String stepCode;
	private String stageName;
	private String stageCode;
	private String specialCase;
	private String receiverAddress;
	private int manifestId;
	// latest chain info
	private int latestChainId;
	
	private int currentChainId;
	private int parentChainId;
	private String queueEnterDate;
	private int partialRtnCCToBranch;
	
	private int custReturnId;
	private int pickupAgentRtnId;
	
	private String allowRtnCustRtn;
	private double pickUpAgentShare;
	
	private int parentId;
	private int parentOf;
	
	private int currentBranchRtnManifestId = 0;
	
	public int getLiaisonAgent() {
		return liaisonAgent;
	}
	public void setLiaisonAgent(int liaisonAgent) {
		this.liaisonAgent = liaisonAgent;
	}
	public int getPathId() {
		return pathId;
	}
	public void setPathId(int pathId) {
		this.pathId = pathId;
	}

	private String stateName;
	private int toBranchCode;
	private int fromBranchCode;
	private int liaisonAgent;
	private int pathId;
	
	private String paidDeliveryCostInAdvance;
	
	public int getToBranchCode() {
		return toBranchCode;
	}
	public void setToBranchCode(int toBranchCode) {
		this.toBranchCode = toBranchCode;
	}
	public int getFromBranchCode() {
		return fromBranchCode;
	}
	public void setFromBranchCode(int fromBranchCode) {
		this.fromBranchCode = fromBranchCode;
	}
	
	
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
	public String getLocationDetails() {
		return locationDetails;
	}
	public void setLocationDetails(String locationDetails) {
		this.locationDetails = locationDetails;
	}
	public String getRmk() {
		return rmk;
	}
	public void setRmk(String rmk) {
		this.rmk = rmk;
	}

	
	public double getShipmentCharge() {
		return shipmentCharge;
	}
	public void setShipmentCharge(double shipmentCharge) {
		this.shipmentCharge = shipmentCharge;
	}
	public int getCaseid() {
		return caseid;
	}
	public void setCaseid(int caseid) {
		this.caseid = caseid;
	}
	
	public String getRural() {
		return rural;
	}
	public void setRural(String rural) {
		this.rural = rural;
	}
	public String getFragile() {
		return fragile;
	}
	public void setFragile(String fragile) {
		this.fragile = fragile;
	}
	
	
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getCustReceiptNoOri() {
		return custReceiptNoOri;
	}
	public void setCustReceiptNoOri(String custReceiptNoOri) {
		this.custReceiptNoOri = custReceiptNoOri;
	}
	
	
	public int getSmarty_new_row_seq() {
		return smarty_new_row_seq;
	}
	public void setSmarty_new_row_seq(int smarty_new_row_seq) {
		this.smarty_new_row_seq = smarty_new_row_seq;
	}
	public double getAgentShare() {
		return agentShare;
	}
	public void setAgentShare(double agentShare) {
		this.agentShare = agentShare;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreateddt() {
		return createddt;
	}
	public void setCreateddt(String createddt) {
		this.createddt = createddt;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	

	
	public String getShipmentChargesPaidByCustomer() {
		return shipmentChargesPaidByCustomer;
	}
	public void setShipmentChargesPaidByCustomer(String shipmentChargesPaidByCustomer) {
		this.shipmentChargesPaidByCustomer = shipmentChargesPaidByCustomer;
	}
	public String getShipmentChargesPaidBysender() {
		return shipmentChargesPaidBysender;
	}
	public void setShipmentChargesPaidBysender(String shipmentChargesPaidBysender) {
		this.shipmentChargesPaidBysender = shipmentChargesPaidBysender;
	}
	public String getAdvancedPaymentStatus() {
		return advancedPaymentStatus;
	}
	public void setAdvancedPaymentStatus(String advancedPaymentStatus) {
		this.advancedPaymentStatus = advancedPaymentStatus;
	}
	public int getAdvancedPmtId() {
		return advancedPmtId;
	}
	public void setAdvancedPmtId(int advancedPmtId) {
		this.advancedPmtId = advancedPmtId;
	}
	
	public int getPickupAgent() {
		return pickupAgent;
	}
	public void setPickupAgent(int pickupAgent) {
		this.pickupAgent = pickupAgent;
	}
	/**
	 * @return the receiptAmtB4Change
	 */
	public double getReceiptAmtB4Change() {
		return receiptAmtB4Change;
	}
	/**
	 * @param receiptAmtB4Change the receiptAmtB4Change to set
	 */
	public void setReceiptAmtB4Change(double receiptAmtB4Change) {
		this.receiptAmtB4Change = receiptAmtB4Change;
	}
	/**
	 * @return the changedPrice
	 */
	public String getChangedPrice() {
		return changedPrice;
	}
	/**
	 * @param changedPrice the changedPrice to set
	 */
	public void setChangedPrice(String changedPrice) {
		this.changedPrice = changedPrice;
	}
	/**
	 * @return the assignedAgentName
	 */
	public String getAssignedAgentName() {
		return assignedAgentName;
	}
	/**
	 * @param assignedAgentName the assignedAgentName to set
	 */
	public void setAssignedAgentName(String assignedAgentName) {
		this.assignedAgentName = assignedAgentName;
	}
	/**
	 * @return the assignedAgentCode
	 */
	public String getAssignedAgentCode() {
		return assignedAgentCode;
	}
	/**
	 * @param assignedAgentCode the assignedAgentCode to set
	 */
	public void setAssignedAgentCode(String assignedAgentCode) {
		this.assignedAgentCode = assignedAgentCode;
	}
	/**
	 * @return the stepName
	 */
	public String getStepName() {
		return stepName;
	}
	/**
	 * @param stepName the stepName to set
	 */
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	/**
	 * @return the stepCode
	 */
	public String getStepCode() {
		return stepCode;
	}
	/**
	 * @param stepCode the stepCode to set
	 */
	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}
	/**
	 * @return the specialCase
	 */
	public String getSpecialCase() {
		return specialCase;
	}
	/**
	 * @param specialCase the specialCase to set
	 */
	public void setSpecialCase(String specialCase) {
		this.specialCase = specialCase;
	}
	public int getLatestChainId() {
		return latestChainId;
	}
	public void setLatestChainId(int latestChainId) {
		this.latestChainId = latestChainId;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	
	public String getSenderHp() {
		return senderHp;
	}
	public void setSenderHp(String senderHp) {
		this.senderHp = senderHp;
	}
	
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public String getStageCode() {
		return stageCode;
	}
	public void setStageCode(String stageCode) {
		this.stageCode = stageCode;
	}
	public int getManifestId() {
		return manifestId;
	}
	public void setManifestId(int manifestId) {
		this.manifestId = manifestId;
	}
	public String getReceiverHp1() {
		return receiverHp1;
	}
	public void setReceiverHp1(String receiverHp1) {
		this.receiverHp1 = receiverHp1;
	}
	public String getReceiverHp2() {
		return receiverHp2;
	}
	public void setReceiverHp2(String receiverHp2) {
		this.receiverHp2 = receiverHp2;
	}
	public String getProductInfo() {
		return productInfo;
	}
	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}
	public double getNetPrice() {
		return netPrice;
	}
	public void setNetPrice(double netPrice) {
		this.netPrice = netPrice;
	}
	public int getDistrict() {
		return district;
	}
	public void setDistrict(int district) {
		this.district = district;
	}
	public String getProductCodes() {
		return productCodes;
	}
	public void setProductCodes(String productCodes) {
		this.productCodes = productCodes;
	}
	public int getMasterSenderId() {
		return masterSenderId;
	}
	public void setMasterSenderId(int masterSenderId) {
		this.masterSenderId = masterSenderId;
	}
	public int getOrigintingBranch() {
		return origintingBranch;
	}
	public void setOrigintingBranch(int origintingBranch) {
		this.origintingBranch = origintingBranch;
	}
	public int getCurrentBranch() {
		return currentBranch;
	}
	public void setCurrentBranch(int currentBranch) {
		this.currentBranch = currentBranch;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getActionTakenBy() {
		return actionTakenBy;
	}
	public void setActionTakenBy(int actionTakenBy) {
		this.actionTakenBy = actionTakenBy;
	}
	public int getCurrentChainId() {
		return currentChainId;
	}
	public void setCurrentChainId(int currentChainId) {
		this.currentChainId = currentChainId;
	}
	public int getParentChainId() {
		return parentChainId;
	}
	public void setParentChainId(int parentChainId) {
		this.parentChainId = parentChainId;
	}
	public int getDlvAgentManifestId() {
		return dlvAgentManifestId;
	}
	public void setDlvAgentManifestId(int dlvAgentManifestId) {
		this.dlvAgentManifestId = dlvAgentManifestId;
	}
	public String getDlvAgentManifestDate() {
		return dlvAgentManifestDate;
	}
	public void setDlvAgentManifestDate(String dlvAgentManifestDate) {
		this.dlvAgentManifestDate = dlvAgentManifestDate;
	}
	public String getQueueEnterDate() {
		return queueEnterDate;
	}
	public void setQueueEnterDate(String queueEnterDate) {
		this.queueEnterDate = queueEnterDate;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	public String getOriginatinBranchName() {
		return originatinBranchName;
	}
	public void setOriginatinBranchName(String originatinBranchName) {
		this.originatinBranchName = originatinBranchName;
	}
	public int getAgentRtnId() {
		return agentRtnId;
	}
	public void setAgentRtnId(int agentRtnId) {
		this.agentRtnId = agentRtnId;
	}
	public int getPartialRtnCCToBranch() {
		return partialRtnCCToBranch;
	}
	public void setPartialRtnCCToBranch(int partialRtnCCToBranch) {
		this.partialRtnCCToBranch = partialRtnCCToBranch;
	}
	public int getPartialRtn_Qty() {
		return partialRtn_Qty;
	}
	public void setPartialRtn_Qty(int partialRtn_Qty) {
		this.partialRtn_Qty = partialRtn_Qty;
	}
	public String getCurrentBranchName() {
		return currentBranchName;
	}
	public void setCurrentBranchName(String currentBranchName) {
		this.currentBranchName = currentBranchName;
	}
	public int getCustReturnId() {
		return custReturnId;
	}
	public void setCustReturnId(int custReturnId) {
		this.custReturnId = custReturnId;
	}
	public int getPickupAgentRtnId() {
		return pickupAgentRtnId;
	}
	public void setPickupAgentRtnId(int pickupAgentRtnId) {
		this.pickupAgentRtnId = pickupAgentRtnId;
	}
	public int getRtnQty() {
		return rtnQty;
	}
	public void setRtnQty(int rtnQty) {
		this.rtnQty = rtnQty;
	}
	public String getAllowRtnCustRtn() {
		return allowRtnCustRtn;
	}
	public void setAllowRtnCustRtn(String allowRtnCustRtn) {
		this.allowRtnCustRtn = allowRtnCustRtn;
	}
	public String getMasterSenderHp1() {
		return masterSenderHp1;
	}
	public void setMasterSenderHp1(String masterSenderHp1) {
		this.masterSenderHp1 = masterSenderHp1;
	}
	public double getPickUpAgentShare() {
		return pickUpAgentShare;
	}
	public void setPickUpAgentShare(double pickUpAgentShare) {
		this.pickUpAgentShare = pickUpAgentShare;
	}
	public int getPickUpAgentPmtId() {
		return pickUpAgentPmtId;
	}
	public void setPickUpAgentPmtId(int pickUpAgentPmtId) {
		this.pickUpAgentPmtId = pickUpAgentPmtId;
	}
	public int getSenderPmtId() {
		return senderPmtId;
	}
	public void setSenderPmtId(int senderPmtId) {
		this.senderPmtId = senderPmtId;
	}
	public int getDlvAgentPmtId() {
		return dlvAgentPmtId;
	}
	public void setDlvAgentPmtId(int dlvAgentPmtId) {
		this.dlvAgentPmtId = dlvAgentPmtId;
	}
	public double getReceiptAmtUsd() {
		return receiptAmtUsd;
	}
	public void setReceiptAmtUsd(double receiptAmtUsd) {
		this.receiptAmtUsd = receiptAmtUsd;
	}
	public double getReceiptAmtIqd() {
		return receiptAmtIqd;
	}
	public void setReceiptAmtIqd(double receiptAmtIqd) {
		this.receiptAmtIqd = receiptAmtIqd;
	}
	public String getChangedPriceUsd() {
		return changedPriceUsd;
	}
	public void setChangedPriceUsd(String changedPriceUsd) {
		this.changedPriceUsd = changedPriceUsd;
	}
	public double getReceiptAmtUsdB4Change() {
		return receiptAmtUsdB4Change;
	}
	public void setReceiptAmtUsdB4Change(double receiptAmtUsdB4Change) {
		this.receiptAmtUsdB4Change = receiptAmtUsdB4Change;
	}
	public String getPaidDeliveryCostInAdvance() {
		return paidDeliveryCostInAdvance;
	}
	public void setPaidDeliveryCostInAdvance(String paidDeliveryCostInAdvance) {
		this.paidDeliveryCostInAdvance = paidDeliveryCostInAdvance;
	}
	public int getDlvAgentId() {
		return dlvAgentId;
	}
	public void setDlvAgentId(int dlvAgentId) {
		this.dlvAgentId = dlvAgentId;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getParentOf() {
		return parentOf;
	}
	public void setParentOf(int parentOf) {
		this.parentOf = parentOf;
	}
	public int getCurrentBranchRtnManifestId() {
		return currentBranchRtnManifestId;
	}
	public void setCurrentBranchRtnManifestId(int currentBranchRtnManifestId) {
		this.currentBranchRtnManifestId = currentBranchRtnManifestId;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getWhenItWasScannedByBarCodel() {
		return whenItWasScannedByBarCodel;
	}
	public void setWhenItWasScannedByBarCodel(String whenItWasScannedByBarCodel) {
		this.whenItWasScannedByBarCodel = whenItWasScannedByBarCodel;
	}

}
