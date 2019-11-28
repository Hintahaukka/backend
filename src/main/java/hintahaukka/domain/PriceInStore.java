package hintahaukka.domain;

public class PriceInStore {
    
    private String ean;
    private int cents;
    private String timestamp;

    public PriceInStore(String ean, int cents, String timestamp) {
        this.ean = ean;
        this.cents = cents;
        this.timestamp = timestamp;
    }

    public String getEan() {
        return ean;
    }

    public int getCents() {
        return cents;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
