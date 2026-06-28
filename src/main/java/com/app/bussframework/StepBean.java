package com.app.bussframework;

import java.util.ArrayList;
import java.util.HashMap;

public class StepBean {
	private int stpId;
	private int stpSeq;
	private String stepCode;
	private String stpName;
	private String stepIcon;
	private String stepColor;
	private int currentCasesCtr;
	private String countCases;
	private String stepFontColor;
	private ArrayList<StepsDecisionsBean> descisionsList;
	private HashMap<String,StepsDecisionsBean> decisionsMap;
	public int getStpId() {
		return stpId;
	}
	public void setStpId(int stpId) {
		this.stpId = stpId;
	}
	public int getStpSeq() {
		return stpSeq;
	}
	public void setStpSeq(int stpSeq) {
		this.stpSeq = stpSeq;
	}
	public String getStepCode() {
		return stepCode;
	}
	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}
	public String getStpName() {
		return stpName;
	}
	public void setStpName(String stpName) {
		this.stpName = stpName;
	}
	public String getStepIcon() {
		return stepIcon;
	}
	public void setStepIcon(String stepIcon) {
		this.stepIcon = stepIcon;
	}
	public String getStepColor() {
		return stepColor;
	}
	public void setStepColor(String stepColor) {
		this.stepColor = stepColor;
	}
	public int getCurrentCasesCtr() {
		return currentCasesCtr;
	}
	public void setCurrentCasesCtr(int currentCasesCtr) {
		this.currentCasesCtr = currentCasesCtr;
	}
	public String getStepFontColor() {
		return stepFontColor;
	}
	public void setStepFontColor(String stepFontColor) {
		this.stepFontColor = stepFontColor;
	}
	public String getCountCases() {
		return countCases;
	}
	public void setCountCases(String countCases) {
		this.countCases = countCases;
	}
	public ArrayList<StepsDecisionsBean> getDescisionsList() {
		return descisionsList;
	}
	public void setDescisionsList(ArrayList<StepsDecisionsBean> descisionsList) {
		this.descisionsList = descisionsList;
	}
	public HashMap<String,StepsDecisionsBean> getDecisionsMap() {
		return decisionsMap;
	}
	public void setDecisionsMap(HashMap<String,StepsDecisionsBean> decisionsMap) {
		this.decisionsMap = decisionsMap;
	}
	
}
