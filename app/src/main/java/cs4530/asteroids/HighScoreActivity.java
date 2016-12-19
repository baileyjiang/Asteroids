package cs4530.asteroids;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by blyjng on 12/18/16.
 */

public class HighScoreActivity extends AppCompatActivity implements ListAdapter {

    ListView scores;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore_view);
        GameModel.getInstance(this).loadScores();

        scores = (ListView) findViewById(R.id.list_view);
        scores.setAdapter(this);

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainMenu = new Intent();
                mainMenu.setClass(HighScoreActivity.this, MainActivity.class);
                startActivity(mainMenu);
            }
        });
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return GameModel.getInstance(this).getScores().size();
    }

    @Override
    public Object getItem(int i) {
        return GameModel.getInstance(this).getScores().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Score score = (Score) getItem(i);
        TextView scoreSummaryView = new TextView(this);
        scoreSummaryView.setTextColor(Color.WHITE);
        scoreSummaryView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scoreSummaryView.setGravity(Gravity.CENTER);
        scoreSummaryView.setTextSize(8.0f * getResources().getDisplayMetrics().density);
        scoreSummaryView.setText(score.getName() + "\n" + "Score: " + score.getScore());
        return scoreSummaryView;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
