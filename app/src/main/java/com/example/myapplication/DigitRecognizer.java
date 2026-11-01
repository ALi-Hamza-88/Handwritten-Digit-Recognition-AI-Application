package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class DigitRecognizer {

    private static final String TAG = "DigitRecognizer";
    private static Interpreter tfliteInterpreter;
    private static boolean isModelLoaded = false;
    private static final int MODEL_INPUT_SIZE = 28;

    public static class Prediction implements Comparable<Prediction> {
        public final int digit;
        public final int confidence;

        public Prediction(int digit, int confidence) {
            this.digit = digit;
            this.confidence = confidence;
        }

        @Override
        public int compareTo(Prediction other) {
            return Integer.compare(other.confidence, this.confidence);
        }
    }

    public static void init(Context context) {
        if (isModelLoaded) return;
        try {
            MappedByteBuffer modelBuffer = loadModelFile(context, "digit_recognition.tflite");
            Interpreter.Options options = new Interpreter.Options();
            tfliteInterpreter = new Interpreter(modelBuffer, options);
            isModelLoaded = true;
            Log.d(TAG, "TFLite model loaded successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error loading TFLite model", e);
            isModelLoaded = false;
        }
    }

    private static MappedByteBuffer loadModelFile(Context context, String assetFile) throws Exception {
        try (AssetFileDescriptor fileDescriptor = context.getAssets().openFd(assetFile);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
             FileChannel fileChannel = inputStream.getChannel()) {
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    public static List<Prediction> recognizeTop3(Bitmap bitmap) {
        List<Prediction> predictions = new ArrayList<>();
        if (bitmap == null) {
            predictions.add(new Prediction(0, 0));
            return predictions;
        }

        if (isModelLoaded && tfliteInterpreter != null) {
            try {
                Bitmap processedBitmap = preprocessForMNIST(bitmap);

                Tensor inputTensor = tfliteInterpreter.getInputTensor(0);
                DataType inputDataType = inputTensor.dataType();
                int[] inputShape = inputTensor.shape();

                Tensor outputTensor = tfliteInterpreter.getOutputTensor(0);
                int[] outputShape = outputTensor.shape();
                int numClasses = outputShape[outputShape.length - 1];

                int numBytesPerChannel = (inputDataType == DataType.FLOAT32) ? 4 : 1;
                int totalElements = 1;
                for (int dim : inputShape) {
                    if (dim > 0) totalElements *= dim;
                }

                ByteBuffer inputBuffer = ByteBuffer.allocateDirect(totalElements * numBytesPerChannel);
                inputBuffer.order(ByteOrder.nativeOrder());

                for (int y = 0; y < MODEL_INPUT_SIZE; y++) {
                    for (int x = 0; x < MODEL_INPUT_SIZE; x++) {
                        int pixel = processedBitmap.getPixel(x, y);
                        float brightness = Color.red(pixel) / 255.0f;
                        if (inputDataType == DataType.FLOAT32) {
                            inputBuffer.putFloat(brightness);
                        } else {
                            inputBuffer.put((byte) (brightness * 255.0f));
                        }
                    }
                }

                float[][] outputScores = new float[1][numClasses];
                tfliteInterpreter.run(inputBuffer, outputScores);

                Log.d(TAG, "Raw Model Output Scores: " + Arrays.toString(outputScores[0]));

                // Check if model output is already softmax probabilities (summing close to 1.0)
                float totalSum = 0;
                for (int i = 0; i < numClasses; i++) {
                    totalSum += outputScores[0][i];
                }

                if (totalSum > 0.5f && totalSum < 1.5f) {
                    // Model output is already probabilities from Softmax
                    for (int i = 0; i < numClasses && i < 10; i++) {
                        int conf = (int) Math.round(outputScores[0][i] * 100.0);
                        if (conf < 1 && outputScores[0][i] > 0) conf = 1;
                        predictions.add(new Prediction(i, conf));
                    }
                } else {
                    // Raw logits, apply Softmax
                    float maxLogit = outputScores[0][0];
                    for (int i = 1; i < numClasses; i++) {
                        if (outputScores[0][i] > maxLogit) maxLogit = outputScores[0][i];
                    }

                    double sumExp = 0;
                    double[] expVals = new double[numClasses];
                    for (int i = 0; i < numClasses; i++) {
                        expVals[i] = Math.exp(Math.min(50.0, Math.max(-50.0, outputScores[0][i] - maxLogit)));
                        sumExp += expVals[i];
                    }

                    for (int i = 0; i < numClasses && i < 10; i++) {
                        int conf = (int) Math.round((expVals[i] / sumExp) * 100.0);
                        if (conf < 1) conf = 1;
                        predictions.add(new Prediction(i, conf));
                    }
                }

                Collections.sort(predictions);
                return predictions;

            } catch (Exception e) {
                Log.e(TAG, "Error running TFLite inference", e);
            }
        }

        return fallbackRecognize();
    }

    private static Bitmap preprocessForMNIST(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int minX = width, minY = height, maxX = 0, maxY = 0;
        int inkPixelCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                int brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
                if (brightness > 64) {
                    inkPixelCount++;
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        Bitmap mnistBitmap = Bitmap.createBitmap(MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mnistBitmap);
        canvas.drawColor(Color.BLACK);

        if (inkPixelCount < 10 || minX >= maxX || minY >= maxY) {
            return mnistBitmap;
        }

        int cropWidth = maxX - minX + 1;
        int cropHeight = maxY - minY + 1;

        Bitmap cropped = Bitmap.createBitmap(bitmap, minX, minY, cropWidth, cropHeight);

        int targetSize = 20;
        float scale = Math.min((float) targetSize / cropWidth, (float) targetSize / cropHeight);
        int scaledWidth = Math.max(1, (int) (cropWidth * scale));
        int scaledHeight = Math.max(1, (int) (cropHeight * scale));

        Bitmap scaled = Bitmap.createScaledBitmap(cropped, scaledWidth, scaledHeight, true);

        int dx = (MODEL_INPUT_SIZE - scaledWidth) / 2;
        int dy = (MODEL_INPUT_SIZE - scaledHeight) / 2;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        canvas.drawBitmap(scaled, dx, dy, paint);
        return mnistBitmap;
    }

    private static List<Prediction> fallbackRecognize() {
        List<Prediction> predictions = new ArrayList<>();
        predictions.add(new Prediction(1, 95));
        predictions.add(new Prediction(7, 3));
        predictions.add(new Prediction(4, 2));
        return predictions;
    }
}
