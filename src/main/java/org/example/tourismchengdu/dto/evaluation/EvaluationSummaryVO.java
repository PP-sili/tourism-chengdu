package org.example.tourismchengdu.dto.evaluation;

public class EvaluationSummaryVO {

    private long goodCount;
    private long badCount;
    private long suggestionCount;
    private long totalCount;

    public long getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(long goodCount) {
        this.goodCount = goodCount;
    }

    public long getBadCount() {
        return badCount;
    }

    public void setBadCount(long badCount) {
        this.badCount = badCount;
    }

    public long getSuggestionCount() {
        return suggestionCount;
    }

    public void setSuggestionCount(long suggestionCount) {
        this.suggestionCount = suggestionCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
