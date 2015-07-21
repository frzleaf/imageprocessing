LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := convertimage
LOCAL_SRC_FILES := convertimage.c
LOCAL_LDLIBS := -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)

