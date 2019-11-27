package hintahaukka.domain;

import java.util.ArrayList;

public class PricesOfStoresAndPoints {
    
    private int pointsTotal;
    private int pointsUnused;
    private ArrayList<PricesOfStore> pricesOfStores;

    public PricesOfStoresAndPoints(int pointsTotal, int pointsUnused, ArrayList<PricesOfStore> pricesOfStores) {
        this.pointsTotal = pointsTotal;
        this.pointsUnused = pointsUnused;
        this.pricesOfStores = pricesOfStores;
    }

}
