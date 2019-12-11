package hintahaukka.domain;

/**
 * Represents a user.
 */
public class User {
    
    private int id;
    private String token;
    private String nickname;
    private int pointsTotal;
    private int pointsUnused;

    public User(int id, String token, String nickname, int pointsTotal, int pointsUnused) {
        this.id = id;
        this.token = token;
        this.nickname = nickname;
        this.pointsTotal = pointsTotal;
        this.pointsUnused = pointsUnused;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public void setPointsTotal(int pointsTotal) {
        this.pointsTotal = pointsTotal;
    }

    public int getPointsUnused() {
        return pointsUnused;
    }

    public void setPointsUnused(int pointsUnused) {
        this.pointsUnused = pointsUnused;
    }
    
    

}
