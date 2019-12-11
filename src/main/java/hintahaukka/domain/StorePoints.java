package hintahaukka.domain;

/**
 * Represents user's points earned from a specific store.
 * These points should be also added to the user's points tracking attributes.
 */
public class StorePoints {
    
    private int id;
    private int userId;
    private int storeId;
    private int points;

    public StorePoints(int id, int userId, int storeId, int points) {
        this.id = id;
        this.userId = userId;
        this.storeId = storeId;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
