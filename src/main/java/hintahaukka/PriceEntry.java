package hintahaukka;

public class PriceEntry {
    
    private int ean;
    private int cents;
    private int storeId;
    private long timestamp;

    public PriceEntry(int ean, int cents, int storeId, long timestamp) {
        this.ean = ean;
        this.cents = cents;
        this.storeId = storeId;
        this.timestamp = timestamp;
    }

}
