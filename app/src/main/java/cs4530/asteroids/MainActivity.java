package cs4530.asteroids;

import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer, View.OnTouchListener {

//    List<Sprite> sprites = new ArrayList<>();
    List<Sprite> lasers = new ArrayList<>();
    List<Sprite> asteroids = new ArrayList<>();
    DisplayMetrics metrics = null;
    float ratio = -1;
    GLSurfaceView surfaceView = null;
    Date gameLoopLastRun = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        surfaceView.setRenderer(this);
        surfaceView.setOnTouchListener(this);
        setContentView(surfaceView);

        metrics = getResources().getDisplayMetrics();
        ratio = ((float)metrics.heightPixels / (float)metrics.widthPixels);

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.9f, 0.2f, 0.2f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES20.glViewport(0, 0, i, i1);
        List<Sprite> sprites = GameModel.getInstance(this).getBaseSprites();
        List<Sprite> lasers = GameModel.getInstance(this).getLaserSprites();
        List<Sprite> asteroidsBig = GameModel.getInstance(this).getAsteroidsBigSprites();
        List<Sprite> asteroidsSmall = GameModel.getInstance(this).getAsteroidsSmallSprites();


        float ratio = (float) i / i1;
        GameModel.getInstance(this).getShip().setupProjections(ratio);
        for (Sprite s : sprites) {
            s.setupProjections(ratio);
        }
        for (Sprite s : lasers) {
            s.setupProjections(ratio);
        }
        for (Sprite s : asteroidsBig) {
            s.setupProjections(ratio);
        }
        for (Sprite s : asteroidsSmall) {
            s.setupProjections(ratio);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear frame first
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (gameLoopLastRun == null) {
            gameLoopLastRun = new Date();
        }
        Date now = new Date();
        float elapsedTime = (float) (now.getTime() - gameLoopLastRun.getTime()) / 1000.0f;
        gameLoopLastRun = now;
        GameModel g = GameModel.getInstance(this);
        Random rand = new Random();

        Sprite ship = g.getShip();
        ship.setCenterX(ship.getCenterX() + ship.getVelocityX() * elapsedTime);
        ship.setCenterY(ship.getCenterY() + ship.getVelocityY() * elapsedTime);

        ship.decelerate();
        checkEdges();
        ship.draw();

        for (Sprite s : g.getBaseSprites()) {
            s.draw();
        }

        for (Sprite laser : g.getLaserSprites()) {
            float tempAngle = laser.getRotation();
            tempAngle += 90;
            if (tempAngle >= 360.0f) {
                tempAngle = tempAngle - 360.0f;
            }
            tempAngle = (float) Math.toRadians(tempAngle);

            laser.setCenterX(laser.getCenterX() + ((float) Math.cos((double) tempAngle) * 0.1f));
            laser.setCenterY(laser.getCenterY() + ((float) Math.sin((double) tempAngle) * 0.1f));

            laser.draw();

            // check if it hits an asteroid
            for (Sprite asteroids : g.getAsteroidsModelBig()) {
                float vectorX = laser.getCenterX() - asteroids.getCenterX();
                float vectorY = laser.getCenterY() - asteroids.getCenterY();
                float vectorLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);
                if (vectorLength < laser.getWidth() * 0.5f + asteroids.getWidth() * 0.5f) {
                    laser.setCenterX(90.0f);
                    laser.setCenterY(90.0f);
                    g.getAsteroidsModelBig().remove(asteroids);
                    asteroids.setSetupVelocity(false);
                    g.updateModelSmall();
                    g.setScore(g.getScore() + 100);
                    // Set up small asteroids here.
                    for (Sprite asteroidsSmall : g.getAsteroidsModelSmall()) {
                        if (!asteroidsSmall.isSetupVelocity()) {
                            asteroidsSmall.setVelocityX(rand.nextFloat()/2);
                            asteroidsSmall.setVelocityY(rand.nextFloat()/2);
                            asteroidsSmall.setCenterX(asteroids.getCenterX());
                            asteroidsSmall.setCenterY(asteroids.getCenterY());
                            asteroidsSmall.setSetupVelocity(true);
                        }
                    }
                }
            }

            List<Sprite> toRemove = new ArrayList<>();
            for (Sprite asteroids : g.getAsteroidsModelSmall()) {
                float vectorX = laser.getCenterX() - asteroids.getCenterX();
                float vectorY = laser.getCenterY() - asteroids.getCenterY();
                float vectorLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);
                if (vectorLength < laser.getWidth() * 0.5f + asteroids.getWidth() * 0.5f) {
                    laser.setCenterX(90.0f);
                    laser.setCenterY(90.0f);
//                    g.getAsteroidsModelSmall().remove(asteroids);
                    toRemove.add(asteroids);
                    asteroids.setSetupVelocity(false);
                    g.setScore(g.getScore() + 50);
                }
            }
            g.getAsteroidsModelSmall().removeAll(toRemove);
        }

        for (Sprite asteroidsBig : g.getAsteroidsModelBig()) {
            // TODO: random rotations
//            int rotation = -1;
//            if (rand.nextBoolean()) {
//                rotation = 1;
//            }
            if (!asteroidsBig.isSetupVelocity()) {
                asteroidsBig.setVelocityX(rand.nextFloat()/2);
                asteroidsBig.setVelocityY(rand.nextFloat()/2);
                asteroidsBig.setSetupVelocity(true);
            }
//            asteroidsBig.setRotation(asteroidsBig.getRotation() + (rotation * 20f));
            asteroidsBig.setCenterX(asteroidsBig.getCenterX() + asteroidsBig.getVelocityX() * elapsedTime);
            asteroidsBig.setCenterY(asteroidsBig.getCenterY() + asteroidsBig.getVelocityY() * elapsedTime);
            asteroidsBig.draw();
        }

        for (Sprite asteroidsSmall : g.getAsteroidsModelSmall()) {
            // TODO: random rotations
//            int rotation = -1;
//            if (rand.nextBoolean()) {
//                rotation = 1;
//            }
//            asteroidsBig.setRotation(asteroidsBig.getRotation() + (rotation * 20f));
            asteroidsSmall.setCenterX(asteroidsSmall.getCenterX() + asteroidsSmall.getVelocityX() * elapsedTime);
            asteroidsSmall.setCenterY(asteroidsSmall.getCenterY() + asteroidsSmall.getVelocityY() * elapsedTime);
            asteroidsSmall.draw();
        }
    }

    // Wraps certain sprites around if they hit an edge.
    private void checkEdges() {
        GameModel g = GameModel.getInstance(this);
        Sprite ship = g.getShip();
        float shipX = ((ship.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
        float shipY = ((ship.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels;

        if (shipX < 0) {
            ship.setCenterX(1.0f / ratio);
        }
        if (shipX > metrics.widthPixels) {
            ship.setCenterX(-1.0f / ratio);
        }
        if (shipY < 0) {
            ship.setCenterY(-1.0f);
        }
        if (shipY > metrics.heightPixels) {
            ship.setCenterY(1.0f);
        }

        for (Sprite asteroidsBig : g.getAsteroidsModelBig()) {
            float asteroidX = ((asteroidsBig.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
            float asteroidY = ((asteroidsBig.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels;

            if (asteroidX < 0) {
                asteroidsBig.setCenterX(1.0f / ratio);
            }
            if (asteroidX > metrics.widthPixels) {
                asteroidsBig.setCenterX(-1.0f / ratio);
            }
            if (asteroidY < 0) {
                asteroidsBig.setCenterY(-1.0f);
            }
            if (asteroidY > metrics.heightPixels) {
                asteroidsBig.setCenterY(1.0f);
            }
        }

        for (Sprite asteroidsSmall : g.getAsteroidsModelSmall()) {
            float asteroidX = ((asteroidsSmall.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
            float asteroidY = ((asteroidsSmall.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels;

            if (asteroidX < 0) {
                asteroidsSmall.setCenterX(1.0f / ratio);
            }
            if (asteroidX > metrics.widthPixels) {
                asteroidsSmall.setCenterX(-1.0f / ratio);
            }
            if (asteroidY < 0) {
                asteroidsSmall.setCenterY(-1.0f);
            }
            if (asteroidY > metrics.heightPixels) {
                asteroidsSmall.setCenterY(1.0f);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        List<Sprite> sprites = GameModel.getInstance(this).getBaseSprites();


        Sprite ship = GameModel.getInstance(this).getShip();

        Sprite rightArrow = sprites.get(0);
        Sprite leftArrow = sprites.get(1);
        Sprite thrust = sprites.get(2);
        Sprite shoot = sprites.get(3);

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        float rightArrowCenterY = ((rightArrow.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels;
        float rightArrowCenterX = ((rightArrow.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
        float rightArrowWidth = rightArrow.getWidth() * metrics.widthPixels;
        float rightArrowHeight = rightArrow.getHeight() * (metrics.heightPixels);

        float leftArrowCenterY = ((leftArrow.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels;
        float leftArrowCenterX = ((leftArrow.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
        float leftArrowWidth = leftArrow.getWidth() * metrics.widthPixels;
        float leftArrowHeight = leftArrow.getHeight() * (metrics.heightPixels);

        float thrustCenterY = ((thrust.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels;
        float thrustCenterX = ((thrust.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
        float thrustWidth = thrust.getWidth() * metrics.widthPixels;
        float thrustHeight = thrust.getHeight() * (metrics.heightPixels);

        float shootCenterY = ((shoot.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels;
        float shootCenterX = ((shoot.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
        float shootWidth = shoot.getWidth() * metrics.widthPixels;
        float shootHeight = shoot.getHeight() * (metrics.heightPixels);

        boolean rightArrowPress = x >= rightArrowCenterX - rightArrowWidth/2 && x <= rightArrowCenterX + rightArrowWidth/2 && y >= rightArrowCenterY - rightArrowHeight/2 && y <= rightArrowCenterY + rightArrowHeight/2;
        boolean leftArrowPress = x >= leftArrowCenterX - leftArrowWidth/2 && x <= leftArrowCenterX + leftArrowWidth/2 && y >= leftArrowCenterY - leftArrowHeight/2 && y <= leftArrowCenterY + leftArrowHeight/2;
        boolean thrustPress = x >= thrustCenterX - thrustWidth/2 && x <= thrustCenterX + thrustWidth/2 && y >= thrustCenterY - thrustHeight/2 && y <= thrustCenterY + thrustHeight/2;
        boolean shootPress = x >= shootCenterX - shootWidth/2 && x <= shootCenterX + shootWidth/2 && y >= shootCenterY - shootHeight/2 && y <= shootCenterY + shootHeight/2;
        if (rightArrowPress) {
//            Log.i("EVENT", "Starting..");
//            Log.i("EVENT", "Angle: " + ship.getRotation());
//            Log.i("EVENT", "X value: " + ship.getCenterX());
//            Log.i("EVENT", "Y value: " + ship.getCenterY());
            ship.setRotation(ship.getRotation() - 5.0f);
        }
        if (leftArrowPress) {
//            Log.i("EVENT", "Starting..");
//            Log.i("EVENT", "Angle: " + ship.getRotation());
//            Log.i("EVENT", "X value: " + ship.getCenterX());
//            Log.i("EVENT", "Y value: " + ship.getCenterY());
            ship.setRotation(ship.getRotation() + 5.0f);
        }
        if (thrustPress) {
            ship.accelerate();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship_thrust));
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship));
            }
        }
        if (shootPress) {
//            Log.i("EVENT", "shoot");
//            Sprite bullet = new Sprite();
//            bullet.setHeight(0.25f);
//            bullet.setWidth(0.25f);
            Sprite bullet = GameModel.getInstance(this).getOutOfPositionLaser();
            if (bullet != null) {
                bullet.setCenterX(ship.getCenterX());
                bullet.setCenterY(ship.getCenterY());
                float tempAngle = ship.getRotation();
                bullet.setRotation(tempAngle);
                tempAngle += 90;
                if (tempAngle >= 360.0f) {
                    tempAngle = tempAngle - 360.0f;
                }
                tempAngle = (float) Math.toRadians(tempAngle);

                bullet.setCenterX(ship.getCenterX() + ((float) Math.cos((double) tempAngle) * 0.1f));
                bullet.setCenterY(ship.getCenterY() + ((float) Math.sin((double) tempAngle) * 0.1f));
            }
        }
        return true;
    }
}
