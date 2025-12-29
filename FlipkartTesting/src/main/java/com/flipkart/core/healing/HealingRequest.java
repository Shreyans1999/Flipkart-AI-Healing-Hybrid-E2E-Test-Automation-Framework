package com.flipkart.core.healing;

import java.util.List;

/**
 * POJO for healing request to the AI Healing Service
 */
public class HealingRequest {

    private String pageName;
    private String elementKey;
    private String failedSelector;
    private String domSnapshot;
    private String action;
    private List<String> fallbacks;

    // Default constructor
    public HealingRequest() {
    }

    // Full constructor
    public HealingRequest(String pageName, String elementKey, String failedSelector,
            String domSnapshot, String action, List<String> fallbacks) {
        this.pageName = pageName;
        this.elementKey = elementKey;
        this.failedSelector = failedSelector;
        this.domSnapshot = domSnapshot;
        this.action = action;
        this.fallbacks = fallbacks;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getElementKey() {
        return elementKey;
    }

    public void setElementKey(String elementKey) {
        this.elementKey = elementKey;
    }

    public String getFailedSelector() {
        return failedSelector;
    }

    public void setFailedSelector(String failedSelector) {
        this.failedSelector = failedSelector;
    }

    public String getDomSnapshot() {
        return domSnapshot;
    }

    public void setDomSnapshot(String domSnapshot) {
        this.domSnapshot = domSnapshot;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getFallbacks() {
        return fallbacks;
    }

    public void setFallbacks(List<String> fallbacks) {
        this.fallbacks = fallbacks;
    }

    public static class Builder {
        private String pageName;
        private String elementKey;
        private String failedSelector;
        private String domSnapshot;
        private String action = "click"; // default
        private List<String> fallbacks;

        public Builder pageName(String pageName) {
            this.pageName = pageName;
            return this;
        }

        public Builder elementKey(String elementKey) {
            this.elementKey = elementKey;
            return this;
        }

        public Builder failedSelector(String selector) {
            this.failedSelector = selector;
            return this;
        }

        public Builder domSnapshot(String dom) {
            this.domSnapshot = dom;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder fallbacks(List<String> fallbacks) {
            this.fallbacks = fallbacks;
            return this;
        }

        public HealingRequest build() {
            return new HealingRequest(pageName, elementKey, failedSelector, domSnapshot, action, fallbacks);
        }
    }
}
