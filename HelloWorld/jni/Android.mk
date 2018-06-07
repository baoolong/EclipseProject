LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE :=avcodec-57-prebuilt
LOCAL_SRC_FILES :=prebuilt/libavcodec-57.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=avdevice-57-prebuilt
LOCAL_SRC_FILES :=prebuilt/libavdevice-57.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=avfilter-6-prebuilt
LOCAL_SRC_FILES :=prebuilt/libavfilter-6.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=avformat-57-prebuilt
LOCAL_SRC_FILES :=prebuilt/libavformat-57.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  avutil-55-prebuilt
LOCAL_SRC_FILES :=prebuilt/libavutil-55.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  postproc-54-prebuilt
LOCAL_SRC_FILES :=prebuilt/libpostproc-54.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  avswresample-2-prebuilt
LOCAL_SRC_FILES :=prebuilt/libswresample-2.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  swscale-4-prebuilt
LOCAL_SRC_FILES :=prebuilt/libswscale-4.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=encodeAac
LOCAL_SRC_FILES :=encodeAac.c
LOCAL_LDLIBS    +=  -lm -llog -landroid -lz -ljnigraphics
LOCAL_CFLAGS	+=  -D__STDC_CONSTANT_MACROS
LOCAL_C_INCLUDES	:= 	$(LOCAL_PATH)
#LOCAL_PROGUARED_ENABLDE := disabled					
LOCAL_SHARED_LIBRARIES	:=	avformat-57-prebuilt \
							avcodec-57-prebuilt \
							avswresample-2-prebuilt \
							avdevice-57-prebuilt \
							avfilter-6-prebuilt \
							avutil-55-prebuilt \
							swscale-4-prebuilt				
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=encodeH264
LOCAL_SRC_FILES :=encodeH264a.c
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid
LOCAL_SHARED_LIBRARIES:= avcodec-57-prebuilt \
						 avswresample-2-prebuilt \
						 avdevice-57-prebuilt \
						 avfilter-6-prebuilt \
						 avformat-57-prebuilt \
						 avutil-55-prebuilt \
						 swscale-4-prebuilt	
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE :=muxerToMP4
LOCAL_SRC_FILES :=MyMuxerToMP4.c
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid
LOCAL_SHARED_LIBRARIES:= avcodec-57-prebuilt \
						 avswresample-2-prebuilt \
						 avdevice-57-prebuilt \
						 avfilter-6-prebuilt \
						 avformat-57-prebuilt \
						 avutil-55-prebuilt \
						 swscale-4-prebuilt	\
						 postproc-54-prebuilt
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE :=jinLoadSo
LOCAL_C_INCLUDES += system/core/include/cutils
LOCAL_SRC_FILES :=autiDebug.c
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid
LOCAL_SHARED_LIBRARIES := -lpthread
include $(BUILD_SHARED_LIBRARY)

#include $(call all-makefiles-under,$(LOCAL_PATH))