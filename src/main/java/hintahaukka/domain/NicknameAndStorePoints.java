package hintahaukka.domain;

public class NicknameAndStorePoints {
    
    private String nickname;
    private int points;

    public NicknameAndStorePoints(String nickname, int points) {
        this.nickname = nickname;
        this.points = points;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPoints() {
        return points;
    }

}
