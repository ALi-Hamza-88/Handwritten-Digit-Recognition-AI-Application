# Assignment No. 2: Practical Implementation Report
## AI-Based Mini Application Development: Integrating ML/DL Models into Android with TensorFlow Lite

**Project Title:** Handwritten Digit Recognition AI Application
**Provided Repository Reference:** [NumberGuessingAppTflite](https://github.com/Uzair390-Del/NumberGuessingAppTflite.git)
**Development Environment:** Android Studio, Java, TensorFlow Lite 2.17.0

---

## 1. Project Overview & Repository Reference
* **GitHub Repository Reference:** [https://github.com/Uzair390-Del/NumberGuessingAppTflite.git](https://github.com/Uzair390-Del/NumberGuessingAppTflite.git)
* **Application Architecture:** Built following robust Android MVC/Activity patterns with custom view rendering and local TFLite model execution.

---

## 2. Technical Implementation & Source Code Analysis

### A. Model Loading (`DigitRecognizer.java`)
The custom trained model (`digit_recognition.tflite`) is stored in the `app/src/main/assets/` directory and loaded into memory as a memory-mapped byte buffer (`MappedByteBuffer`) for efficient initialization of the TFLite `Interpreter`.

```java
public static void init(Context context) {
    if (isModelLoaded) return;
    try {
        MappedByteBuffer modelBuffer = loadModelFile(context, "digit_recognition.tflite");
        Interpreter.Options options = new Interpreter.Options();
        tfliteInterpreter = new Interpreter(modelBuffer, options);
        isModelLoaded = true;
    } catch (Exception e) {
        Log.e(TAG, "Error loading TFLite model", e);
        isModelLoaded = false;
    }
}
```

### B. Input Preprocessing & MNIST Formatting (`DigitRecognizer.java`)
User drawings are captured from the touch canvas, cropped to their bounding box, scaled to fit within a `20x20` pixel bounding box, centered in a `28x28` grid, and normalized into input tensor buffers.

```java
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

    Bitmap cropped = Bitmap.createBitmap(bitmap, minX, minY, maxX - minX + 1, maxY - minY + 1);
    Bitmap scaled = Bitmap.createScaledBitmap(cropped, 20, 20, true);
    canvas.drawBitmap(scaled, (28 - scaled.getWidth()) / 2f, (28 - scaled.getHeight()) / 2f, new Paint(Paint.ANTI_ALIAS_FLAG));
    return mnistBitmap;
}
```

### C. Running Inference & Interpreting Output (`DigitRecognizer.java`)
The preprocessed tensor buffer is passed to the TFLite interpreter, and output probability classes are sorted to identify the predicted digit and confidence accuracy.

```java
float[][] outputScores = new float[1][numClasses];
tfliteInterpreter.run(inputBuffer, outputScores);

for (int i = 0; i < numClasses && i < 10; i++) {
    int conf = (int) Math.round(outputScores[0][i] * 100.0);
    predictions.add(new Prediction(i, conf));
}
Collections.sort(predictions);
```

### D. Touch Drawing Canvas (`DrawingCanvasView.java`)
Provides a responsive touch drawing surface with a black background and white stroke rendering.

```java
public DrawingCanvasView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mPath = new Path();
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setColor(Color.WHITE);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeJoin(Paint.Join.ROUND);
    mPaint.setStrokeCap(Paint.Cap.ROUND);
    mPaint.setStrokeWidth(48f);
}
```

---

## 3. Project README Summary
* **Prerequisites:** Android Studio (Jellyfish / Koala or newer), Android SDK 24+, Gradle 8+.
* **Execution Instructions:**
  1. Clone or open project folder in Android Studio.
  2. Sync Gradle dependencies.
  3. Run the app on an Android Emulator or physical device.
  4. Draw a digit (`0`–`9`) and tap **Predict**.

---

## 4. Conclusion
The implementation successfully demonstrates on-device machine learning inference in Android using TensorFlow Lite, meeting all functional and documentation requirements for Assignment No. 2.
