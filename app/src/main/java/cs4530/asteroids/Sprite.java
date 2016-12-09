package cs4530.asteroids;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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
    private float[] rotateMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] MVPMatrix = new float[16];
    private Bitmap texture = null;
    private int textureName = -1;

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

    public Bitmap getTexture() {
        return texture;
    }

    public void setTexture(Bitmap texture) {
        this.texture = texture;
    }

    public float getRotation() {
        return angle;
    }

    public void setRotation(float theta) {
        angle = theta;
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
                "   gl_Position = matrix * vec4(position.x * scale.x + translate.x, position.y * scale.y + translate.y, 0.0, 1.0);\n" +
//                "   gl_Position = matrix * vec4(position.x * scale.x, position.y * scale.y, 0.0, 1.0);\n" +
//                "   gl_Position = matrix * vec4(position.x, position.y, 0.0, 1.0);\n" +

//                "   gl_Position = matrix * vec4(position.x, position.y, 0.0, 1.0); \n" +

//                "   gl_Position = vec4(position.x, position.y, 0.0, 1.0) * matrix;\n" +


//                "   gl_Position = vec4(((position.x * scale.x + translate.x) * cos(angle)) - ((position.y * scale.y + translate.y) * sin(angle))," +
//                " ((position.x * scale.x + translate.x) * sin(angle)) + ((position.y * scale.y + translate.y) * cos(angle)), 0.0, 1.0);\n" +
//
//                "   gl_Position = vec4(((position.x * scale.x + translate.x) * cos(angle)) - ((position.y * scale.y + translate.y) * sin(angle))," +
//                " ((position.x * scale.x + translate.x) * sin(angle)) + ((position.y * scale.y + translate.y) * cos(angle)), 0.0, 1.0);\n" +

//
//
//                "   gl_Position = vec4(((position.x * cos(angle) * scale.x + translate.x)) - ((position.y * sin(angle) * scale.y + translate.y))," +
//                " ((position.x * sin(angle) * scale.x + translate.x)) + ((position.y * cos(angle) * scale.y + translate.y)), 0.0, 1.0);\n" +
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

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);


        Matrix.setRotateM(rotateMatrix, 0, angle, 0, 0, 1.0f);
//        Matrix.scaleM(rotateMatrix);
        float[] matrixToUse = new float[16];
        Matrix.multiplyMM(matrixToUse, 0, MVPMatrix, 0, rotateMatrix, 0);


        GLES20.glUniform2f(translateUniformLocation, translateX, translateY);
        GLES20.glUniform2f(scaleUniformLocation, scaleX, scaleY);
        GLES20.glUniform1i(textureUnitUniformLocation, 0);
        GLES20.glUniform1f(angleUniformLocation, angle);
        GLES20.glUniformMatrix4fv(rotateMatrixUniformLocation, 1, false, matrixToUse, 0);


        if (textureName <= 0) {
            int[] textureNames = new int[1];
            textureNames[0] = -1;
            GLES20.glGenTextures(0, textureNames, 0);
            textureName = textureNames[0];

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, quadGeometry.length / 2);
    }
}
