#include "graphics_shaders_NativeLib.h"
#define GL_GLEXT_PROTOTYPES
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>

GLuint query;

JNIEXPORT void JNICALL Java_graphics_shaders_NativeLib_startGPUTime(JNIEnv * env, jobject obj)
{
	PFNGLGENQUERIESEXTPROC glGenQueriesEXT;
	glGenQueriesEXT = (PFNGLGENQUERIESEXTPROC)eglGetProcAddress("glGenQueriesEXT");
	PFNGLBEGINQUERYEXTPROC glBeginQueryEXT;
	glBeginQueryEXT = (PFNGLBEGINQUERYEXTPROC)eglGetProcAddress("glBeginQueryEXT");
	glGenQueriesEXT(1, &query);
	glBeginQueryEXT (GL_TIME_ELAPSED_EXT, query);
}

JNIEXPORT void JNICALL Java_graphics_shaders_NativeLib_stopGPUTime(JNIEnv * env, jobject obj){
	PFNGLENDQUERYEXTPROC glEndQueryEXT;
	glEndQueryEXT = (PFNGLENDQUERYEXTPROC)eglGetProcAddress("glEndQueryEXT");
	glEndQueryEXT(GL_TIME_ELAPSED_EXT);
}

JNIEXPORT jint JNICALL Java_graphics_shaders_NativeLib_getTime(JNIEnv * env, jobject obj){
	PFNGLGETQUERYOBJECTIVEXTPROC glGetQueryObjectivEXT;
	glGetQueryObjectivEXT = (PFNGLGETQUERYOBJECTIVEXTPROC)eglGetProcAddress("glGetQueryObjectivEXT");
	int done;
	glGetQueryObjectivEXT(query, GL_QUERY_RESULT_AVAILABLE_EXT, &done);
	int time;
	glGetQueryObjectivEXT(query, GL_QUERY_RESULT_EXT, &time);
	return time;
}
