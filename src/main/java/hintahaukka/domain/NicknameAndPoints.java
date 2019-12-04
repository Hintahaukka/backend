package hintahaukka.domain;

public class NicknameAndPoints {

    private String nickname;
    private int points;

    public NicknameAndPoints(String nickname, int points) {
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
