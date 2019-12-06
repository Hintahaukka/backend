package hintahaukka.domain.bundles;

import java.util.ArrayList;

public class PointsAndPricesOfStores {
    
    private int pointsTotal;
    private int pointsUnused;
    private ArrayList<PricesOfStore> pricesOfStores;

    public PointsAndPricesOfStores(int pointsTotal, int pointsUnused, ArrayList<PricesOfStore> pricesOfStores) {
        this.pointsTotal = pointsTotal;
        this.pointsUnused = pointsUnused;
        this.pricesOfStores = pricesOfStores;
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public int getPointsUnused() {
        return pointsUnused;
    }

    public ArrayList<PricesOfStore> getPricesOfStores() {
        return pricesOfStores;
    }

}
