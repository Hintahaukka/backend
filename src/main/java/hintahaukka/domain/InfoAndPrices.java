package hintahaukka.domain;

import java.util.ArrayList;

public class InfoAndPrices {
    
    private String ean;
    private String name;
    private ArrayList<PriceTransferUnit> prices;

    public InfoAndPrices(String ean, String name, ArrayList<PriceTransferUnit> prices) {
        this.ean = ean;
        this.name = name;
        this.prices = prices;
    }
    
}