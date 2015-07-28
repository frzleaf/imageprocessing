#include <jni.h>
#include "cropimage.h"
#include <android/log.h>
#include <android/bitmap.h>

#define TAG "Crop image"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, TAG , __VA_ARGS__)
// #define SWAP(A, B) ( A ^= B ^= A ^= B)

typedef struct {
	uint8_t red;
	uint8_t green;
	uint8_t blue;
	uint8_t alpha;
} rgba;

JNIEXPORT void JNICALL Java_com_leth_cropimage_CropLib_nativeCrop
(JNIEnv *env, jclass jclz, jobject origbm, jobject cutbm, jintArray xarr, jintArray yarr, jint len) {

	AndroidBitmapInfo originfo;
	AndroidBitmapInfo cutinfo;
	void *origpixels;
	void *cutpixels;

	// Pre-process bitmap
	AndroidBitmap_getInfo(env, origbm, &originfo);
	AndroidBitmap_getInfo(env, cutbm, &cutinfo);
	AndroidBitmap_lockPixels(env, origbm, &origpixels);
	AndroidBitmap_lockPixels(env, cutbm, &cutpixels);
	//

	LOGI("Before Crop");

	// Pre-process the boundary
	jint *xar = (*env)->GetIntArrayElements(env, xarr, 0);
	jint *yar = (*env)->GetIntArrayElements(env, yarr, 0);

	int i = 0, j = 0, k = 0,// enum
	top = 0, bot = 0,
	lef = 0, rig = 0;

	// Find the around rectangle
	for ( top = 0, i = 1; i < len; ++i) {
		if ( yar[i] < yar[top] )
		top = i;
		if ( yar[i] > yar[bot] )
		bot = i;
		if ( xar[i] < xar[lef])
		lef = i;
		if ( xar[i] > xar[rig])
		rig = i;
	}

	// Set start point
	origpixels = (int8_t *)origpixels + originfo.stride*yar[top];
	cutpixels = (int8_t *)cutpixels + cutinfo.stride*yar[top];

	for ( j = yar[top] + 1; j <= yar[bot] - 1; ++j) {

		uint32_t * srcpix = (uint32_t *) origpixels;
		uint32_t * despix = (uint32_t *) cutpixels;

		for ( k = xar[lef]; k <= xar[rig]; ++k ) {

			int inside = 0;

			// Check (k,j) to (0,0) cut (xar[i], yar[i]) to (xar[i-1], yar[i-1])
			for ( i = 1; i < len; ++i ) {
				if ( k < xar[i] && k < xar[i-1] ) // left side -> out
				continue;

				int leap = yar[i] > yar[i-1]
				? - 1
				: 1;

				if ( yar[i] + leap <= j && j <= yar[i-1]
						|| yar[i-1] <= j && j <= yar[i] + leap )
				{
					float a,b,c;
					// Follow the equation: ax + by + c = 0
					a = yar[i] - yar[i-1];
					b = xar[i-1] - xar[i];
					c = (-1) * ( a * xar[i] + b * yar[i]);

					// Check (0,j) and (k,j) in both sides of above line
					if ( ( b*j + c) * (a*k + b*j + c) <= 0 )
					++inside;
				}
			}

			if ( inside & 1 ) // inside is odd number which mean this point is inside the crop area
				despix[k] = srcpix[k];
		}

		origpixels = (int8_t *)origpixels + originfo.stride;
		cutpixels = (int8_t *)cutpixels + cutinfo.stride;

	}

	cutpixels = ( char* )cutpixels + cutinfo.stride * yar[top];
	origpixels = ( char* )origpixels + originfo.stride * yar[top];

	LOGI("Finish crop");
	AndroidBitmap_unlockPixels(env, origbm);
	AndroidBitmap_unlockPixels(env, cutbm);
}

/*
 int rs = 0;
 int i = 0;
 for ( i = 1;  i < len ; ++i ){
 if ( x < xar[i] && x < xar[i-1] ) // out
 continue;
 else
 if ( yar[i] <= y  && y <= yar[i-1]
 || yar[i-1] <= y && y <= yar[i] )
 ++rs;
 }

 // Last segment line
 if ( x < xar[0] && x < xar[len-1] ) // out
 continue;
 else
 if ( yar[0] <= y  && y <= yar[len-1]
 || yar[len-1] <= y && y <= yar[0] )
 ++rs;
 */
