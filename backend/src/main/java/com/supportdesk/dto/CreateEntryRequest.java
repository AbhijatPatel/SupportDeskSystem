package com.supportdesk.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateEntryRequest {

    @NotBlank(message = "Content cannot be empty")
    private String content;

    @NotBlank(message = "Author is required")
    private String author;

    public CreateEntryRequest() {
    }

    public CreateEntryRequest(String content, String author) {
        this.content = content;
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
