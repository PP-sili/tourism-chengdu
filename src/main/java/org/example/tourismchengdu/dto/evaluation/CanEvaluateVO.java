package org.example.tourismchengdu.dto.evaluation;

public class CanEvaluateVO {

    private boolean canEvaluate;
    private boolean alreadyEvaluated;
    private String reason;

    public boolean isCanEvaluate() {
        return canEvaluate;
    }

    public void setCanEvaluate(boolean canEvaluate) {
        this.canEvaluate = canEvaluate;
    }

    public boolean isAlreadyEvaluated() {
        return alreadyEvaluated;
    }

    public void setAlreadyEvaluated(boolean alreadyEvaluated) {
        this.alreadyEvaluated = alreadyEvaluated;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
