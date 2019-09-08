package hintahaukka.domain;

public class Store {
    
    private int id;
    private String googleStoreId;
    private String name;

    public Store(int id, String googleStoreId, String name) {
        this.id = id;
        this.googleStoreId = googleStoreId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoogleStoreId() {
        return googleStoreId;
    }

    public void setGoogleStoreId(String googleStoreId) {
        this.googleStoreId = googleStoreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 
    
}
