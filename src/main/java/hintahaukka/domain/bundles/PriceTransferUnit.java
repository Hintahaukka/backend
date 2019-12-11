package hintahaukka.domain.bundles;

public class PriceTransferUnit {
    
    private int cents;
    private String storeId;
    private String timestamp;

    public PriceTransferUnit(int cents, String storeId, String timestamp) {
        this.cents = cents;
        this.storeId = storeId;
        this.timestamp = timestamp;
    }

    public int getCents() {
        return cents;
    }

    public void setCents(int cents) {
        this.cents = cents;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
