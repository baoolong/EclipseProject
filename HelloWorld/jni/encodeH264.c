#include <jni.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include "com_test_natives_FFmpegNative.h"
#include <string.h>
#include <stdio.h>
#include <android/log.h>
#include <stdlib.h>
#include "libavdevice/avdevice.h"
#include "libavutil/avutil.h"
#include "libavutil/opt.h"
#include "libavutil/imgutils.h"
#include "libavutil/log.h"

#define TEST_H264  1

#ifdef ANDROID
#include <jni.h>
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "(>_<)", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "(^_^)", format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  printf("(>_<) " format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  printf("(^_^) " format "\n", ##__VA_ARGS__)
#endif

AVCodec *pCodec;
AVCodecContext *pCodecCtx = NULL;
int i, ret,got_output;
FILE *fp_out;
AVFrame *pFrame;
AVPacket pkt;
int y_size;
int framecnt = 0;
///storage/emulated/0
char filename_out[] = "/storage/emulated/0/yourname.h264";
int in_w = 640, in_h = 480;
int count = 0;


#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     cn_dennishucd_FFmpegNative
 * Method:    avcodec_find_decoder
 * Signature: (I)I
 * 	1、I帧
	I帧又称帧内编码帧，是一种自带全部信息的独立帧，无需参考其他图像便可独立进行解码，可以简单理解为一张静态画面。视频序列中的第一个帧始终都是I帧，因为它是关键帧。
	2、P帧
 	 P帧又称帧间预测编码帧，需要参考前面的I帧才能进行编码。表示的是当前帧画面与前一帧（前一帧可能是I帧也可能是P帧）的差别。解码时需要用之前缓存的画面叠加上本帧定义的差别，生成最终画面。与I帧相比，P帧通常占用更少的数据位，但不足是，由于P帧对前面的P和I参考帧有着复杂的依耐性，因此对传输错误非常敏感。
	3、B帧
	B帧又称双向预测编码帧，也就是B帧记录的是本帧与前后帧的差别。也就是说要解码B帧，不仅要取得之前的缓存画面，还要解码之后的画面，通过前后画面的与本帧数据的叠加取得最终的画面。B帧压缩率高，但是对解码性能要求较高。
 */
JNIEXPORT jint JNICALL Java_com_test_natives_FFmpegNative_avcodec_1find_1decoder
  (JNIEnv *env, jobject obj, jint codecID)
{
	AVCodec *codec = NULL;

	/* register all formats and codecs */
	av_register_all();
	codec = avcodec_find_decoder(codecID);
	if (!codec) {
		LOGE("hCodec not found,code=%d", codec);
	}
	if (codec != NULL){
		return 0;
	}else{
		return -1;
	}
}




JNIEXPORT jint JNICALL Java_com_test_natives_FFmpegNative_getVersion(JNIEnv *env,
        jclass jclass) {
    avcodec_register_all();

    //查找H264编码器
    pCodec = avcodec_find_encoder(AV_CODEC_ID_H264);
    if (!pCodec) {
    	LOGE("H264Codec not found,code=%d", pCodec);
        return -1;
    }
    //	初始化H264编码环境
    pCodecCtx = avcodec_alloc_context3(pCodec);
    if (!pCodecCtx) {
        LOGE("H264Could not allocate video codec context", "");
        return -1;
    }
    //H264编码参数设置
    pCodecCtx->bit_rate = 400000;
    pCodecCtx->thread_count = 4;
    pCodecCtx->width = in_w;
    pCodecCtx->height = in_h;
    pCodecCtx->time_base.num = 1;
    pCodecCtx->time_base.den = 15;
    //每50帧插入一个I帧，I帧越少，视频越小
    pCodecCtx->gop_size = 50;
    pCodecCtx->max_b_frames = 5;
    pCodecCtx->pix_fmt = AV_PIX_FMT_YUV420P;
    av_opt_set(pCodecCtx->priv_data, "preset", "superfast", 0);
    //av_opt_set(pCodecCtx->priv_data, "preset", "slow", 0);
    av_opt_set(pCodecCtx->priv_data, "tune", "zerolatency", 0);

	//运行H264编码器
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
        LOGE("H264Could not open codec", "");
        return -1;
    }

    //打开写入H264数据的文件
    if ((fp_out = fopen(filename_out, "wb")) == NULL) {
    	LOGE("OPEN_FILE", "Could not open file");
        return -1;
    }
    y_size = pCodecCtx->width * pCodecCtx->height;
    return 1;
}



JNIEXPORT jint JNICALL Java_com_test_natives_FFmpegNative_EncodingH264(JNIEnv *env,
        jclass jclass, jbyteArray yuvdata) {
    jbyte *yuv420sp = (jbyte*) (*env)->GetByteArrayElements(env, yuvdata, 0);

    pFrame = av_frame_alloc();
    if (!pFrame) {
        LOGE("Could not allocate video frame");
        return -1;
    }
    pFrame->format = pCodecCtx->pix_fmt;
    pFrame->width = pCodecCtx->width;
    pFrame->height = pCodecCtx->height;
    ret = av_image_alloc(pFrame->data, pFrame->linesize, pCodecCtx->width,
            pCodecCtx->height, pCodecCtx->pix_fmt, 16);
    if (ret < 0) {
    	LOGE("Could not allocate raw picture buffer\n");
        return -1;
    }
    av_init_packet(&pkt);
    pkt.data = NULL; // packet data will be allocated by the encoder
    pkt.size = 0;
    //Read raw YUV data  这里出错了，是按YUV_SP处理的 应该是YUV_P
    pFrame->data[0] = yuv420sp; //PCM Data
    pFrame->data[2] = yuv420sp + y_size; // U
    pFrame->data[1] = yuv420sp + y_size * 5 / 4; // V
    pFrame->pts = count;
    count++;
    /* encode the image */
    ret = avcodec_encode_video2(pCodecCtx, &pkt, pFrame, &got_output);
    int sizee = pkt.size;
    if (ret < 0) {
    	LOGE("Error encoding frame\n");
        return -1;
    }
    if (got_output) {
    	LOGE("Succeed to encode frame: %5d\tsize:%5d\n", framecnt, pkt.size);
    	LOGE("H264 package's pts is %d",pkt.pts);
        framecnt++;
        fwrite(pkt.data, 1, pkt.size, fp_out);
        av_free_packet(&pkt);
        //av_freep(&pFrame->data[0]);
        av_frame_free(&pFrame);
    }
    (*env)->ReleaseByteArrayElements(env, yuvdata, yuv420sp, 0);
    return 1;
}




JNIEXPORT void  Java_com_test_natives_FFmpegNative_CloseVideo
	(JNIEnv *env, jclass jclass) {

    for (got_output = 1; got_output; i++) {
        ret = avcodec_encode_video2(pCodecCtx, &pkt, NULL, &got_output);
        if (ret < 0) {
        	LOGE("Error encoding frame\n");
        }
        if (got_output) {
        	LOGE("Flush Encoder: Succeed to encode 1 frame!\tsize:%5d\n",pkt.size);
            fwrite(pkt.data, 1, pkt.size, fp_out);
            av_free_packet(&pkt);
        }
    }

    fclose(fp_out);
    avcodec_close(pCodecCtx);
    av_free(pCodecCtx);
    //av_freep(&pFrame->data[0]);
    av_frame_free(&pFrame);
}




/**
 * com.leixiaohua1020.sffmpegandroidhelloworld.MainActivity.avcodecinfo()
 * AVCodec Support Information
 */
JNIEXPORT jstring Java_com_test_natives_FFmpegNative_avcodecinfo(JNIEnv *env, jobject obj)
{
    char info[40000] = { 0 };

    av_register_all();

    AVCodec *c_temp = av_codec_next(NULL);

    while(c_temp!=NULL){
        if (c_temp->decode!=NULL){
            sprintf(info, "%s[Dec]", info);
        }
        else{
            sprintf(info, "%s[Enc]", info);
        }
        switch (c_temp->type){
        case AVMEDIA_TYPE_VIDEO:
            sprintf(info, "%s[Video]", info);
            break;
        case AVMEDIA_TYPE_AUDIO:
            sprintf(info, "%s[Audio]", info);
            break;
        default:
            sprintf(info, "%s[Other]", info);
            break;
        }
        sprintf(info, "%s[%10s]\n", info, c_temp->name);


        c_temp=c_temp->next;
    }
    //LOGE("%s", info);

    return (*env)->NewStringUTF(env, info);
}

#ifdef __cplusplus
}
#endif
