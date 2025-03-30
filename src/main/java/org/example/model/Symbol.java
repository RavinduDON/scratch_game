package org.example.model;

public class Symbol {
    private double rewardMultiplier;
    private String type;
    private String impact;
    private double extra;

    public Symbol() {
    }

    public Symbol(double rewardMultiplier, String type, String impact, double extra) {
        this.rewardMultiplier = rewardMultiplier;
        this.type = type;
        this.impact = impact;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "rewardMultiplier=" + rewardMultiplier +
                ", type='" + type + '\'' +
                ", impact='" + impact + '\'' +
                ", extra=" + extra +
                '}';
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public void setRewardMultiplier(double rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public double getExtra() {
        return extra;
    }

    public void setExtra(double extra) {
        this.extra = extra;
    }
}
