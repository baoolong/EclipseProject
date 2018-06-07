#include "com_test_natives_FFmpegNative.h"
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

AVCodec *mAVCodec=NULL;
AVCodecContext *mAVCodecContext=NULL;
AVFrame *mAVFrame=NULL;
int count=0;
AVPacket pkt;

int mBufferSize;
uint8_t *mEncoderData;

AVFormatContext *mAVFormatContext;
AVStream        *mAVStream;
const char *outFile="/sdcard/test.aac";
AVOutputFormat* mAVOUtputFormat;

#ifdef ANDROID
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "(>_<)", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "(^_^)", format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  printf("(>_<) " format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  printf("(^_^) " format "\n", ##__VA_ARGS__)
#endif

#undef LOG_TAG
#define LOG_TAG "jni_FFAacEncoderJni"

#ifdef __cplusplus
extern "C" {
#endif


void short2float(short* in, void* out, int len){
		register int i;
	for(i = 0; i < len; i++)
		((float*)out)[i] = ((float)(in[i])) / 32767.0;
}


JNIEXPORT jint JNICALL Java_com_test_natives_FFmpegNative_aacInit(JNIEnv *env, jobject thiz){
	av_register_all();

	mAVCodec =avcodec_find_encoder_by_name("libfdk_aac");
	if(!mAVCodec){
		LOGE("encoder AV_CODEC_ID_AAC not found");
		return -1;
	}

	mAVCodecContext = avcodec_alloc_context3(mAVCodec);
	if(mAVCodecContext != NULL){
		mAVCodecContext->codec_id         = AV_CODEC_ID_AAC;
		mAVCodecContext->codec_type       = AVMEDIA_TYPE_AUDIO;
		mAVCodecContext->bit_rate         = 64000;
		mAVCodecContext->sample_fmt       = AV_SAMPLE_FMT_S16;
		mAVCodecContext->sample_rate      = 44100;
		mAVCodecContext->channel_layout   = AV_CH_LAYOUT_STEREO;
		mAVCodecContext->channels         = av_get_channel_layout_nb_channels(mAVCodecContext->channel_layout);
	}else {
		LOGE("avcodec_alloc_context3 fail");
		return -1;
	}

	LOGE("start channels %d",mAVCodecContext->channels);
	if(avcodec_open2(mAVCodecContext, mAVCodec, NULL) < 0){
		LOGE("aac avcodec open fail");
		av_free(mAVCodecContext);
		mAVCodecContext = NULL;
		return -1;
	}

	mAVFrame = av_frame_alloc();
	if(!mAVFrame) {
		avcodec_close(mAVCodecContext);
		av_free(mAVCodecContext);
		mAVCodecContext = NULL;
		return -1;
	}
	mAVFrame->nb_samples = mAVCodecContext->frame_size;
	mAVFrame->format = mAVCodecContext->sample_fmt;
	mAVFrame->channel_layout = mAVCodecContext->channel_layout;


	mAVFormatContext = avformat_alloc_context();
	mAVOUtputFormat = av_guess_format(NULL, outFile, NULL);
	mAVFormatContext->oformat = mAVOUtputFormat;

	mBufferSize = av_samples_get_buffer_size(NULL, mAVCodecContext->channels, mAVCodecContext->frame_size, mAVCodecContext->sample_fmt, 0);
	LOGE("44100Hz AAC's BufferSize = %d",mBufferSize);
	if(mBufferSize < 0){
		LOGE("av_samples_get_buffer_size fail");
		av_frame_free(&mAVFrame);
		mAVFrame = NULL;
		avcodec_close(mAVCodecContext);
		av_free(mAVCodecContext);
		mAVCodecContext = NULL;
		return -1;
	}

	mEncoderData = (uint8_t *)av_malloc(mBufferSize);
	if(!mEncoderData){
		LOGE("av_malloc fail");
		av_frame_free(&mAVFrame);
		mAVFrame = NULL;
		avcodec_close(mAVCodecContext);
		av_free(mAVCodecContext);
		mAVCodecContext = NULL;
		return -1;
	}

	avcodec_fill_audio_frame(mAVFrame, mAVCodecContext->channels, mAVCodecContext->sample_fmt, (const uint8_t*)mEncoderData, mBufferSize, 0);

	//Open output URL
	if (avio_open(&mAVFormatContext->pb, outFile, AVIO_FLAG_READ_WRITE) < 0){
		printf("Failed to open output file!\n");
		return -1;
	}
	mAVStream = avformat_new_stream(mAVFormatContext, 0);
	if (!mAVStream){
		return -1;
	}
	av_dump_format(mAVFormatContext, 0, outFile, 1);
	//Write Header
	avformat_write_header(mAVFormatContext, NULL);
	av_new_packet(&pkt, mBufferSize);
	return 1;
}

JNIEXPORT jint JNICALL Java_com_test_natives_FFmpegNative_EncodingAAC(JNIEnv *env, jobject thiz, jbyteArray pcm, jint frameSize){
	uint8_t *pIn =(uint8_t *) (*env)->GetByteArrayElements(env, pcm, 0);
	int encode_ret = -1;
	int got_packet_ptr = 0;
	av_init_packet(&pkt);

	if(mAVCodecContext && mAVFrame){
		mAVFrame->data[0] = pIn;

		encode_ret = avcodec_encode_audio2(mAVCodecContext, &pkt, mAVFrame, &got_packet_ptr);
		if(encode_ret < 0){
			LOGE("Failed to encode!\n");
			return encode_ret;
		}
		pkt.stream_index = mAVStream->index;//通过pkt→stream_index可以查到获取的媒体数据的类型，从而将数据送交相应的解码器进行后续处理
		LOGE("the aac streams's num=%d ,the aac streams's den=%d",mAVStream->time_base.num,mAVStream->time_base.den);
		if(got_packet_ptr){
			av_interleaved_write_frame(mAVFormatContext, &pkt);
		}
		av_packet_unref(&pkt);
		(*env)->ReleaseByteArrayElements(env, pcm, pIn, 0);
	}
	return encode_ret;
}

JNIEXPORT void JNICALL Java_com_test_natives_FFmpegNative_CloseAudio(JNIEnv *env, jobject thiz){
	//fclose(mADTSFile);

	if(mAVFormatContext){
		av_write_trailer(mAVFormatContext);
		avio_close(mAVFormatContext->pb);
		avformat_free_context(mAVFormatContext);
	}

	if(mAVFrame){
		av_frame_free(&mAVFrame);
		mAVFrame = NULL;
	}
	if(mAVCodecContext){
		avcodec_close(mAVCodecContext);
		av_free(mAVCodecContext);
		mAVCodecContext = NULL;
	}
}
#ifdef __cplusplus
}
#endif
