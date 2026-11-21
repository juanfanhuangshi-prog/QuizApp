package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /** 1回のプレイで出題する問題数 */
    public static final int QUIZ_COUNT = 5;

    private TextView countLabel;
    private TextView questionLabel;
    private Button[] answerButtons;

    /** 今回出題する問題（QuizData.QUESTIONS の行） */
    private final List<String[]> quizList = new ArrayList<>();
    private int quizIndex = 0;
    private int correctCount = 0;
    private String rightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countLabel = findViewById(R.id.countLabel);
        questionLabel = findViewById(R.id.questionLabel);
        answerButtons = new Button[]{
                findViewById(R.id.answerBtn1),
                findViewById(R.id.answerBtn2),
                findViewById(R.id.answerBtn3),
                findViewById(R.id.answerBtn4),
        };
        for (Button button : answerButtons) {
            button.setOnClickListener(v -> checkAnswer(((Button) v).getText().toString()));
        }

        // 全問題をシャッフルして、先頭から QUIZ_COUNT 問を使う
        List<String[]> all = new ArrayList<>();
        Collections.addAll(all, QuizData.QUESTIONS);
        Collections.shuffle(all);
        quizList.addAll(all.subList(0, Math.min(QUIZ_COUNT, all.size())));

        showNextQuiz();
    }

    /** 問題と４つの選択肢を表示する */
    private void showNextQuiz() {
        String[] quiz = quizList.get(quizIndex);
        rightAnswer = quiz[1];

        countLabel.setText(getString(R.string.question_label, quizIndex + 1, quizList.size()));
        questionLabel.setText(getString(R.string.question_text, quiz[0]));

        // 選択肢 = 正解 + ほかの問題の答えからランダムに3つ
        List<String> others = new ArrayList<>();
        for (String[] q : QuizData.QUESTIONS) {
            if (!q[1].equals(rightAnswer)) {
                others.add(q[1]);
            }
        }
        Collections.shuffle(others);

        List<String> choices = new ArrayList<>();
        choices.add(rightAnswer);
        choices.addAll(others.subList(0, answerButtons.length - 1));
        Collections.shuffle(choices);

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(choices.get(i));
        }
    }

    /** 押されたボタンが正解かどうかを判定して、ダイアログで知らせる */
    private void checkAnswer(String selected) {
        boolean isCorrect = selected.equals(rightAnswer);
        if (isCorrect) {
            correctCount++;
        }

        boolean isLast = quizIndex == quizList.size() - 1;

        new AlertDialog.Builder(this)
                .setTitle(isCorrect ? R.string.correct_title : R.string.wrong_title)
                .setMessage(getString(R.string.answer_message, rightAnswer))
                .setPositiveButton(isLast ? R.string.see_result : R.string.next, (dialog, which) -> {
                    if (isLast) {
                        showResult();
                    } else {
                        quizIndex++;
                        showNextQuiz();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_CORRECT_COUNT, correctCount);
        intent.putExtra(ResultActivity.EXTRA_QUIZ_COUNT, quizList.size());
        startActivity(intent);
        finish();
    }
}
