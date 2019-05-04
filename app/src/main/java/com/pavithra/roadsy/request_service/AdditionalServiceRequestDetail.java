package com.pavithra.roadsy.request_service;

import java.io.Serializable;

public class AdditionalServiceRequestDetail implements Serializable {
    private String description;
    private boolean isToolsAvailable;

    public AdditionalServiceRequestDetail() {
    }

    public AdditionalServiceRequestDetail(String description, boolean isToolsAvailable) {
        this.description = description;
        this.isToolsAvailable = isToolsAvailable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isToolsAvailable() {
        return isToolsAvailable;
    }

    public void setToolsAvailable(boolean toolsAvailable) {
        isToolsAvailable = toolsAvailable;
    }
}
