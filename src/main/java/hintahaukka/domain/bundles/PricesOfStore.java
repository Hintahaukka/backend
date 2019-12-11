package hintahaukka.domain.bundles;

import java.util.ArrayList;

public class PricesOfStore {
    
    private String googleStoreId;
    private int storeCentsTotal;
    private ArrayList<PriceInStore> pricesInStore;

    public PricesOfStore(String googleStoreId, int storeCentsTotal, ArrayList<PriceInStore> pricesInStore) {
        this.googleStoreId = googleStoreId;
        this.storeCentsTotal = storeCentsTotal;
        this.pricesInStore = pricesInStore;
    }

    public String getGoogleStoreId() {
        return googleStoreId;
    }

    public int getStoreCentsTotal() {
        return storeCentsTotal;
    }

    public ArrayList<PriceInStore> getPricesInStore() {
        return pricesInStore;
    }
    
}
