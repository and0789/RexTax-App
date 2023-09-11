package com.andreseptian.entities;

public class CaseIdentity {

    private String id;

    private String investigatorsName;

    private String handledCase;

    private String caseDescription;

    public CaseIdentity(String id, String investigatorsName, String handledCase, String caseDescription) {
        this.id = id;
        this.investigatorsName = investigatorsName;
        this.handledCase = handledCase;
        this.caseDescription = caseDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvestigatorsName() {
        return investigatorsName;
    }

    public void setInvestigatorsName(String investigatorsName) {
        this.investigatorsName = investigatorsName;
    }

    public String getHandledCase() {
        return handledCase;
    }

    public void setHandledCase(String handledCase) {
        this.handledCase = handledCase;
    }

    public String getCaseDescription() {
        return caseDescription;
    }

    public void setCaseDescription(String caseDescription) {
        this.caseDescription = caseDescription;
    }

}
