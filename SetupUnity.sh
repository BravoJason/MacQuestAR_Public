#!/bin/bash

android_path="/Users/Zerone_Jia/MacQuestAR_NewServer/MacQuest/"
unity_path="/Users/Zerone_Jia/MacQuestAR_NewServer/MacQuest_AR_WithGroundPlane/Android_Export/MacQuest_AR/"

copyLib(){
cp -rf ${unity_path}libs ${android_path}app
rm ${android_path}app/libs/okhttp-3.8.0.jar
rm ${android_path}app/libs/okio-1.13.0.jar
}

copyAssets(){
cp -rf ${unity_path}src/main/assets/ ${android_path}app/src/main/assets
}

copyjniLibs(){
cp -rf ${unity_path}src/main/jniLibs/armeabi-v7a/ ${android_path}app/src/main/jniLibs/armeabi-v7a
cp -rf ${unity_path}src/main/jniLibs/x86/ ${android_path}app/src/main/jniLibs/x86
}

copyLib
copyAssets
copyjniLibs
