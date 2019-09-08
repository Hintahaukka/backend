package hintahaukka.domain;

public class Price {
    
    private int id;
    private int productId;
    private int storeId;
    private int cents;
    private String created;

    public Price(int id, int productId, int storeId, int cents, String created) {
        this.id = id;
        this.productId = productId;
        this.storeId = storeId;
        this.cents = cents;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getCents() {
        return cents;
    }

    public void setCents(int cents) {
        this.cents = cents;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
    
}
