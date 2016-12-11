package cs4530.asteroids;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blyjng on 12/10/16.
 */

public class GameModel {
    private List<Sprite> baseSprites = new ArrayList<Sprite>();
    private List<Sprite> lasers = new ArrayList<Sprite>();
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


        Sprite ship = new Sprite();
        ship.setWidth(0.25f);
        ship.setHeight(0.25f);
//        ship.setRotation(0.2f);
        ship.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceship));
        baseSprites.add(ship);

//        Sprite z = new Sprite();
//        z.setWidth(0.25f);
//        z.setHeight(0.25f);
//        z.setCenterX(-0.25f / ratio);
//        z.setCenterY(-0.5f);
//        z.setTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet));
//        lasers.add(z);
//        baseSprites.add(z);

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
    }

    public List<Sprite> getBaseSprites() {
        return baseSprites;
    }

    public List<Sprite> getLaserSprites() {
        return lasers;
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

}
