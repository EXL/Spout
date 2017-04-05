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

* Edit "project.properties" file and set the installed Android API SDK version here, for example:

```sh
# Project target.
target=android-23
```

* Build the APK-package into deploy directory;

```sh
cd ~/Deploy/SpoutAndroid/
/opt/android/android-sdk-linux/tools/android update project -n Spout -p .
/opt/android/android-ndk-r10d/ndk-build V=1
/opt/android/apache-ant-1.9.4/bin/ant debug
```

* Install Spout APK-package on your Android device via adb;

```sh
cd ~/Deploy/SpoutAndroid/
/opt/android/android-sdk-linux/platform-tools/adb install -r bin/Spout-debug.apk
```

* Run and enjoy!

You can also import this project in your favorite IDE: Eclipse or Android Studio and build the APK-package by using these programs.

## More information

Please read [Porting Guide (In Russian)](http://exlmoto.ru/spout-droid/) for more info about Spout.
