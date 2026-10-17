package org.example.tourismchengdu.dto.upload;

public class UploadImageVO {

    private String url;
    private String originalFilename;
    private long size;

    public UploadImageVO() {
    }

    public UploadImageVO(String url, String originalFilename, long size) {
        this.url = url;
        this.originalFilename = originalFilename;
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
