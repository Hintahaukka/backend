package hintahaukka.domain;

public class PriceTransferUnit {
    
    private String ean;
    private int cents;
    private String storeId;
    private String timestamp;

    public PriceTransferUnit(String ean, int cents, String storeId, String timestamp) {
        this.ean = ean;
        this.cents = cents;
        this.storeId = storeId;
        this.timestamp = timestamp;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
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
