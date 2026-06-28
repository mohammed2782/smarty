package com.app.bussframework;

import java.util.HashMap;

public class QueueActionsParamsBean {
	private HashMap <Integer , String> rtnReasonMap;
	private HashMap <Integer , String> postponedOptionsMap ;
	private HashMap <Integer , String> postponedDateTimeToMap ;
	private HashMap <Integer, String> casesRmk;
	private HashMap <Integer,Integer> liaisonAgentsMap;
	private HashMap <Integer,Integer> dlvAgentMap; 
	private HashMap <Integer, Integer> backToBranchMap;
	private HashMap <Integer, Integer> partialQtyReturnMap;
	
	private HashMap <Integer , Double> newReceiptAmtIqdMap;
	private HashMap <Integer , Double> newReceiptAmtUsdMap;
	private HashMap <Integer, Integer> districtsMap;
	
	private HashMap <Integer, Integer> assignedAgentsMap;
	
	public HashMap<Integer, String> getRtnReasonMap() {
		return rtnReasonMap;
	}
	public void setRtnReasonMap(HashMap<Integer, String> rtnReasonMap) {
		this.rtnReasonMap = rtnReasonMap;
	}
	public HashMap<Integer, String> getPostponedOptionsMap() {
		return postponedOptionsMap;
	}
	public void setPostponedOptionsMap(HashMap<Integer, String> postponedOptionsMap) {
		this.postponedOptionsMap = postponedOptionsMap;
	}
	public HashMap<Integer, String> getPostponedDateTimeToMap() {
		return postponedDateTimeToMap;
	}
	public void setPostponedDateTimeToMap(HashMap<Integer, String> postponedDateTimeToMap) {
		this.postponedDateTimeToMap = postponedDateTimeToMap;
	}
	
	
	public HashMap <Integer, Integer> getBackToBranchMap() {
		return backToBranchMap;
	}
	public void setBackToBranchMap(HashMap <Integer, Integer> backToBranchMap) {
		this.backToBranchMap = backToBranchMap;
	}
	public HashMap <Integer,Integer> getLiaisonAgentsMap() {
		return liaisonAgentsMap;
	}
	public void setLiaisonAgentsMap(HashMap <Integer,Integer> liaisonAgentsMap) {
		this.liaisonAgentsMap = liaisonAgentsMap;
	}
	public HashMap <Integer,Integer> getDlvAgentMap() {
		return dlvAgentMap;
	}
	public void setDlvAgentMap(HashMap <Integer,Integer> dlvAgentMap) {
		this.dlvAgentMap = dlvAgentMap;
	}
	public HashMap <Integer, Integer> getPartialQtyReturnMap() {
		return partialQtyReturnMap;
	}
	public void setPartialQtyReturnMap(HashMap <Integer, Integer> partialQtyReturnMap) {
		this.partialQtyReturnMap = partialQtyReturnMap;
	}
	public HashMap <Integer, String> getCasesRmk() {
		return casesRmk;
	}
	public void setCasesRmk(HashMap <Integer, String> casesRmk) {
		this.casesRmk = casesRmk;
	}
	public HashMap <Integer , Double> getNewReceiptAmtIqdMap() {
		return newReceiptAmtIqdMap;
	}
	public void setNewReceiptAmtIqdMap(HashMap <Integer , Double> newReceiptAmtIqdMap) {
		this.newReceiptAmtIqdMap = newReceiptAmtIqdMap;
	}
	public HashMap <Integer , Double> getNewReceiptAmtUsdMap() {
		return newReceiptAmtUsdMap;
	}
	public void setNewReceiptAmtUsdMap(HashMap <Integer , Double> newReceiptAmtUsdMap) {
		this.newReceiptAmtUsdMap = newReceiptAmtUsdMap;
	}
	public HashMap <Integer, Integer> getDistrictsMap() {
		return districtsMap;
	}
	public void setDistrictsMap(HashMap <Integer, Integer> districtsMap) {
		this.districtsMap = districtsMap;
	}
	public HashMap <Integer, Integer> getAssignedAgentsMap() {
		return assignedAgentsMap;
	}
	public void setAssignedAgentsMap(HashMap <Integer, Integer> assignedAgentsMap) {
		this.assignedAgentsMap = assignedAgentsMap;
	}
	
}
