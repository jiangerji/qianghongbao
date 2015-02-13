LOCAL_PATH := $(call my-dir)  
  
include $(CLEAR_VARS)  
   
LOCAL_MODULE := libhb
LOCAL_CFLAGS =  -DANDROID_NDK_BUILD -D__STDC_FORMAT_MACROS -D__STDC_INT64__

LOCAL_C_INCLUDES := $(LOCAL_PATH)

LOCAL_SRC_FILES :=  ./sendEvent.c

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)  