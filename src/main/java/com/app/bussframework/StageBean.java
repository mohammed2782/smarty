package com.app.bussframework;

import java.util.ArrayList;
import java.util.HashMap;

public class StageBean {
	private String stageColor;
	private String stageCode;
	private String stageName;
	private ArrayList<StepBean> stepsList;
	private HashMap<String,StepBean> stepsMap;
	private int currentCasesCtr;
	private String stageIcon;
	
	public String getStageCode() {
		return stageCode;
	}
	public void setStageCode(String stageCode) {
		this.stageCode = stageCode;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public ArrayList<StepBean> getStepsList() {
		return stepsList;
	}
	public void setStepsList(ArrayList<StepBean> stepsList) {
		this.stepsList = stepsList;
	}
	public String getStageColor() {
		return stageColor;
	}
	public void setStageColor(String stageColor) {
		this.stageColor = stageColor;
	}
	public int getCurrentCasesCtr() {
		return currentCasesCtr;
	}
	public void setCurrentCasesCtr(int currentCasesCtr) {
		this.currentCasesCtr = currentCasesCtr;
	}
	public String getStageIcon() {
		return stageIcon;
	}
	public void setStageIcon(String stageIcon) {
		this.stageIcon = stageIcon;
	}
	public HashMap<String,StepBean> getStepsMap() {
		return stepsMap;
	}
	public void setStepsMap(HashMap<String,StepBean> stepsMap) {
		this.stepsMap = stepsMap;
	}
}
