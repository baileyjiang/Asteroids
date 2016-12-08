package cs4530.asteroids;

import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    List<Sprite> sprites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        surfaceView.setRenderer(this);
        setContentView(surfaceView);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float ratio = ((float)metrics.heightPixels / (float)metrics.widthPixels);


//        Sprite sprite = new Sprite();
//        sprite.setWidth(1.0f);
//        sprite.setHeight(2.0f);
////        sprite.setCenterX(0.4f);
////        sprite.setCenterY(0.0f);
//        sprite.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.texture));
//        sprites.add(sprite);


        Sprite rightArrow = new Sprite();
        rightArrow.setWidth(0.25f * ratio);
        rightArrow.setHeight(0.25f);
        rightArrow.setCenterX(-0.25f);
        rightArrow.setCenterY(-0.85f);
        rightArrow.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_arrow_right));
        sprites.add(rightArrow);

        Sprite leftArrow = new Sprite();
        leftArrow.setWidth(0.25f * ratio);
        leftArrow.setHeight(0.25f);
        leftArrow.setCenterX(-0.75f);
        leftArrow.setCenterY(-0.85f);
        leftArrow.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_arrow_left));
        sprites.add(leftArrow);

        Sprite upArrow = new Sprite();
        upArrow.setWidth(0.25f * ratio);
        upArrow.setHeight(0.25f);
        upArrow.setCenterX(0.25f);
        upArrow.setCenterY(-0.85f);
        upArrow.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_arrow_up));
        sprites.add(upArrow);

        Sprite shoot = new Sprite();
        shoot.setWidth(0.25f * ratio);
        shoot.setHeight(0.25f);
        shoot.setCenterX(0.75f);
        shoot.setCenterY(-0.85f);
        shoot.setTexture(BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
        sprites.add(shoot);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.9f, 0.2f, 0.2f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear frame first
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        Sprite s = sprites.get(0);
//        s.setCenterX(s.getCenterX() + 0.001f);

        for (Sprite sprite : sprites) {
            sprite.draw();
        }

    }
}
