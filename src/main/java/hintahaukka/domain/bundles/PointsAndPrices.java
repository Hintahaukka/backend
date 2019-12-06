package hintahaukka.domain.bundles;

import hintahaukka.domain.bundles.PriceTransferUnit;
import java.util.ArrayList;

public class PointsAndPrices {
    
    private int pointsTotal;
    private int pointsUnused;
    private ArrayList<PriceTransferUnit> prices;

    public PointsAndPrices(int pointsTotal, int pointsUnused, ArrayList<PriceTransferUnit> prices) {
        this.pointsTotal = pointsTotal;
        this.pointsUnused = pointsUnused;
        this.prices = prices;
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public int getPointsUnused() {
        return pointsUnused;
    }

    public ArrayList<PriceTransferUnit> getPrices() {
        return prices;
    }

}
