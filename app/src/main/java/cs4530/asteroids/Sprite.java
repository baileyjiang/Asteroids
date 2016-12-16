package cs4530.asteroids;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;

/**
 * Created by blyjng on 12/3/16.
 */

public class Sprite {

    // Create quad
    private static final float[] quadGeometry = {
//            -0.5f, -0.5f,
//            0.5f, -0.5f,
//            -0.5f, 0.5f,
//            0.5f, 0.5f,

            -0.5f, -0.5f,
            0.5f, -0.5f,
            -0.5f, 0.5f,
            0.5f, 0.5f,


//            0.5f, 0.5f5f,

    };

    //interpolated color
    private static final float[] quadTextureCoordinates = {
            0.0f, 1.0f, //bottom left
            1.0f, 1.0f, //bottom right
            0.0f, 0.0f, //top left
            1.0f, 0.0f, //top right
    };

    private float translateX = 0.0f;
    private float translateY = 0.0f;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float angle = 0.0f;
    private float velocityX = 0.0f;
    private float velocityY = 0.0f;
    private float[] rotateMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] MVPMatrix = new float[16];
    private Bitmap texture = null;
    private int textureName = -1;
    private boolean setupVelocity = false;
//    private Context context;
//
//    //TODO: Dlete this
//    String tag;
//
//    public Sprite(Context context) {
//        this.context = context;
//    }

    private static int program = -1;

    private static int translateUniformLocation = -1;
    private static int scaleUniformLocation = -1;
    private static int textureUnitUniformLocation = -1;
    private static int angleUniformLocation = -1;
    private static int rotateMatrixUniformLocation = -1;

    private static final int POSITION_ARRAY = 0;
    private static final int TEXTURE_COORDINATE_ARRAY = 1;

    private static boolean setup = false;


    public float getCenterX() {
        return translateX;
    }

    public void setCenterX(float centerX) {
        translateX = centerX;
    }

    public float getCenterY() {
        return translateY;
    }

    public void setCenterY(float centerY) {
        translateY = centerY;
    }

    public float getWidth() {
        return scaleX;
    }

    public void setWidth(float width) {
        scaleX = width;
    }

    public float getHeight() {
        return scaleY;
    }

    public void setHeight(float height) {
        scaleY = height;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public Bitmap getTexture() {
        return texture;
    }

    public void setTexture(Bitmap texture) {
        this.texture = texture;
        if (textureName != -1) {
//            int[] doomedTextures = {textureName};
//            GLES20.glDeleteTextures(GLES20.GL_TEXTURE_2D, doomedTextures, 0);
            textureName = -1;
        }
    }

    // Text texture is captured through a Canvas object. The implementation is based off of JVitela's answer on StackOverflow:
    // http://stackoverflow.com/questions/1339136/draw-text-in-opengl-es
    public void setTextTexture(String text) {
        texture = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(texture);
        texture.eraseColor(0);

        Paint textPaint = new Paint();
        textPaint.setTextSize(32);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);

        canvas.drawText(text, 16,112, textPaint);
        if (textureName != -1) {
            textureName = -1;
        }
    }

    public float getRotation() {
        return angle;
    }

    public void setRotation(float theta) {
        angle = theta;
        if (angle >= 360.0f) {
            angle = angle - 360.0f;
        }
        if (angle < 0.0f) {
            angle = angle + 360.0f;
        }
    }

    public boolean isSetupVelocity() {
        return setupVelocity;
    }

    public void setSetupVelocity(boolean setupVelocity) {
        this.setupVelocity = setupVelocity;
    }

    public void accelerate() {
        float tempAngle = angle;
        tempAngle += 90.0f;
            if (tempAngle >= 360.0f) {
                tempAngle = tempAngle - 360.0f;
            }
        tempAngle = (float) Math.toRadians(tempAngle);

        // Round numbers
        double roundedCos = Math.round(Math.cos(tempAngle) * 100000);
        double roundedSin = Math.round(Math.sin(tempAngle) * 100000);
        double xResult = roundedCos/100000;
        double yResult = roundedSin/100000;

        float tempVelocityX = velocityX + (float) xResult * 0.1f;
        float tempVelocityY = velocityY + (float) yResult * 0.1f;
        if (Math.sqrt(tempVelocityX * tempVelocityX + tempVelocityY * tempVelocityY) < 0.5f)
        {
            velocityX = tempVelocityX;
            velocityY = tempVelocityY;
        }
    }

    public void decelerate() {
        if (velocityX != 0 || velocityY != 0) {
            float tempVelocityX = velocityX / 150;
            tempVelocityX = velocityX - tempVelocityX;
            float tempVelocityY = velocityY / 150;
            tempVelocityY = velocityY - tempVelocityY;

            velocityX = tempVelocityX;
            velocityY = tempVelocityY;
        }
    }

    private static void setup() {
        String vertexShaderSource = "" +
                "\n" +
                "attribute vec2 position;\n" +
                "attribute vec2 textureCoordinate;\n" +
                "uniform vec2 translate;\n" +
                "uniform vec2 scale;\n" +
                "uniform float angle;\n" +
                "uniform mat4 matrix;\n" +
                "varying vec2 textureCoordinateInterpolated; \n" +
                "\n" +
                "\n" +
                "void main() {\n" +
                "   gl_Position = matrix * vec4(position.x * scale.x, position.y * scale.y, 0.0, 1.0);\n" +
                "   textureCoordinateInterpolated = textureCoordinate; \n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n";

        String fragmentShaderSource = "" +
                "\n" +
                "varying highp vec2 textureCoordinateInterpolated; \n" +
                "uniform sampler2D textureUnit; \n" +
                "void main() { \n" +
                "    gl_FragColor = texture2D(textureUnit, textureCoordinateInterpolated); \n" +
                "} \n" +
                "\n" +
                "\n" +
                "\n";

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderSource);
        GLES20.glCompileShader(vertexShader);
        String vertexShaderLog = GLES20.glGetShaderInfoLog(vertexShader);
        Log.i("Vertex Shader", "Output: " + vertexShaderLog);

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);
        String fragmentShaderLog = GLES20.glGetShaderInfoLog(fragmentShader);
        Log.i("Fragment Shader", "Output: " + fragmentShaderLog);


        // Link program
        program = GLES20.glCreateProgram();

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glBindAttribLocation(program, POSITION_ARRAY, "position");
        GLES20.glBindAttribLocation(program, TEXTURE_COORDINATE_ARRAY, "textureCoordinate");

        GLES20.glLinkProgram(program);
        Log.i("Link Log", "Output: " + GLES20.glGetProgramInfoLog(program));

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(POSITION_ARRAY);
        GLES20.glEnableVertexAttribArray(TEXTURE_COORDINATE_ARRAY);

        ByteBuffer quadByteBuffer = ByteBuffer.allocateDirect(quadGeometry.length * 4);
        quadByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer quadBuffer = quadByteBuffer.asFloatBuffer();
        quadBuffer.put(quadGeometry);
        quadBuffer.rewind();

        ByteBuffer quadTextureCoordinateByteBuffer = ByteBuffer.allocateDirect(quadTextureCoordinates.length * 4);
        quadTextureCoordinateByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer quadTextureCoordinateBuffer = quadTextureCoordinateByteBuffer.asFloatBuffer();
        quadTextureCoordinateBuffer.put(quadTextureCoordinates);
        quadTextureCoordinateBuffer.rewind();

        GLES20.glVertexAttribPointer(POSITION_ARRAY, 2, GLES20.GL_FLOAT, false, 0, quadBuffer);
        GLES20.glVertexAttribPointer(TEXTURE_COORDINATE_ARRAY, 2, GLES20.GL_FLOAT, false, 0, quadTextureCoordinateBuffer);

        translateUniformLocation = GLES20.glGetUniformLocation(program, "translate");
        scaleUniformLocation = GLES20.glGetUniformLocation(program, "scale");
        textureUnitUniformLocation = GLES20.glGetUniformLocation(program, "textureUnit");
        angleUniformLocation = GLES20.glGetUniformLocation(program, "angle");
        rotateMatrixUniformLocation = GLES20.glGetUniformLocation(program, "matrix");

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        setup = true;

    }

    public void setupProjections(float ratio) {
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void draw() {

        if (!setup) {
            setup();
        }

        // The ordering and setup of the matrices references Google's OpenGL ES tutorial at https://developer.android.com/training/graphics/opengl/index.html
        // and the answer by escalator on Stackoverflow: http://stackoverflow.com/questions/13480043/opengl-es-android-matrix-transformations
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, translateX, translateY, 0);

        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.setRotateM(rotateMatrix, 0, angle, 0, 0, 1.0f);

        float[] matrixTemp;
        matrixTemp = modelMatrix.clone();
        Matrix.multiplyMM(modelMatrix, 0, matrixTemp, 0, rotateMatrix, 0);
        matrixTemp = MVPMatrix.clone();
        Matrix.multiplyMM(MVPMatrix, 0, matrixTemp, 0, modelMatrix, 0);

        GLES20.glUniform2f(translateUniformLocation, translateX, translateY);
        GLES20.glUniform2f(scaleUniformLocation, scaleX, scaleY);
        GLES20.glUniform1i(textureUnitUniformLocation, 0);
        GLES20.glUniform1f(angleUniformLocation, angle);
        GLES20.glUniformMatrix4fv(rotateMatrixUniformLocation, 1, false, MVPMatrix, 0);


        if (textureName <= 0) {
            int[] textureNames = new int[1];
            textureNames[0] = -1;
            GLES20.glGenTextures(1, textureNames, 0);
            textureName = textureNames[0];

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, quadGeometry.length / 2);
    }
}
