package com.app.bussframework;

public  class SingleQueueFactory    {
	public SingleQueue getSingleQueuObj(String stageCode, String stepCode) {
	
		if (stageCode.equalsIgnoreCase("INIT") && stepCode.equalsIgnoreCase("NEWINSTORE"))
			return new SingleQueue_INIT_NEWINSTORE();
		else if (stageCode.equalsIgnoreCase("INIT") && stepCode.equalsIgnoreCase("PRINTMANIFEST"))
			return new SingleQueue_INIT_PRINTMANIFEST();
		
		else if (stageCode.equalsIgnoreCase("AGENTOP")) {
			return new SingleQueue_AGENTOP();
		}
		
		else if (stageCode.equalsIgnoreCase("BRANCHES") && stepCode.equalsIgnoreCase("MANIFEST_BRANCHES"))
			return new SingleQueue_BRANCHES_MANIFEST_BRANCHES();
		else if (stageCode.equalsIgnoreCase("BRANCHES") && stepCode.equalsIgnoreCase("LIAISONAGT_NEWONWAY"))
			return new SingleQueue_BRANCHES_LIAISONAGT_NEWONWAY();
		else if (stageCode.equalsIgnoreCase("BRANCHES") && stepCode.equalsIgnoreCase("RTN_INSTORE_WAITLIAISON"))
			return new SingleQueue_BRANCHES_RTN_INSTORE_WAITLIAISON();
		else if (stageCode.equalsIgnoreCase("BRANCHES") && stepCode.equalsIgnoreCase("RTN_MANIFEST_LIAISON"))
			return new SingleQueue_BRANCHES_RTN_MANIFEST_LIAISON();
		else if (stageCode.equalsIgnoreCase("BRANCHES") && stepCode.equalsIgnoreCase("RTN_WITHLIAISONAGENT"))
			return new SingeQueue_BRANCHES_RTN_WITHLIAISONAGENT();
		else if (stageCode.equalsIgnoreCase("CNCL") && stepCode.equalsIgnoreCase("RTN_INSTORE"))
			return new SingleQueue_CNCL_RTN_INSTORE();
		else if (stageCode.equalsIgnoreCase("DLV")) 
			return new SingleQueue_DLV();
		return new SingleQueue();
	}
}
