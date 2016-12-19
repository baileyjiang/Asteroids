package cs4530.asteroids;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by blyjng on 12/17/16.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        Button startGame = (Button) findViewById(R.id.start_game);

        if (GameModel.getInstance(this).getGameState() != null) {
            startGame.setText("Resume");
        } else {
            startGame.setText("Start Game");
        }

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newGameIntent = new Intent();
                newGameIntent.setClass(MainActivity.this, GameActivity.class);
                startActivity(newGameIntent);
            }
        });

        Button highScore = (Button) findViewById(R.id.high_score);
        highScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newHighScoreIntent = new Intent();
                newHighScoreIntent.setClass(MainActivity.this, HighScoreActivity.class);
                startActivity(newHighScoreIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button startGame = (Button) findViewById(R.id.start_game);
        if (GameModel.getInstance(this).getGameState() != null) {
            startGame.setText("Resume");
        } else {
            startGame.setText("Start Game");
        }
    }
}
