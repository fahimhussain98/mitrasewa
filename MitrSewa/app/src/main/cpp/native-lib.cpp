#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_wts_com_mitrsewa_retrofit_RetrofitClient_getAPIHost(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "http://api.mitrdigiportal.in/Vr1.0/100021/gP5CZRhl7rosFyQ6j5jKITqkr/bx1B56EbQIWoCM0jZLfN056Z/api/";
    return env->NewStringUTF(apiKey.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_wts_com_mitrsewa_retrofit_RetrofitClient_getAPIHostPlans(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "http://api.mitrdigiportal.in/Vr1.0/100021/gP5CZRhl7rosFyQ6j5jKITqkr/bx1B56EbQIWoCM0jZLfN056Z/api/";
    return env->NewStringUTF(apiKey.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_wts_com_mitrsewa_retrofit_RetrofitClient_getAPI(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "http://api.mitrdigiportal.in/api/";
    return env->NewStringUTF(apiKey.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_wts_com_mitrsewa_retrofit_RetrofitClient_getAuthKey(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "Basic d2VidGVjaCMkJV5zb2x1dGlvbiQkJiZAQCZeJmp1bHkyazIxOmJhc2ljJSUjI0AmJmF1dGgmIyYjJiMmQEAjJnBhc1d0UzIwMjE=";
    return env->NewStringUTF(apiKey.c_str());
}
