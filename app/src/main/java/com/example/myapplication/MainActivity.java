package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawingCanvasView drawingCanvasView;
    private TextView resultTextView;
    private TextView accuracyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize the custom trained TFLite model
        DigitRecognizer.init(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawingCanvasView = findViewById(R.id.drawingCanvasView);
        Button clearButton = findViewById(R.id.clearButton);
        Button predictButton = findViewById(R.id.predictButton);
        resultTextView = findViewById(R.id.resultTextView);
        accuracyTextView = findViewById(R.id.accuracyTextView);

        clearButton.setOnClickListener(v -> {
            drawingCanvasView.clearCanvas();
            resultTextView.setText("Prediction: ?");
            accuracyTextView.setText("Accuracy: —");
        });

        predictButton.setOnClickListener(v -> {
            Bitmap bitmap = drawingCanvasView.getBitmap();
            if (bitmap != null) {
                List<DigitRecognizer.Prediction> predictions = DigitRecognizer.recognizeTop3(bitmap);
                if (!predictions.isEmpty()) {
                    DigitRecognizer.Prediction top = predictions.get(0);
                    resultTextView.setText(String.format(Locale.getDefault(), "Prediction: %d", top.digit));
                    accuracyTextView.setText(String.format(Locale.getDefault(), "Accuracy: %d%%", top.confidence));
                } else {
                    resultTextView.setText("Could not recognize");
                    accuracyTextView.setText("Accuracy: 0%");
                }
            } else {
                resultTextView.setText("Please draw a digit first!");
                accuracyTextView.setText("");
            }
        });
    }
}
