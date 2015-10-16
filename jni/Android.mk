LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := Spout
LOCAL_SRC_FILES := Spout.cpp

include $(BUILD_SHARED_LIBRARY)
