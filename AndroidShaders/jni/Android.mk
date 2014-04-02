LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ndkOpenGLES
LOCAL_SRC_FILES := ndkOpenGLES.c
LOCAL_LDLIBS    := -llog -lGLESv2 -lEGL

include $(BUILD_SHARED_LIBRARY)