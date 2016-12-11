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
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer, View.OnTouchListener {

//    List<Sprite> sprites = new ArrayList<>();
    List<Sprite> lasers = new ArrayList<>();
    List<Sprite> asteroids = new ArrayList<>();
    DisplayMetrics metrics = null;
    float ratio = -1;
    GLSurfaceView surfaceView = null;

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


//        Sprite sprite = new Sprite();
//        sprite.setWidth(1.0f);
//        sprite.setHeight(2.0f);
////        sprite.setCenterX(0.4f);
////        sprite.setCenterY(0.0f);
//        sprite.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.texture));
//        sprites.add(sprite);


//        Sprite rightArrow = new Sprite();
//        rightArrow.setWidth(0.25f);
//        rightArrow.setHeight(0.25f);
//        rightArrow.setCenterX(-0.25f / ratio);
//        rightArrow.setCenterY(-0.85f);
//        rightArrow.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_arrow_right));
//        sprites.add(rightArrow);
//
//        Sprite leftArrow = new Sprite();
//        leftArrow.setWidth(0.25f);
//        leftArrow.setHeight(0.25f);
//        leftArrow.setCenterX(-0.75f / ratio);
//        leftArrow.setCenterY(-0.85f);
//        leftArrow.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_arrow_left));
//        sprites.add(leftArrow);
//
//        Sprite upArrow = new Sprite();
//        upArrow.setWidth(0.25f);
//        upArrow.setHeight(0.25f);
//        upArrow.setCenterX(0.25f / ratio);
//        upArrow.setCenterY(-0.85f);
//        upArrow.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_arrow_up));
//        sprites.add(upArrow);
//
//        Sprite shoot = new Sprite();
//        shoot.setWidth(0.25f);
//        shoot.setHeight(0.25f);
//        shoot.setCenterX(0.75f / ratio);
//        shoot.setCenterY(-0.85f);
//        shoot.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
//        sprites.add(shoot);
//
//
//        Sprite ship = new Sprite();
//        ship.setWidth(0.25f);
//        ship.setHeight(0.25f);
////        ship.setRotation(0.2f);
//        ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship));
//        sprites.add(ship);

//        Sprite z = new Sprite();
//        z.setWidth(0.25f);
//        z.setHeight(0.25f);
//        z.setCenterX(-0.25f / ratio);
//        z.setCenterY(-0.5f);
//        z.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
//        lasers.add(z);
//        sprites.add(z);
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


        float ratio = (float) i / i1;
        for (Sprite s : sprites) {
            s.setupProjections(ratio);
        }
        for (Sprite s : lasers) {
            s.setupProjections(ratio);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear frame first
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);


//        sprites.get(4).setCenterX(sprites.get(4).getCenterX() + 0.01f);

//        Sprite z = new Sprite();
//        z.setWidth(0.25f);
//        z.setHeight(0.25f);
//        z.setCenterX(-0.25f / ratio);
//        z.setCenterY(-0.5f);
//        z.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
//        z.tag = "123";
//        GameModel.getInstance(this).addBaseSprite(z);
//        List<Sprite> sprites = GameModel.getInstance(this).getBaseSprites();
//        if (sprites.size() > 8) {
//            Log.i("a", "size: " + sprites.size());
//        }
//        Iterator<Sprite> spriteIterator = sprites.iterator();
//        while (spriteIterator.hasNext()) {
//            spriteIterator.next().draw();
//        }
        GameModel g = GameModel.getInstance(this);
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
        }

//        for (int i = 0; i < lasers.size(); i++) {
//            Sprite laser = lasers.get(i);
//            float laserY = (((laser.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels);
//            float laserX = ((laser.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
////            if (laserY > metrics.heightPixels || laserY < 0
////                    || laserX > metrics.widthPixels || laserX < 0) {
////                lasers.remove(i);
////            } else {
//                laser.draw();
////            }
//        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        List<Sprite> sprites = GameModel.getInstance(this).getBaseSprites();


        Sprite ship = sprites.get(4);

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
            float tempAngle = ship.getRotation();
            tempAngle += 90;
            if (tempAngle >= 360.0f) {
                tempAngle = tempAngle - 360.0f;
            }
            tempAngle = (float) Math.toRadians(tempAngle);

//            Log.i("EVENT", "Starting..");
//            Log.i("EVENT", "Angle: " + ship.getRotation());
//            Log.i("EVENT", "X value: " + ship.getCenterX());
//            Log.i("EVENT", "Y value: " + ship.getCenterY());
//            Log.i("EVENT", "temp value: " + tempAngle);
//            Log.i("EVENT", "Adding to X: " + (float) Math.cos((double) tempAngle));
//            Log.i("EVENT", "Adding to Y: " + (float) Math.sin((double) tempAngle));
            ship.setCenterX(ship.getCenterX() + ((float) Math.cos((double) tempAngle) * 0.1f));
            ship.setCenterY(ship.getCenterY() + ((float) Math.sin((double) tempAngle) * 0.1f));
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
//
//            bullet.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
//            lasers.add(bullet);
//
//
//            Log.i("here1", "a: ");
//
//            Sprite z = new Sprite();
//            z.setWidth(0.25f);
//            z.setHeight(0.25f);
//            z.setCenterX(-0.25f / ratio);
//            z.setCenterY(-0.5f);
//            z.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
////            sprites.add(z);
//            GameModel.getInstance(this).addBaseSprite(z);

        }
//            boolean flag = false;
//            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                flag = true;
//            }
//            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                flag = false;
//            }
//            while (flag) {
//            }

        return true;
    }
}
