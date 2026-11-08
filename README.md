# Driver Drowsiness Detection System — Java + OpenCV

🏆 **1st Place, SIBA Fest** — Sukkur IBA University tech fest.

A real-time Java desktop app that watches the driver through a webcam and reacts when their eyes stay closed: first it takes over driving in a simulator, then it stops the car and sounds an alarm.

## How it works

- **Detection:** OpenCV `VideoCapture(0)` + Haar cascades — `haarcascade_frontalface_default.xml` for the face, `haarcascade_eye.xml` for the eyes. Frames are converted to grayscale and histogram-equalised before detection.
- **Eyes closed > 3 seconds → Auto-Driving mode.** A dialog appears ("Eyes closed. Shifting to Auto-Driving mode.") and the in-app driving simulator (`GamePanel`) starts steering itself to avoid traffic.
- **Eyes closed > 10 seconds → Car stops + alarm.** Dialog ("Driver unresponsive. Car is stopping at roadside."), alarm sound plays, car is marked stopped.
- **Eyes open again → manual confirm.** A dialog asks "Eyes opened. Return to Manual Mode?" — the driver must confirm before manual control returns.
- **UI:** three panels — driving simulator (left), live camera feed with face box (green = eyes detected, red = no eyes), message history (right).

## Demo

> ⚠️ Demo video / GIF not yet added. A 10-second screen recording is the single most valuable addition to this repo — record the camera feed + the 3s auto-drive switch and add it here as `demo/demo.gif`.

## How to run

> ⚠️ **This repo does not run as-is right now — read this before cloning.** The code references a `resources/` folder that is **not committed**:
> - `resources/haarcascade_frontalface_default.xml`
> - `resources/haarcascade_eye.xml`
> - `resources/mixkit-emergency-alert-alarm-1007.wav`
>
> Add those three files to `resources/` first. There is also **no build file yet** (`pom.xml` / `build.gradle`) — compile manually until one is added:

```bash
# 1. Install OpenCV for Java and note the path to opencv-XXX.jar
# 2. Add the 3 files listed above to resources/
# 3. Compile and run (adjust the OpenCV jar path and native lib path):
javac -cp ".;<path-to-opencv-jar>" *.java
java -Djava.library.path="<path-to-opencv-native>" -cp ".;<path-to-opencv-jar>" DrowsinessApp
```

Requirements: Java 8+, a webcam, OpenCV 4.x Java bindings.

## Structure

```
.
├── DrowsinessApp.java   # entry point — builds the 3-panel Swing window (1000×600)
├── CameraPanel.java     # webcam capture, Haar face/eye detection, 3s / 10s timers, alarm
├── GamePanel.java       # driving simulator — keyboard driving, traffic, auto-drive mode
├── MessagePanel.java    # event / message history panel
├── README.md
└── resources/           # NOT COMMITTED YET — see "How to run" above
    ├── haarcascade_frontalface_default.xml
    ├── haarcascade_eye.xml
    └── mixkit-emergency-alert-alarm-1007.wav
```

## What was measured — and what wasn't

Honest status, because a recruiter will ask:

- ✅ Works live: face/eye detection, 3s and 10s thresholds, auto-drive switch, stop + alarm, return-to-manual confirm.
- ❌ **Not yet measured:** frames per second on a given machine, detection accuracy / false-alarm rate, performance with glasses, in low light, or at night. No benchmark table exists yet — do not quote one.
- ❌ No demo video, no screenshots, no build file, no tests.

Next steps, in order: (1) commit `resources/` + add a `pom.xml`, (2) record the demo GIF, (3) benchmark FPS + false alarms over a 5-minute drive, (4) low-power mobile optimisation + infrared for night driving.

## Limitations

- Haar cascades are fragile: glasses, low light, head turns, and occlusions cause missed eye detections, which this system reads as "eyes closed". That is a false-positive risk by design — the 3s / 10s timers exist to absorb brief misses, but they are not a fix.
- Eye-absence is a proxy for drowsiness, not a measurement of it — there is no EAR (Eye Aspect Ratio) threshold or blink-rate model here.
- The "car" is a simulator panel, not a real vehicle interface. This is a detection + alert demo, not an automotive safety system.

## Author

Bahadur Ali Meer Mohammad — BS Computer Science (AI), Sukkur IBA University.
Machine Learning Engineer / Agent Builder (LLM Engineer). LinkedIn: https://www.linkedin.com/in/bahadur-ali82/
