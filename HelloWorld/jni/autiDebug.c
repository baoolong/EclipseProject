#include "com_lgyw_emergency_natives_AntiDebugNative.h"

#ifdef ANDROID
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "(>_<)", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "(^_^)", format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  printf("(>_<) " format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  printf("(^_^) " format "\n", ##__VA_ARGS__)
#endif


void anti_debug(){
	LOGE("anti_debug  Method is called..");
	ptrace(PTRACE_TRACEME,0,0,0);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm , void* reserved){
	LOGE("JNI_OnLoad Method is called");

	anti_debug();

	JNIEnv* env = NULL;

	if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6)  != JNI_OK) {
		LOGE("ERROR: GetEnv failed");
		return -1;
	}
	return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL Java_com_lgyw_emergency_natives_AntiDebugNative_checkProgramStatue
  (JNIEnv *env, jobject thiz){
	LOGE("checkProgramStatue Method is called");
	const int bufsize=1024;
	char filename[bufsize];
	char line[bufsize];
	int pid = 0;
	FILE* fd= NULL;
	pid = getpid();
	if(pid > 0){
		memset(filename,0,bufsize);
		sprintf(filename,"/proc/%d/status",pid);
		fd = fopen(filename,"r");
		if(fd!=NULL){
			while(fgets(line,bufsize,fd)){
				if(strncmp(line,"TracerPid",9)==0){
					int statue=atoi(&line[10]);
					if(statue!=0){
						int ret=kill(pid,SIGKILL);
					}
					break;
				}
			}
			fclose(fd);
		}
	}
}

JNIEXPORT void JNICALL Java_com_lgyw_emergency_natives_AntiDebugNative_uninstall
  (JNIEnv *env, jobject thiz,jstring path){
	LOGE("uninstall Method is called");
	const char* chars = (*env)->GetStringUTFChars(env, path,0);

	void* handle = dlopen(chars, RTLD_NOW);
	int count = 4;
	int i = 0;
	for (i = 0; i < count; i++){
		if (NULL != handle){
			dlclose(handle);
		}
	}
}
