package cs4530.asteroids;

import java.util.List;

/**
 * Created by blyjng on 12/18/16.
 */

public class GameState {

    private int score;
    private int lives;
    private int level;
    private List<Score> scores;
    private boolean valid;

    public GameState(int score, int lives, int level, List<Score> scores, boolean valid) {
        this.score = score;
        this.lives = lives;
        this.level = level;
        this.scores = scores;
        this.valid = valid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
