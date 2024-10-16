package com.compiler.app.payload;

public class CodeRequest {
    private String language;
    private String code;
    private String fileName;// Add this field

    // Getters and setters

    public String getLanguage() {
        return language;
    }

    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
