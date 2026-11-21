package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    public static final String EXTRA_CORRECT_COUNT = "correct_count";
    public static final String EXTRA_QUIZ_COUNT = "quiz_count";

    private static final String PREFS_NAME = "quiz_score";
    private static final String KEY_BEST_SCORE = "best_score";
    private static final String KEY_PLAY_COUNT = "play_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int correctCount = getIntent().getIntExtra(EXTRA_CORRECT_COUNT, 0);
        int quizCount = getIntent().getIntExtra(EXTRA_QUIZ_COUNT, MainActivity.QUIZ_COUNT);

        // スコアを端末に保存する（アプリを閉じても残る）
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int bestScore = prefs.getInt(KEY_BEST_SCORE, 0);
        int playCount = prefs.getInt(KEY_PLAY_COUNT, 0);

        // 画面回転などで作り直されたときに、二重に数えないようにする
        if (savedInstanceState == null) {
            playCount++;
            boolean isNewRecord = correctCount > bestScore;
            if (isNewRecord) {
                bestScore = correctCount;
            }
            prefs.edit()
                    .putInt(KEY_BEST_SCORE, bestScore)
                    .putInt(KEY_PLAY_COUNT, playCount)
                    .apply();
            findViewById(R.id.newRecordLabel)
                    .setVisibility(isNewRecord ? View.VISIBLE : View.GONE);
        }

        TextView scoreLabel = findViewById(R.id.scoreLabel);
        TextView bestScoreLabel = findViewById(R.id.bestScoreLabel);
        TextView playCountLabel = findViewById(R.id.playCountLabel);

        scoreLabel.setText(getString(R.string.result_score, quizCount, correctCount));
        bestScoreLabel.setText(getString(R.string.best_score, bestScore));
        playCountLabel.setText(getString(R.string.play_count, playCount));

        findViewById(R.id.retryBtn).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
