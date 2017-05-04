LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

DEF := -DANDROID_NDK

LOCAL_CFLAGS +=  -O3 -ffast-math -fomit-frame-pointer $(DEF)
LOCAL_CPPFLAGS += -O3 -frtti -ffast-math -fomit-frame-pointer $(DEF)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/Spout
LOCAL_CPP_INCLUDES := $(LOCAL_C_INCLUDES)

LOCAL_MODULE    := Spout
LOCAL_SRC_FILES := SpoutNativeLibProxy.cpp Spout/spout.c Spout/piece.c

LOCAL_LDLIBS    += -llog -lGLESv1_CM

include $(BUILD_SHARED_LIBRARY)
