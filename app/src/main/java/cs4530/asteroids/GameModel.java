package cs4530.asteroids;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by blyjng on 12/10/16.
 */

public class GameModel {
    private List<Sprite> baseSprites = new ArrayList<Sprite>();
    private List<Sprite> lasers = new ArrayList<Sprite>();
    private List<Sprite> asteroidsBig = new ArrayList<Sprite>();
    private List<Sprite> asteroidsSmall = new ArrayList<Sprite>();
    private List<Sprite> asteroidsModelBig = new ArrayList<Sprite>();
    private List<Sprite> asteroidsModelSmall = new ArrayList<Sprite>();
    private List<Sprite> minerals = new ArrayList<Sprite>();
    private List<Sprite> mineralModel = new ArrayList<Sprite>();
    private List<Score> scores = new ArrayList<Score>();
    private Sprite ship = null;
    private static GameModel instance = null;
    private static Context context = null;
    private float ratio = -1.0f;
    private int score = 0;
    private int lives = 3;
    private int level = 1;
    private Sprite levelTextValue;
    private Sprite scoreTextValue;
    private Sprite livesTextValue;
    private Sprite gameOver;
    private Random rand;
    private boolean shipHasMineral = false;

    public static GameModel getInstance(Context c) {
        if (instance == null) {
            context = c;
            instance = new GameModel();
        }
        return instance;
    }

    public void resetGameModel() {
        instance = null;
    }

    private GameModel() {
        ratio = ((float)context.getResources().getDisplayMetrics().heightPixels / (float)context.getResources().getDisplayMetrics().widthPixels);

        Sprite ship = new Sprite();
        ship.setWidth(0.25f);
        ship.setHeight(0.25f);
        ship.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceship));
        this.ship = ship;


        for (int i = 0; i < 10; i++) {
            Sprite l = new Sprite();
            l.setWidth(0.15f);
            l.setHeight(0.15f);
            l.setCenterX(90.0f);
            l.setCenterY(90.0f);
            l.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet));
//            baseSprites.add(l);
            lasers.add(l);
        }

        int asteroid1 = R.drawable.asteroid1;
        int asteroid2 = R.drawable.asteroid2;
        int asteroid3 = R.drawable.asteroid3;
        int asteroid4 = R.drawable.asteroid4;
        int asteroid5 = R.drawable.asteroid5;

        rand = new Random();
        for (int i = 0; i < 100; i++) {
            int asteroidNumber = rand.nextInt(5);
            asteroidNumber++;
            int asteroidTextureNumber = 0;
            if (asteroidNumber == 1) {
                asteroidTextureNumber = asteroid1;
            }
            if (asteroidNumber == 2) {
                asteroidTextureNumber = asteroid2;
            }
            if (asteroidNumber == 3) {
                asteroidTextureNumber = asteroid3;
            }
            if (asteroidNumber == 4) {
                asteroidTextureNumber = asteroid4;
            }
            if (asteroidNumber == 5) {
                asteroidTextureNumber = asteroid5;
            }
            Sprite a = new Sprite();
            a.setWidth(0.15f);
            a.setHeight(0.15f);
            a.setCenterX(90.0f);
            a.setCenterY(90.0f);
            a.setTexture(BitmapFactory.decodeResource(context.getResources(), asteroidTextureNumber));
            asteroidsBig.add(a);
        }
        for (int i = 0; i < 100; i++) {
            int asteroidNumber = rand.nextInt(5);
            asteroidNumber++;
            int asteroidTextureNumber = 0;
            if (asteroidNumber == 1) {
                asteroidTextureNumber = asteroid1;
            }
            if (asteroidNumber == 2) {
                asteroidTextureNumber = asteroid2;
            }
            if (asteroidNumber == 3) {
                asteroidTextureNumber = asteroid3;
            }
            if (asteroidNumber == 4) {
                asteroidTextureNumber = asteroid4;
            }
            if (asteroidNumber == 5) {
                asteroidTextureNumber = asteroid5;
            }
            Sprite a = new Sprite();
            a.setWidth(0.05f);
            a.setHeight(0.05f);
            a.setCenterX(90.0f);
            a.setCenterY(90.0f);
            a.setTexture(BitmapFactory.decodeResource(context.getResources(), asteroidTextureNumber));
            asteroidsSmall.add(a);
        }

        for (int i = 0; i < 10; i++) {
            Sprite mineral = new Sprite();
            mineral.setWidth(0.15f);
            mineral.setHeight(0.15f);
            mineral.setCenterX(90.0f);
            mineral.setCenterY(90.0f);
            mineral.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.mineral));
            minerals.add(mineral);
        }

        asteroidsModelBig = asteroidsBig.subList(0, level);

        //TODO delete later
        Score s = new Score();
        s.setName("Test");
        s.setScore(100);
        scores.add(s);

        s = new Score();
        s.setName("Test2");
        s.setScore(200);
        scores.add(s);

        s = new Score();
        s.setName("Test3");
        s.setScore(150);
        scores.add(s);


        Sprite rightArrow = new Sprite();
        rightArrow.setWidth(0.25f);
        rightArrow.setHeight(0.25f);
        rightArrow.setCenterX(-0.25f / ratio);
        rightArrow.setCenterY(-0.85f);
        rightArrow.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent_arrow_right));
        baseSprites.add(rightArrow);

        Sprite leftArrow = new Sprite();
        leftArrow.setWidth(0.25f);
        leftArrow.setHeight(0.25f);
        leftArrow.setCenterX(-0.75f / ratio);
        leftArrow.setCenterY(-0.85f);
        leftArrow.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent_arrow_left));
        baseSprites.add(leftArrow);

        Sprite upArrow = new Sprite();
        upArrow.setWidth(0.25f);
        upArrow.setHeight(0.25f);
        upArrow.setCenterX(0.25f / ratio);
        upArrow.setCenterY(-0.85f);
        upArrow.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent_arrow_up));
        baseSprites.add(upArrow);

        Sprite shoot = new Sprite();
        shoot.setWidth(0.25f);
        shoot.setHeight(0.25f);
        shoot.setCenterX(0.75f / ratio);
        shoot.setCenterY(-0.85f);
        shoot.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet));
        baseSprites.add(shoot);

        Sprite scoreText = new Sprite();
        scoreText.setWidth(0.5f);
        scoreText.setHeight(0.5f);
        scoreText.setCenterX(-0.65f / ratio);
        scoreText.setCenterY(0.9f);
        scoreText.setTextTexture("Score");
        baseSprites.add(scoreText);

        scoreTextValue = new Sprite();
        scoreTextValue.setWidth(0.5f);
        scoreTextValue.setHeight(0.5f);
        scoreTextValue.setCenterX(-0.65f / ratio);
        scoreTextValue.setCenterY(0.8f);
        scoreTextValue.setTextTexture("" + score);
        baseSprites.add(scoreTextValue);

        Sprite levelText = new Sprite();
        levelText.setWidth(0.5f);
        levelText.setHeight(0.5f);
        levelText.setCenterX(0.25f / ratio);
        levelText.setCenterY(0.9f);
        levelText.setTextTexture("Level");
        baseSprites.add(levelText);

        levelTextValue = new Sprite();
        levelTextValue.setWidth(0.5f);
        levelTextValue.setHeight(0.5f);
        levelTextValue.setCenterX(0.25f / ratio);
        levelTextValue.setCenterY(0.8f);
        levelTextValue.setTextTexture("" + level);
        baseSprites.add(levelTextValue);

        Sprite livesText = new Sprite();
        livesText.setWidth(0.5f);
        livesText.setHeight(0.5f);
        livesText.setCenterX(1.15f / ratio);
        livesText.setCenterY(0.9f);
        livesText.setTextTexture("Lives");
        baseSprites.add(livesText);

        livesTextValue = new Sprite();
        livesTextValue.setWidth(0.5f);
        livesTextValue.setHeight(0.5f);
        livesTextValue.setCenterX(1.15f / ratio);
        livesTextValue.setCenterY(0.8f);
        livesTextValue.setTextTexture("" + lives);
        baseSprites.add(livesTextValue);

        gameOver = new Sprite();
        gameOver.setWidth(1.0f);
        gameOver.setHeight(1.0f);
        gameOver.setCenterX(90.0f);
        gameOver.setCenterY(90.0f);
        gameOver.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_over));
        baseSprites.add(gameOver);


    }

    public List<Sprite> getBaseSprites() {
        return baseSprites;
    }

    public List<Sprite> getLaserSprites() {
        return lasers;
    }

    public List<Sprite> getAsteroidsBigSprites() {
        return asteroidsBig;
    }

    public List<Sprite> getAsteroidsSmallSprites() {
        return asteroidsSmall;
    }

    public void addLaser(Sprite s) {
        lasers.add(s);
    }

    public void addBaseSprite(Sprite s) {
        baseSprites.add(s);
    }

    public Sprite getOutOfPositionLaser() {
        for (Sprite laser : lasers) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            float laserY = (((laser.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels);
            float laserX = ((laser.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
            if (laserY > metrics.heightPixels || laserY < 0
                    || laserX > metrics.widthPixels || laserX < 0) {
                return laser;
            }
        }
        return null;
//        return lasers.get(0);
    }

    public Sprite getShip() {
        return ship;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        scoreTextValue.setTextTexture("" + this.score);
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
        livesTextValue.setTextTexture("" + this.lives);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        levelTextValue.setTextTexture("" + this.level);
    }

    public List<Sprite> getAsteroidsModelSmall() {
        return asteroidsModelSmall;
    }

    public List<Sprite> getAsteroidsModelBig() {
        return asteroidsModelBig;
    }

    public void updateModelSmall() {
        int randIndex = rand.nextInt(asteroidsSmall.size() - 4);
        asteroidsModelSmall.addAll(asteroidsSmall.subList(randIndex, randIndex + 4));
    }

    public void updateModelBig() {
        asteroidsModelBig = asteroidsBig.subList(0, level);
    }

    public List<Sprite> getMinerals() {
        return minerals;
    }

    public List<Sprite> getMineralModel() {
        return mineralModel;
    }

    public boolean isShipHasMineral() {
        return shipHasMineral;
    }

    public void setShipHasMineral(boolean shipHasMineral) {
        this.shipHasMineral = shipHasMineral;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void addScore(String name, int score) {
        Score s = new Score();
        s.setName(name);
        s.setScore(score);
        scores.add(s);

        Collections.sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score score, Score t1) {
                return t1.getScore() - score.getScore();
            }
        });
        if (scores.size() > 10) {
            scores = scores.subList(0, 9);
        }
    }

    public Sprite getGameOver() {
        return gameOver;
    }

    

    public void saveAll() {
        String FILENAME = "games";
        Gson gson = new Gson();
        String json = gson.toJson();
        try {
            FileOutputStream fos = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (Exception e) {
        }
    }
}
