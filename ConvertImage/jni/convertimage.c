#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
/*
 * Class:     com_leth_convertimage_MainActivity
 * Method:    convertImage
 * Signature: (Landroid/graphics/Bitmap;Landroid/graphics/Bitmap;)
 */

#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,"convert image c" , __VA_ARGS__)

typedef struct
{
	uint8_t alpha;
	uint8_t red;
	uint8_t green;
	uint8_t blue;
} argb;

JNIEXPORT void JNICALL Java_com_leth_convertimage_MainActivity_convertImage
	(JNIEnv *env, jobject  obj, jobject origbm,jobject graybm)
{

		AndroidBitmapInfo  infocolor;
	    void				*pixelscolor;
	    AndroidBitmapInfo  infogray;
	    void 				*pixelsgray;
	    int 			    y;
	    int          	    x;

	    LOGI("Before convert");

	    AndroidBitmap_getInfo(env, origbm, &infocolor);
	    AndroidBitmap_getInfo(env, graybm, &infogray);
	    AndroidBitmap_lockPixels(env, origbm, &pixelscolor);
	    AndroidBitmap_lockPixels(env, graybm, &pixelsgray);

	    LOGI("Start convert");

	    for (y=0;y<infocolor.height;y++) {
	    	argb * line = (argb *) pixelscolor;
	    	uint8_t * grayline = (uint8_t *) pixelsgray;
	    	for (x=0;x<infocolor.width;x++) {
	    		grayline[x] = 0.3 * line[x].red + 0.59 * line[x].green + 0.11*line[x].blue;
	    	/*	LOGI("Pixels: %d", grayline[x]);*/
	    	}
	    	pixelscolor = (char *)pixelscolor + infocolor.stride;
	    	pixelsgray = (char *) pixelsgray + infogray.stride;
	    }

	    LOGI("Finish convert");
	    AndroidBitmap_unlockPixels(env, origbm);
	    AndroidBitmap_unlockPixels(env, graybm);
}
