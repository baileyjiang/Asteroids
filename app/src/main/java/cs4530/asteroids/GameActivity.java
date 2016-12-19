package cs4530.asteroids;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameActivity extends AppCompatActivity implements GLSurfaceView.Renderer, View.OnTouchListener {

//    List<Sprite> sprites = new ArrayList<>();
    List<Sprite> lasers = new ArrayList<>();
    List<Sprite> asteroids = new ArrayList<>();
    DisplayMetrics metrics = null;
    float ratio = -1;
    GLSurfaceView surfaceView = null;
    Date gameLoopLastRun = null;


//    WorkerThread workerThread;
//
//    class HoldThread extends Thread {
//        private volatile boolean stopped = false;
//        private volatile int action;
//        private volatile Context context;
//
//        public void setAction(int action) {
//            this.action = action;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            while (!stopped) {
//                // Shoot
//                if (action == 1) {
//                    GameModel.getInstance().getShip().accelerate();
//                }
//            }
//        }
//    }


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
        List<Sprite> minerals = GameModel.getInstance(this).getMinerals();

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
        for (Sprite s : minerals) {
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

        if (ship.isDestroyed()) {
            ship.setRotation(ship.getRotation() - 20.0f);
            ship.setHeight(ship.getHeight() - 0.005f);
            ship.setWidth(ship.getWidth() - 0.005f);
            if (ship.getWidth() < 0.0f || ship.getHeight() < 0.0f) {
                ship.setDestroyed(false);
                g.setLives(g.getLives() - 1);
                ship.setHeight(0.25f);
                ship.setWidth(0.25f);
                ship.setCenterX(0.0f);
                ship.setCenterY(0.0f);
                ship.setRotation(0.0f);
                // Destroy surrounding asteroids

                List<Sprite> toRemove = new ArrayList<>();
                for (Sprite asteroid : g.getAsteroidsModelBig()) {
                    float vectorX = ship.getCenterX() - asteroid.getCenterX();
                    float vectorY = ship.getCenterY() - asteroid.getCenterY();
                    float vectorLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);
                    if (vectorLength < ship.getWidth() * 1.5f + asteroid.getWidth() * 1.5f) {
                        asteroid.setDestroyed(true);
                        asteroid.setSetupVelocity(false);
                        toRemove.add(asteroid);
                    }
                }
                g.getAsteroidsModelBig().removeAll(toRemove);
                toRemove.clear();
                for (Sprite asteroid : g.getAsteroidsModelSmall()) {
                    float vectorX = ship.getCenterX() - asteroid.getCenterX();
                    float vectorY = ship.getCenterY() - asteroid.getCenterY();
                    float vectorLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);
                    if (vectorLength < ship.getWidth() * 1.5f + asteroid.getWidth() * 1.5f) {
                        asteroid.setDestroyed(true);
                        asteroid.setSetupVelocity(false);
                        toRemove.add(asteroid);
                    }
                }
                g.getAsteroidsModelSmall().removeAll(toRemove);
                checkGameOver();
            }
        }

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
                if (!asteroids.isDestroyed()) {
                    float vectorX = laser.getCenterX() - asteroids.getCenterX();
                    float vectorY = laser.getCenterY() - asteroids.getCenterY();
                    float vectorLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);
                    if (vectorLength < laser.getWidth() * 0.5f + asteroids.getWidth() * 0.5f) {
                        laser.setCenterX(90.0f);
                        laser.setCenterY(90.0f);
                        asteroids.setDestroyed(true);
                        asteroids.setSetupVelocity(false);
                        g.updateModelSmall();
                        g.setScore(g.getScore() + 100);
                        // Spawn mineral
                        if (rand.nextInt(100) > 95) {
                            g.getMineralModel().add(g.getMinerals().get(g.getMineralModel().size()));
                            Sprite mineral = g.getMineralModel().get(g.getMineralModel().size() - 1);
                            mineral.setCenterX(asteroids.getCenterX());
                            mineral.setCenterY(asteroids.getCenterY());
                            mineral.setVelocityX(rand.nextFloat() / 2);
                            mineral.setVelocityY(rand.nextFloat() / 2);
                            mineral.setStaticRotation((rand.nextFloat() - 0.5f) * 30.0f);
                        }
                        // Set up small asteroids here.
                        for (Sprite asteroidsSmall : g.getAsteroidsModelSmall()) {
                            if (!asteroidsSmall.isSetupVelocity()) {
                                asteroidsSmall.setVelocityX(rand.nextFloat() / 2);
                                asteroidsSmall.setVelocityY(rand.nextFloat() / 2);
                                asteroidsSmall.setCenterX(asteroids.getCenterX());
                                asteroidsSmall.setCenterY(asteroids.getCenterY());
                                asteroidsSmall.setSetupVelocity(true);
                                asteroidsSmall.setStaticRotation((rand.nextFloat() - 0.5f) * 30.0f);
                            }
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
                    if (rand.nextInt(100) > 95) {
                        g.getMineralModel().add(g.getMinerals().get(g.getMineralModel().size()));
                        Sprite mineral = g.getMineralModel().get(g.getMineralModel().size() - 1);
                        mineral.setCenterX(asteroids.getCenterX());
                        mineral.setCenterY(asteroids.getCenterY());
                        mineral.setVelocityX(rand.nextFloat() / 2);
                        mineral.setVelocityY(rand.nextFloat() / 2);
                        mineral.setStaticRotation((rand.nextFloat() - 0.5f) * 30.0f);
                    }
                    toRemove.add(asteroids);
                    asteroids.setSetupVelocity(false);
                    g.setScore(g.getScore() + 50);
                }
            }
            g.getAsteroidsModelSmall().removeAll(toRemove);
        }

        List<Sprite> toRemove = new ArrayList<>();
        for (Sprite asteroidsBig : g.getAsteroidsModelBig()) {
            if (asteroidsBig.getStaticRotation() == 0.0f) {
                asteroidsBig.setStaticRotation((rand.nextFloat() - 0.5f) * 30.0f);
            }
            if (!asteroidsBig.isSetupVelocity()) {
                asteroidsBig.setVelocityX(rand.nextFloat()/2);
                asteroidsBig.setVelocityY(rand.nextFloat()/2);
                asteroidsBig.setSetupVelocity(true);
                asteroidsBig.setCenterX((rand.nextFloat() - 0.50f) * 100.0f);
                asteroidsBig.setCenterY(((rand.nextFloat() - 0.50f) * 2.0f));
            }
            if (asteroidsBig.isDestroyed()) {
                asteroidsBig.setRotation(asteroidsBig.getRotation() - 20.0f);
                asteroidsBig.setHeight(asteroidsBig.getHeight() - 0.005f);
                asteroidsBig.setWidth(asteroidsBig.getWidth() - 0.005f);
                if (asteroidsBig.getWidth() < 0.0f || asteroidsBig.getHeight() < 0.0f) {
                    asteroidsBig.setDestroyed(false);
                    asteroidsBig.setHeight(0.15f);
                    asteroidsBig.setWidth(0.15f);
                    toRemove.add(asteroidsBig);
                }
            }
            // Check if a ship collision has occurred
            float vectorX = ship.getCenterX() - asteroidsBig.getCenterX();
            float vectorY = ship.getCenterY() - asteroidsBig.getCenterY();
            float vectorLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);
            if (vectorLength < ship.getWidth() * 0.5f + asteroidsBig.getWidth() * 0.5f) {
                if (g.isShipHasMineral()) {
                    ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship));
                    g.setShipHasMineral(false);
                    asteroidsBig.setDestroyed(true);
                    asteroidsBig.setSetupVelocity(false);
                    g.updateModelSmall();
                    g.setScore(g.getScore() + 100);
                    // Spawn mineral
                    if (rand.nextInt(100) > 95) {
                        g.getMineralModel().add(g.getMinerals().get(g.getMineralModel().size()));
                        Sprite mineral = g.getMineralModel().get(g.getMineralModel().size() - 1);
                        mineral.setCenterX(asteroidsBig.getCenterX());
                        mineral.setCenterY(asteroidsBig.getCenterY());
                        mineral.setVelocityX(rand.nextFloat() / 2);
                        mineral.setVelocityY(rand.nextFloat() / 2);
                        mineral.setStaticRotation((rand.nextFloat() - 0.5f) * 30.0f);
                    }
                    // Set up small asteroids here.
                    for (Sprite asteroidsSmall : g.getAsteroidsModelSmall()) {
                        if (!asteroidsSmall.isSetupVelocity()) {
                            asteroidsSmall.setVelocityX(rand.nextFloat() / 2);
                            asteroidsSmall.setVelocityY(rand.nextFloat() / 2);
                            asteroidsSmall.setCenterX(asteroidsBig.getCenterX());
                            asteroidsSmall.setCenterY(asteroidsBig.getCenterY());
                            asteroidsSmall.setSetupVelocity(true);
                            asteroidsSmall.setStaticRotation((rand.nextFloat() - 0.5f) * 30.0f);
                        }
                    }

                }
                else {
                    ship.setDestroyed(true);
                }
            }

            asteroidsBig.setRotation(asteroidsBig.getRotation() + asteroidsBig.getStaticRotation());
            asteroidsBig.setCenterX(asteroidsBig.getCenterX() + asteroidsBig.getVelocityX() * elapsedTime);
            asteroidsBig.setCenterY(asteroidsBig.getCenterY() + asteroidsBig.getVelocityY() * elapsedTime);
            asteroidsBig.draw();
        }
        g.getAsteroidsModelBig().removeAll(toRemove);

        toRemove.clear();
        for (Sprite asteroidsSmall : g.getAsteroidsModelSmall()) {
            // Check if a ship collision has occurred
            float vectorX = ship.getCenterX() - asteroidsSmall.getCenterX();
            float vectorY = ship.getCenterY() - asteroidsSmall.getCenterY();
            float vectorLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);
            if (vectorLength < ship.getWidth() * 0.5f + asteroidsSmall.getWidth() * 0.5f) {
                if (g.isShipHasMineral()) {
                    ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship));
                    g.setShipHasMineral(false);
                    if (rand.nextInt(100) > 95) {
                        g.getMineralModel().add(g.getMinerals().get(g.getMineralModel().size()));
                        Sprite mineral = g.getMineralModel().get(g.getMineralModel().size() - 1);
                        mineral.setCenterX(asteroidsSmall.getCenterX());
                        mineral.setCenterY(asteroidsSmall.getCenterY());
                        mineral.setVelocityX(rand.nextFloat() / 2);
                        mineral.setVelocityY(rand.nextFloat() / 2);
                        mineral.setStaticRotation((rand.nextFloat() - 0.5f) * 30.0f);
                    }
                    toRemove.add(asteroidsSmall);
                    asteroidsSmall.setSetupVelocity(false);
                    g.setScore(g.getScore() + 50);

                }
                else {
                    ship.setDestroyed(true);
                }
            }
            asteroidsSmall.setRotation(asteroidsSmall.getRotation() + asteroidsSmall.getStaticRotation());
            asteroidsSmall.setCenterX(asteroidsSmall.getCenterX() + asteroidsSmall.getVelocityX() * elapsedTime);
            asteroidsSmall.setCenterY(asteroidsSmall.getCenterY() + asteroidsSmall.getVelocityY() * elapsedTime);
            asteroidsSmall.draw();
        }
        g.getAsteroidsModelSmall().removeAll(toRemove);

        // Check if there are no asteroids left.
        if (g.getAsteroidsModelSmall().size() == 0 && g.getAsteroidsModelBig().size() == 0) {
            if (g.getLives() > 0) {
                g.setLevel(g.getLevel() + 1);
                g.updateModelBig();
            }
        }

        toRemove.clear();
        for (Sprite minerals : g.getMineralModel()) {
            minerals.setCenterX(minerals.getCenterX() + minerals.getVelocityX() * elapsedTime);
            minerals.setCenterY(minerals.getCenterY() + minerals.getVelocityY() * elapsedTime);
            minerals.setRotation(minerals.getRotation() + minerals.getStaticRotation());

            //Check if ship collides with mineral.
            float vectorX = ship.getCenterX() - minerals.getCenterX();
            float vectorY = ship.getCenterY() - minerals.getCenterY();
            float vectorLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);
            if (vectorLength < ship.getWidth() * 0.5f + minerals.getWidth() * 0.5f) {
                if (g.isShipHasMineral()) {
                    g.setScore(g.getScore() + 300);
                    toRemove.add(minerals);
                    minerals.setCenterX(90.0f);
                    minerals.setCenterY(90.0f);
                }
                else {
                    g.setShipHasMineral(true);
                    ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship_shield));
                    toRemove.add(minerals);
                    minerals.setCenterX(90.0f);
                    minerals.setCenterY(90.0f);
                }
            }
            else {
                minerals.draw();
            }
        }
        g.getMineralModel().removeAll(toRemove);
    }

    // Checks if lives are 0
    private void checkGameOver() {
        GameModel g = GameModel.getInstance(this);
        if(g.getLives() < 1) {
            //Remove all asteroids and ship
            g.getAsteroidsModelBig().clear();
            g.getAsteroidsModelSmall().clear();
            g.getGameOver().setCenterX(0.0f);
            g.getGameOver().setCenterY(0.0f);
            g.getShip().setCenterX(0.0f);
            g.getShip().setCenterY(0.0f);
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
        for (Sprite minerals : g.getMineralModel()) {
            float mineralX = ((minerals.getCenterX() * ratio + 1.0f) * 0.5f) * metrics.widthPixels;
            float mineralY = ((minerals.getCenterY() - 1.0f) * -0.5f) * metrics.heightPixels;

            if (mineralX < 0) {
                minerals.setCenterX(1.0f / ratio);
            }
            if (mineralX > metrics.widthPixels) {
                minerals.setCenterX(-1.0f / ratio);
            }
            if (mineralY < 0) {
                minerals.setCenterY(-1.0f);
            }
            if (mineralY > metrics.heightPixels) {
                minerals.setCenterY(1.0f);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final GameModel g = GameModel.getInstance(this);
        // Handle tap when game is over.
        if (g.getLives() < 1) {
            // Check for high score.
            if (g.getScore() > g.getScores().get(g.getScores().size() - 1).getScore() || g.getScores().size() < 10) {

                //Spawn input form
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater li = LayoutInflater.from(this);
                View joinGamePrompt = li.inflate(R.layout.highscore_prompt, null);
                alertDialogBuilder.setView(joinGamePrompt);
                final EditText name = (EditText) joinGamePrompt.findViewById(R.id.nameEditText);
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String playerNameString = name.getText().toString();
                                g.addScore(playerNameString, g.getScore());
                                Intent gameMenu = new Intent();
                                gameMenu.setClass(GameActivity.this, MainActivity.class);
                                startActivity(gameMenu);
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            } else {
                // Go back to menu.
                Intent gameMenu = new Intent();
                gameMenu.setClass(GameActivity.this, MainActivity.class);
                startActivity(gameMenu);
                finish();
                return true;
            }
        }


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
        float shootWidth = 256.0f;
        float shootHeight = 256.0f;


        boolean rightArrowPress = x >= rightArrowCenterX - rightArrowWidth/2 && x <= rightArrowCenterX + rightArrowWidth/2 && y >= rightArrowCenterY - rightArrowHeight/2 && y <= rightArrowCenterY + rightArrowHeight/2;
        boolean leftArrowPress = x >= leftArrowCenterX - leftArrowWidth/2 && x <= leftArrowCenterX + leftArrowWidth/2 && y >= leftArrowCenterY - leftArrowHeight/2 && y <= leftArrowCenterY + leftArrowHeight/2;
        boolean thrustPress = x >= thrustCenterX - thrustWidth/2 && x <= thrustCenterX + thrustWidth/2 && y >= thrustCenterY - thrustHeight/2 && y <= thrustCenterY + thrustHeight/2;
        boolean shootPress = x >= shootCenterX - shootWidth/2 && x <= shootCenterX + shootWidth/2 && y >= shootCenterY - shootHeight/2 && y <= shootCenterY + shootHeight/2;
        if (rightArrowPress) {
            ship.setRotation(ship.getRotation() - 10.0f);
        }
        if (leftArrowPress) {
            ship.setRotation(ship.getRotation() + 10.0f);
        }
        if (thrustPress) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ship.accelerate();

                if (GameModel.getInstance(this).isShipHasMineral()) {
                    ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship_shield_thrust));
                } else {
                    ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship_thrust));
                }
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                //TODO: delete this
                g.setLives(0);
                if (GameModel.getInstance(this).isShipHasMineral()) {
                    ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship_shield));
                } else {
                    ship.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship));
                }
            }

        }
        if (shootPress) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
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
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameModel.getInstance(this).saveScores();
        GameModel.getInstance(this).resetGameModel();
        Sprite.reset();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameModel.getInstance(this).saveAll();
    }
}
