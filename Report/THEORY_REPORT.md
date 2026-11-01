::: {align="center"}
# INSTITUTE OF SPACE TECHNOLOGY

## KICSIT, Kahuta Campus

`<br>`{=html}

# ASSIGNMENT NO. 2

## AI-Based Mini Application Development

### Integrating ML/DL Models into Android with TensorFlow Lite

`<br>`{=html}

### THEORY REPORT

**BSCS-5B**
:::

------------------------------------------------------------------------

**NAME:** Ali Hamza Azam

**REGISTRATION NO.:** 242201088

**SERIAL NO.:** 38

**SUBJECT:** Artificial Intelligence

**PROJECT TITLE:** Handwritten Digit Recognition AI Application

**SUBMITTED TO:** \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

**SUBMISSION DATE:** 01/11/2026

------------------------------------------------------------------------

# 1. Introduction

Artificial Intelligence (AI) allows computer systems to perform tasks
that normally require human intelligence. Machine Learning (ML) is a
branch of AI that enables computers to learn patterns from data. Deep
Learning (DL) is a branch of machine learning that uses neural networks
to solve complex problems.

Mobile applications can use machine learning models to provide
intelligent features directly on smartphones. TensorFlow Lite enables
trained models to run on mobile devices without requiring continuous
internet access.

This assignment focuses on a Handwritten Digit Recognition application.
The user draws a digit from 0 to 9 on the mobile screen, and a trained
deep learning model processes the drawing and predicts the digit.

# 2. Embedding Machine Learning Models into Android Applications

Embedding an ML model into an Android application means integrating a
trained model so that the application can make predictions using user
input.

The general process includes:

1.  Collecting and preparing training data.
2.  Training a machine learning or deep learning model.
3.  Converting the trained model into a mobile-compatible format.
4.  Adding the model file to the Android project.
5.  Preparing user input according to the model requirements.
6.  Running inference using the model.
7.  Displaying the prediction in the application.

This approach can be used for image classification, object detection,
speech recognition, and handwriting recognition.

# 3. TensorFlow Lite

TensorFlow Lite is a framework designed to run trained machine learning
models on mobile devices and embedded systems. It allows developers to
integrate machine learning features into Android and iOS applications.

When a model runs locally on a device, the application can make
predictions without sending every input to a remote server.

## 3.1 Difference Between TensorFlow and TensorFlow Lite

  -----------------------------------------------------------------------
  TensorFlow                          TensorFlow Lite
  ----------------------------------- -----------------------------------
  Commonly used for model development Designed mainly for on-device
  and training.                       inference.

  Supports model training and         Provides tools to run converted
  evaluation workflows.               models on supported devices.

  Can be used in server and desktop   Optimized for mobile and edge
  environments.                       environments.

  Provides a broad machine learning   Provides a lightweight runtime and
  ecosystem.                          deployment tools.
  -----------------------------------------------------------------------

## 3.2 Why TensorFlow Lite Is Used

TensorFlow Lite is useful for Android applications because:

1.  It supports on-device model execution.
2.  It can allow predictions without an internet connection.
3.  It is designed for mobile and edge devices.
4.  It provides an Interpreter or supported runtime to execute models.
5.  It can reduce the need to send user input to a cloud service.

# 4. Model Conversion and Integration

A deep learning model can be trained using TensorFlow or Keras. After
training, it can be converted into TensorFlow Lite format for deployment
in a mobile application.

The converted model is saved with the `.tflite` extension.

The model file used in this project is:

`digit_recognition.tflite`

The file is placed in the Android application's assets directory so that
the application can load it.

## 4.1 TensorFlow Lite Dependency

A TensorFlow Lite dependency is added to the Android project through
Gradle. For example:

``` gradle
implementation 'org.tensorflow:tensorflow-lite:2.17.0'
```

The dependency version should match the version used by the actual
Android project.

## 4.2 TensorFlow Lite Interpreter

The Interpreter executes a TensorFlow Lite model. Its main
responsibilities are:

-   Loading the model.
-   Receiving prepared input data.
-   Running model inference.
-   Returning the model's output.

In this application, the Interpreter is used to classify the handwritten
digit.

# 5. Convolutional Neural Network (CNN)

A Convolutional Neural Network is a type of deep learning model commonly
used for image-related tasks.

CNNs learn visual patterns from images. Early layers can learn simple
patterns such as edges, while deeper layers can learn more complex
features.

For handwritten digit recognition, a CNN learns patterns that help
distinguish digits such as 0, 1, 2, and other digits.

# 6. MNIST Dataset

MNIST is a well-known dataset used for handwritten digit recognition. It
contains grayscale images of handwritten digits from 0 to 9.

The images are 28 × 28 pixels in size. The dataset is commonly used to
train and evaluate image classification models.

In this project, MNIST provides the handwritten digit examples used for
the digit recognition model.

# 7. Input Processing and Model Inference

The model expects an image in a specific input format. In this project,
the drawing is prepared as a 28 × 28 grayscale image.

The main processing steps are:

1.  The user draws a digit on the canvas.
2.  The drawing is captured as a bitmap.
3.  The bitmap is resized to 28 × 28 pixels.
4.  Pixel values are converted to grayscale.
5.  Pixel values are normalized according to the model's training
    format.
6.  The processed data is placed in the required input structure.
7.  The input is passed to the TensorFlow Lite Interpreter.

The model produces output scores for the ten possible digits (0--9). The
application selects the digit with the highest output score and displays
it as the prediction.

# 8. Analysis of the Existing GitHub Repository

The assignment involves studying an existing GitHub repository before
developing the mini application.

Repository analysis helps a developer understand how an Android project
is organized and how a machine learning model can be integrated into it.

The main areas to examine include:

-   Android project structure.
-   Application source files.
-   Model file location.
-   Model loading and execution.
-   User input processing.
-   Prediction result display.

The repository should be acknowledged in the final submission, and any
code reused or adapted should be clearly identified.

**Original repository reference:** Add the exact URL of the repository
provided in your assignment.

# 9. Proposed Application

## 9.1 Project Name

Handwritten Digit Recognition AI Application.

## 9.2 Objective

The objective of this application is to recognize handwritten digits
using a deep learning model integrated into an Android application.

The user can draw a single digit between 0 and 9. The application
processes the drawing and displays the predicted digit with a confidence
score.

## 9.3 Technologies Used

  Technology        Purpose
  ----------------- ----------------------------------
  Android Studio    Android application development
  Java              Application programming
  XML               User interface design
  TensorFlow Lite   Running the trained model
  CNN               Handwritten digit classification
  MNIST Dataset     Training and evaluation data
  Gradle            Dependency management

## 9.4 Main Features

1.  **Drawing Area:** Allows users to draw digits.
2.  **Clear Button:** Removes the existing drawing.
3.  **Predict Button:** Runs the model on the drawing.
4.  **Prediction Result:** Displays the recognized digit.
5.  **Confidence Score:** Displays the model's confidence in its
    prediction.
6.  **Offline Processing:** Allows prediction directly on the device
    when the model is available locally.

# 10. Application Workflow

The application follows this general workflow:

**User Draws Digit → Bitmap Creation → Image Resizing → Grayscale
Processing → Normalization → TensorFlow Lite Interpreter → Prediction →
Result Display**

This process allows the application to recognize handwritten digits
directly on the Android device.

# 11. Testing and Evaluation

The application was tested by drawing different handwritten digits on
the canvas.

  Test     Actual Digit   Predicted Digit   Displayed Confidence
  ------ -------------- ----------------- ----------------------
  1                   7                 7                   100%
  2                   3                 5                    35%

The first test correctly recognized digit 7. In the second test, the
model predicted 5 instead of 3.

These tests demonstrate the application's prediction functionality. They
do not represent the overall accuracy of the model. Overall accuracy
should be calculated by evaluating the model on a labelled test dataset.

# 12. Conclusion

This assignment demonstrates how machine learning concepts can be
applied in an Android application through a Handwritten Digit
Recognition project.

The project explains how a CNN model can be trained, converted into
TensorFlow Lite format, and integrated into an Android application. The
application accepts handwritten input, processes the image, and displays
a prediction with a confidence score.

The project provides practical understanding of deep learning, image
preprocessing, TensorFlow Lite, and Android application development.

# 13. References

1.  TensorFlow Lite Documentation -- https://www.tensorflow.org/lite
2.  TensorFlow Documentation -- https://www.tensorflow.org/
3.  Android Developers -- https://developer.android.com/
4.  MNIST Database -- https://yann.lecun.com/exdb/mnist/
5.  Keras Documentation -- https://keras.io/
6.  TensorFlow Lite Android Guide --
    https://www.tensorflow.org/lite/android
