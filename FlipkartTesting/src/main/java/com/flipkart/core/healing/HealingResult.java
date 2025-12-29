package com.flipkart.core.healing;

import java.util.List;

/**
 * POJO for healing response from the AI Healing Service
 */
public class HealingResult {

    private boolean success;
    private String healedSelector;
    private double confidence;
    private List<String> fallbacksAttempted;
    private long duration;
    private String error;

    // Default constructor for JSON deserialization
    public HealingResult() {
    }

    // Builder constructor
    private HealingResult(Builder builder) {
        this.success = builder.success;
        this.healedSelector = builder.healedSelector;
        this.confidence = builder.confidence;
        this.fallbacksAttempted = builder.fallbacksAttempted;
        this.duration = builder.duration;
        this.error = builder.error;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getHealedSelector() {
        return healedSelector;
    }

    public double getConfidence() {
        return confidence;
    }

    public List<String> getFallbacksAttempted() {
        return fallbacksAttempted;
    }

    public long getDuration() {
        return duration;
    }

    public String getError() {
        return error;
    }

    // Setters for JSON deserialization
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setHealedSelector(String healedSelector) {
        this.healedSelector = healedSelector;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setFallbacksAttempted(List<String> fallbacksAttempted) {
        this.fallbacksAttempted = fallbacksAttempted;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "HealingResult{" +
                "success=" + success +
                ", healedSelector='" + healedSelector + '\'' +
                ", confidence=" + confidence +
                ", duration=" + duration + "ms" +
                ", error='" + error + '\'' +
                '}';
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private String healedSelector;
        private double confidence;
        private List<String> fallbacksAttempted;
        private long duration;
        private String error;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder healedSelector(String selector) {
            this.healedSelector = selector;
            return this;
        }

        public Builder confidence(double confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder fallbacksAttempted(List<String> fallbacks) {
            this.fallbacksAttempted = fallbacks;
            return this;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public HealingResult build() {
            return new HealingResult(this);
        }
    }
}
