package cs4530.asteroids;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.util.ArrayList;
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
    private Sprite ship = null;
    private static GameModel instance = null;
    private static Context context = null;
    private float ratio = -1.0f;

    public static GameModel getInstance(Context c) {
        if (instance == null) {
            context = c;
            instance = new GameModel();
        }
        return instance;
    }

    private GameModel() {
        ratio = ((float)context.getResources().getDisplayMetrics().heightPixels / (float)context.getResources().getDisplayMetrics().widthPixels);

//        Sprite sprite = new Sprite();
//        sprite.setWidth(1.0f);
//        sprite.setHeight(2.0f);
////        sprite.setCenterX(0.4f);
////        sprite.setCenterY(0.0f);
//        sprite.setTexture(BitmapFactory.decodeResource(contex.getResources(), R.drawable.texture));
//        baseSprites.add(sprite);


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

        Sprite score = new Sprite();
        score.setWidth(0.5f);
        score.setHeight(0.5f);
        score.setCenterX(-0.65f / ratio);
        score.setCenterY(0.9f);
        score.setTextTexture("Score");
        baseSprites.add(score);

        Sprite scoreValue = new Sprite();
        scoreValue.setWidth(0.5f);
        scoreValue.setHeight(0.5f);
        scoreValue.setCenterX(-0.65f / ratio);
        scoreValue.setCenterY(0.8f);
        scoreValue.setTextTexture("" + 123890);
        baseSprites.add(scoreValue);

        Sprite level = new Sprite();
        level.setWidth(0.5f);
        level.setHeight(0.5f);
        level.setCenterX(0.25f / ratio);
        level.setCenterY(0.9f);
        level.setTextTexture("Level");
        baseSprites.add(level);

        Sprite levelValue = new Sprite();
        levelValue.setWidth(0.5f);
        levelValue.setHeight(0.5f);
        levelValue.setCenterX(0.25f / ratio);
        levelValue.setCenterY(0.8f);
        levelValue.setTextTexture("" + 123890);
        baseSprites.add(levelValue);

        Sprite lives = new Sprite();
        lives.setWidth(0.5f);
        lives.setHeight(0.5f);
        lives.setCenterX(1.15f / ratio);
        lives.setCenterY(0.9f);
        lives.setTextTexture("Lives");
        baseSprites.add(lives);

        Sprite livesValue = new Sprite();
        livesValue.setWidth(0.5f);
        livesValue.setHeight(0.5f);
        livesValue.setCenterX(1.15f / ratio);
        livesValue.setCenterY(0.8f);
        livesValue.setTextTexture("" + 0);
        baseSprites.add(livesValue);




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

        Random rand = new Random();
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

}
