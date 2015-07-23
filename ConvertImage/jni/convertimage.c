#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <math.h>
/*
 * Class:     com_leth_convertimage_MainActivity
 * Method:    convertImage
 * Signature: (Landroid/graphics/Bitmap;Landroid/graphics/Bitmap;)
 */

#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,"convert image c" , __VA_ARGS__)

typedef struct
{
	uint8_t red;
	uint8_t green;
	uint8_t blue;
	uint8_t alpha;
} rgba;

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
	    LOGI("infocolor :: width-%d height-%d format-%d stride-%ld"
	    				   ,infocolor.width
						   ,infocolor.height
						   ,infocolor.stride);
	    AndroidBitmap_getInfo(env, graybm, &infogray);
	    AndroidBitmap_lockPixels(env, origbm, &pixelscolor);
	    AndroidBitmap_lockPixels(env, graybm, &pixelsgray);

	    LOGI("Start convert");
	    uint8_t tmp;
	    for ( y=0; y<infocolor.height; y++) {
	    	rgba * line = (rgba *) pixelscolor;
	    	uint32_t * grayline = (uint32_t *) pixelsgray;
	    	for ( x=0; x < infocolor.width; x++){
	    		tmp =
	    				line[x].red * 0.33
	    				+ line[x].green  * 0.33
						+ line[x].blue * 0.33
						;
	    		grayline[x] =
	    					  (line[x].alpha & 0xff) << 24
							| (tmp & 0xff) << 16
							| (tmp & 0xff) << 8
							| (tmp & 0xff)
							//| line[x].red << 16
							//| line[x].green << 8
	    					//| line[x].blue
							;
	    	}
	    	pixelscolor = (int8_t *)pixelscolor + infocolor.stride;
	    	pixelsgray = (int8_t *) pixelsgray + infogray.stride;
	    }

	    LOGI("Finish convert");
	    AndroidBitmap_unlockPixels(env, origbm);
	    AndroidBitmap_unlockPixels(env, graybm);
}
