package hintahaukka.domain;

/**
 * Represents a product.
 */
public class Product {
    
    private int id;
    private String ean;
    private String name;

    public Product(int id, String ean, String name) {
        this.id = id;
        this.ean = ean;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
