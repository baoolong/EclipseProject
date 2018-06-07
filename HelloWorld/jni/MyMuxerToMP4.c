/**
 * ��򵥵Ļ���FFmpeg������Ƶ������
 * Simplest FFmpeg Muxer
 *
 * ������ Lei Xiaohua
 * leixiaohua1020@126.com
 * �й���ý��ѧ/���ֵ��Ӽ���
 * Communication University of China / Digital TV Technology
 * http://blog.csdn.net/leixiaohua1020
 *
 * ��������Խ���Ƶ��������Ƶ���������һ�ַ�װ��ʽ�С�
 * �����н�AAC�������Ƶ������H.264�������Ƶ���������
 * MPEG2TS��װ��ʽ���ļ���
 * ��Ҫע����Ǳ����򲢲��ı�����Ƶ�ı����ʽ��
 *
 * This software mux a video bitstream and a audio bitstream 
 * together into a file.
 * In this example, it mux a H.264 bitstream (in MPEG2TS) and 
 * a AAC bitstream file together into MP4 format file.
 *
 */

#include <stdio.h>
#include "com_test_natives_FFmpegNative.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/mathematics.h>
#include <libswscale/swscale.h>


#define __STDC_CONSTANT_MACROS


#ifdef ANDROID
#include <jni.h>
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "(>_<)", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "(^_^)", format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  LOGE("(>_<) " format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  LOGE("(^_^) " format "\n", ##__VA_ARGS__)
#endif

/*
FIX: H.264 in some container format (FLV, MP4, MKV etc.) need 
"h264_mp4toannexb" bitstream filter (BSF)
  *Add SPS,PPS in front of IDR frame
  *Add start code ("0,0,0,1") in front of NALU
H.264 in some container (MPEG2TS) don't need this BSF.
*/
//'1': Use H.264 Bitstream Filter 
#define USE_H264BSF 0

/*
FIX:AAC in some container format (FLV, MP4, MKV etc.) need 
"aac_adtstoasc" bitstream filter (BSF)
*/
//'1': Use AAC Bitstream Filter 
#define USE_AACBSF 1


JNIEXPORT jint JNICALL Java_com_test_natives_FFmpegNative_Composite264Aac
(JNIEnv *env, jobject obj,jstring h264FileName,jstring aacFileName,jstring outFileName,jstring angles)
{
	const char *inputH264FileName= (*env)->GetStringUTFChars(env,h264FileName, NULL);
	const char *inputAacFileName= (*env)->GetStringUTFChars(env,aacFileName, NULL);
	const char *outMP4FileName= (*env)->GetStringUTFChars(env,outFileName, NULL);
	const char *angle= (*env)->GetStringUTFChars(env,angles, NULL);

	AVOutputFormat *ofmt = NULL;
	//Input AVFormatContext and Output AVFormatContext
	AVFormatContext *ifmt_ctx_v = NULL, *ifmt_ctx_a = NULL,*ofmt_ctx = NULL;
	AVPacket pkt;
	int ret, i;
	int videoindex_v=-1,videoindex_out=-1;
	int audioindex_a=-1,audioindex_out=-1;
	int frame_index=0;
	int64_t cur_pts_a=0;
	int64_t cur_pts_v=0;

	LOGE("==========in h264==filename:%s\n",inputH264FileName);
	LOGE("==========in aac ===filename:%s\n",inputAacFileName);

	const char *in_filename_v =inputH264FileName;
	const char *in_filename_a =inputAacFileName;
	const char *out_filename =outMP4FileName;//Output file URL

	av_register_all();
	//Input
	if ((ret = avformat_open_input(&ifmt_ctx_v, in_filename_v, 0, 0)) < 0) {
		LOGE( "Could not open input file.");
		goto end;
	}
	if ((ret = avformat_find_stream_info(ifmt_ctx_v, 0)) < 0) {
		LOGE( "Failed to retrieve input stream information ������������Ϣʧ��");
		goto end;
	}

	if ((ret = avformat_open_input(&ifmt_ctx_a, in_filename_a, 0, 0)) < 0) {
		LOGE( "Could not open input file.");
		goto end;
	}
	if ((ret = avformat_find_stream_info(ifmt_ctx_a, 0)) < 0) {
		LOGE( "Failed to retrieve input stream information ������������Ϣʧ��");
		goto end;
	}
	LOGE("===========Input Information==========\n");
	av_dump_format(ifmt_ctx_v, 0, in_filename_v, 0);
	av_dump_format(ifmt_ctx_a, 0, in_filename_a, 0);
	LOGE("===================%d===================\n",ifmt_ctx_a->bit_rate);
	//Output
	avformat_alloc_output_context2(&ofmt_ctx, NULL, NULL, out_filename);
	if (!ofmt_ctx) {
		LOGE( "Could not create output context\n");
		ret = AVERROR_UNKNOWN;
		goto end;
	}
	ofmt = ofmt_ctx->oformat;

	for (i = 0; i < ifmt_ctx_v->nb_streams; i++) {
		//Create output AVStream according to input AVStream
		if(ifmt_ctx_v->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO){
		AVStream *in_stream = ifmt_ctx_v->streams[i];
		AVStream *out_stream = avformat_new_stream(ofmt_ctx, in_stream->codec->codec);
		videoindex_v=i;
		if (!out_stream) {
			LOGE( "Failed allocating output stream\n");
			ret = AVERROR_UNKNOWN;
			goto end;
		}
		videoindex_out=out_stream->index;
		//Copy the settings of AVCodecContext
		if (avcodec_copy_context(out_stream->codec, in_stream->codec) < 0) {
			LOGE( "Failed to copy context from input to output stream codec context\n");
			goto end;
		}
		out_stream->codec->codec_tag = 0;
		if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
			out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;
		break;
		}
	}

	for (i = 0; i < ifmt_ctx_a->nb_streams; i++) {
		//Create output AVStream according to input AVStream
		if(ifmt_ctx_a->streams[i]->codec->codec_type==AVMEDIA_TYPE_AUDIO){
			AVStream *in_stream = ifmt_ctx_a->streams[i];
			AVStream *out_stream = avformat_new_stream(ofmt_ctx, in_stream->codec->codec);
			audioindex_a=i;
			if (!out_stream) {
				LOGE( "Failed allocating output stream\n");
				ret = AVERROR_UNKNOWN;
				goto end;
			}
			audioindex_out=out_stream->index;
			//Copy the settings of AVCodecContext
			if (avcodec_copy_context(out_stream->codec, in_stream->codec) < 0) {
				LOGE( "Failed to copy context from input to output stream codec context\n");
				goto end;
			}
			out_stream->codec->codec_tag = 0;
			if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
				out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;

			break;
		}
	}

	LOGE("==========Output Information==========\n");
	av_dump_format(ofmt_ctx, 0, out_filename, 1);
	LOGE("======================================\n");
	//Open output file
	if (!(ofmt->flags & AVFMT_NOFILE)) {
		if (avio_open(&ofmt_ctx->pb, out_filename, AVIO_FLAG_WRITE) < 0) {
			LOGE( "Could not open output file '%s'", out_filename);
			goto end;
		}
	}
	//Write file header
	if (avformat_write_header(ofmt_ctx, NULL) < 0) {
		LOGE( "Error occurred when opening output file\n");
		goto end;
	}


	//FIX
#if USE_H264BSF
	AVBitStreamFilterContext* h264bsfc =  av_bitstream_filter_init("h264_mp4toannexb"); 
#endif
#if USE_AACBSF
	AVBitStreamFilterContext* aacbsfc =  av_bitstream_filter_init("aac_adtstoasc"); 
#endif
	while (1) {
		AVFormatContext *ifmt_ctx;
		int stream_index=0;
		AVStream *in_stream, *out_stream;

		//TODO AAC�ļ���timeBase��numһֱ��1����denһֱ��28224000  Get an AVPacket
		//LOGE("the aac streams's num=%d ,the aac streams's den=%d",ifmt_ctx_a->streams[audioindex_a]->time_base.num,ifmt_ctx_a->streams[audioindex_a]->time_base.den);
		//LOGE("the H264 streams's num=%d ,the H264 streams's den=%d",ifmt_ctx_v->streams[videoindex_v]->time_base.num,ifmt_ctx_v->streams[videoindex_v]->time_base.den);
		//LOGE("the H264'cur_pts_v is %d ,the AAC's cur_pts_a is %d",cur_pts_v,cur_pts_a);
		if(av_compare_ts(cur_pts_v,ifmt_ctx_v->streams[videoindex_v]->time_base,cur_pts_a,ifmt_ctx_a->streams[audioindex_a]->time_base) <= 0){
			ifmt_ctx=ifmt_ctx_v;
			stream_index=videoindex_out;

			if(av_read_frame(ifmt_ctx, &pkt) >= 0){
				do{
					in_stream  = ifmt_ctx->streams[pkt.stream_index];
					out_stream = ofmt_ctx->streams[stream_index];
					LOGE("the pkt.stream_index is %d ,the Avideoindex_v is %d",pkt.stream_index,videoindex_v);
					if(pkt.stream_index==videoindex_v){
						//FIX��No PTS (Example: Raw H.264)
						//Simple Write PTS
						if(pkt.pts==AV_NOPTS_VALUE){
							//Write PTS
							AVRational time_base1=in_stream->time_base;
							//Duration between 2 frames (us)
							int64_t calc_duration=(double)AV_TIME_BASE/av_q2d(in_stream->r_frame_rate);
							//Parameters
							pkt.pts=(double)(frame_index*calc_duration)/(double)(av_q2d(time_base1)*AV_TIME_BASE);
							pkt.dts=pkt.pts;
							pkt.duration=(double)calc_duration/(double)(av_q2d(time_base1)*AV_TIME_BASE);

							
							frame_index++;
						}
						cur_pts_v=pkt.pts;
						break;
					}
				}while(av_read_frame(ifmt_ctx, &pkt) >= 0);
			}else{
				break;
			}
		}else{
			ifmt_ctx=ifmt_ctx_a;
			stream_index=audioindex_out;
			if(av_read_frame(ifmt_ctx, &pkt) >= 0){
				do{
					in_stream  = ifmt_ctx->streams[pkt.stream_index];
					out_stream = ofmt_ctx->streams[stream_index];
					LOGE("the AAC stream write to mp4");
					if(pkt.stream_index==audioindex_a){

						//FIX��No PTS
						//Simple Write PTS
						if(pkt.pts==AV_NOPTS_VALUE){
							//Write PTS
							AVRational time_base1=in_stream->time_base;
							//Duration between 2 frames (us)
							int64_t calc_duration=(double)AV_TIME_BASE/av_q2d(in_stream->r_frame_rate);
							//Parameters
							pkt.pts=(double)(frame_index*calc_duration)/(double)(av_q2d(time_base1)*AV_TIME_BASE);
							pkt.dts=pkt.pts;
							pkt.duration=(double)calc_duration/(double)(av_q2d(time_base1)*AV_TIME_BASE);
							frame_index++;
						}
						cur_pts_a=pkt.pts;

						break;
					}
				}while(av_read_frame(ifmt_ctx, &pkt) >= 0);
			}else{
				break;
			}

		}

		//FIX:Bitstream Filter
#if USE_H264BSF
		av_bitstream_filter_filter(h264bsfc, in_stream->codec, NULL, &pkt.data, &pkt.size, pkt.data, pkt.size, 0);
#endif
#if USE_AACBSF
		av_bitstream_filter_filter(aacbsfc, out_stream->codec, NULL, &pkt.data, &pkt.size, pkt.data, pkt.size, 0);
#endif

		//Convert PTS/DTS
		pkt.pts = av_rescale_q_rnd(pkt.pts, in_stream->time_base, out_stream->time_base, AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX);
		pkt.dts = av_rescale_q_rnd(pkt.dts, in_stream->time_base, out_stream->time_base, AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX);
		pkt.duration = av_rescale_q(pkt.duration, in_stream->time_base, out_stream->time_base);
		pkt.pos = -1;
		pkt.stream_index=stream_index;

		LOGE("Write 1 Packet. size:%5d\tpts:%lld\n",pkt.size,pkt.pts);
		//Write
		if (av_interleaved_write_frame(ofmt_ctx, &pkt) < 0) {
			LOGE( "Error muxing packet\n");
			break;
		}
		av_free_packet(&pkt);

	}
	//Write file trailer
	av_write_trailer(ofmt_ctx);

#if USE_H264BSF
	av_bitstream_filter_close(h264bsfc);
#endif
#if USE_AACBSF
	av_bitstream_filter_close(aacbsfc);
#endif

end:
	avformat_close_input(&ifmt_ctx_v);
	avformat_close_input(&ifmt_ctx_a);
	/* close output */
	if (ofmt_ctx && !(ofmt->flags & AVFMT_NOFILE))
		avio_close(ofmt_ctx->pb);
	avformat_free_context(ofmt_ctx);
	if (ret < 0 && ret != AVERROR_EOF) {
		LOGE( "Error occurred.\n");
		return -1;
	}

	getchar();
	return 0;
}

