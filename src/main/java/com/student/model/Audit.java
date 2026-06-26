package com.student.model;

import java.time.LocalDateTime;

public class Audit {

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Audit() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}   