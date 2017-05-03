Spout
=============

![Spout port to Android OS, Motorola Photon Q](https://raw.github.com/EXL/Spout/master/images/photo_thumb.jpg)

Spout is a small, abstract shooting game from Japanese developer kuni. My port of Spout on Android OS uses rendering the videocontext of the game to texture, which is displayed with using OpenGL ES. Added touch control and some game engine improvements. Big Thanks to [SoD]Thor for implementing handy touch joystick.

![Spout animation](https://raw.github.com/EXL/Spout/master/images/animation_spout.gif)

Rules of the Spout game are fairly simple: you need to rise on the ship higher and higher, struggling with gravity and destroying obstacles by reactive jet.

![Spout Android Screen](https://raw.github.com/EXL/Spout/master/images/spout_android.png)

## Build instructions

For example, GNU/Linux:

* Install the latest [Android SDK](https://developer.android.com/sdk/) and [Android NDK](https://developer.android.com/ndk/);

* Clone repository into deploy directory;

```sh
cd ~/Deploy/
git clone https://github.com/EXL/Spout SpoutAndroid
```

* Build the APK-package into deploy directory with Gradle building script;

```sh
cd ~/Deploy/SpoutAndroid/
ANDROID_HOME="/opt/android-sdk/" ./gradlew assembleDebug
```

* Install Spout APK-package on your Android device via adb;

```sh
cd ~/Deploy/SpoutAndroid/
/opt/android-sdk/platform-tools/adb install -r spout/build/outputs/apk/spout-debug.apk
```

* Run and enjoy!

You can also open this project in Android Studio IDE and build the APK-package by using this program.

## More information

Please read [Porting Guide (In Russian)](http://exlmoto.ru/spout-droid/) for more info about Spout.
