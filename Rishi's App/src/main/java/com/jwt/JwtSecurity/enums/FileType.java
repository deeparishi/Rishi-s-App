package com.jwt.JwtSecurity.enums;

public enum FileType {

    PDF("PdfFilePathService"),
    EXCEL("ExcelFilePathService"),
    IMAGE("ImageFilePathService");

    public final String label;

    private FileType(String label) {
        this.label = label;
    }

}
