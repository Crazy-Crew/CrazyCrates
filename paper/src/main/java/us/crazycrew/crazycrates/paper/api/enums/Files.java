package us.crazycrew.crazycrates.paper.api.enums;

public enum Files {

    locations("locations.yml"),
    users("users.yml");

    private final String fileName;

    Files(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}