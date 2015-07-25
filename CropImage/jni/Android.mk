LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := cropimage
LOCAL_SRC_FILES := cropimage.c
LOCAL_LDLIBS += -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)

