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

	int i = 0, j = 0, start = 0;

	// Rearrange the array by x
	for ( start = 0, i = 1; i < len; ++i) {
		if ( yar[i] < yar[start] )
		start = i;
	}

	i = start;
	int end = start != 0 ? (start - 1 ) % len : len - 1;
	int oy, y;
	int ox, x;
	int count = 0;
	int iNext = (start + 1) % len;


	cutpixels = ( char* )cutpixels + cutinfo.stride * yar[start];
	origpixels = ( char* )origpixels + originfo.stride * yar[start];

	LOGI("Start at = %d", start);

	// *NOTE: Crop by counter-clockwise direction would return unexpected results.
	for ( y = yar[i], x = xar[i],
			oy = y, ox = x
			; count <= len
			; y = yar[i] , x = xar[i],
			iNext = (i + 1) % len ) {

		if ( y - oy > 1 || oy - y > 1 ) {
			y = y > oy
				? oy + 1
				: oy - 1;
			if( yar[iNext] != yar[i] ) { 					// x = deltay * (dx / dy ) + x0
				x = 1.*(y-yar[i]) * (xar[iNext] - xar[i]) / (yar[iNext] - yar[i])
						+ 0.5 + xar[i]; 					//  using +0.5 instead of round function
				LOGI( "(x,y) = (%d,%d) ", x, y );
			}
		} else {
			i = iNext;
			++ count;
		}

		// Paint the inside
		uint32_t *pixels = (uint32_t *) cutpixels;
		uint32_t *opixels = (uint32_t *) origpixels;

		int isClear = 0;

		if ( pixels[0] != 0 && y != oy )
		isClear = 1;

		if ( isClear )
		// Clear pixel from x to 0 ~~~ set alpha = 0
		for ( j = x - 1; j >= 0; --j ) {
			pixels[j] = 0;
		}
		else
		// Copy original pixel to croping pixel
		for ( j = x; j >= 0; --j ) {
			pixels[j] = opixels[j];
		}

		if ( y > oy ) { // Go down
			cutpixels = ( char* )cutpixels + cutinfo.stride;
			origpixels = ( char* )origpixels + originfo.stride;
		}
		else if ( oy > y ) { // Painting go up
			cutpixels = ( char* )cutpixels - cutinfo.stride;
			origpixels = ( char* )origpixels - originfo.stride;
		}
		oy = y;
		ox = x;
	}

	LOGI("Finish crop");
	AndroidBitmap_unlockPixels(env, origbm);
	AndroidBitmap_unlockPixels(env, cutbm);
}

