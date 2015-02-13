#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <errno.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/input.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>

#define LOG_TAG "event"
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)

void simulate_touch(int fd, int x, int y) {
    struct timeval tv;
    gettimeofday(&tv, 0);

    struct input_event xEvent;
    xEvent.type = EV_ABS;
    xEvent.code = 0x00;
    xEvent.value = x;
    xEvent.time = tv;

    struct input_event yEvent;
    yEvent.type = EV_ABS;
    yEvent.code = 0x01;
    yEvent.value = y;
    yEvent.time = tv;

    write(fd, &xEvent, sizeof(xEvent)) ;
    write(fd, &yEvent, sizeof(yEvent)) ;

    // 等待时间
    struct input_event event;

}

// 模拟按键事件
void simulate_key(int fd,int kval) {
    struct input_event event;
    event.type = EV_KEY;
    event.value = 1;
    event.code = kval;

    gettimeofday(&event.time,0);
    write(fd,&event,sizeof(event)) ;

    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(fd, &event, sizeof(event));

    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_KEY;
    event.code = kval;
    event.value = 0;
    write(fd, &event, sizeof(event));
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(fd, &event, sizeof(event));
}

JNIEXPORT jint JNICALL Java_com_wanke_tv_QiangHongBaoService_home (JNIEnv* env, jobject obj) {
    int fd_kbd = open("/dev/input/event3", O_RDWR);
    if(fd_kbd<=0) {
        LOGD("open keybd error:%s",strerror(errno));
        return -2;
    }

    simulate_key(fd_kbd, KEY_HOME);
    close(fd_kbd);
    LOGD("Hello LIB!");
    return 0;
}

JNIEXPORT jint JNICALL Java_com_wanke_tv_QiangHongBaoService_touch (JNIEnv* env, jobject obj, jint x, jint y) {
    int fd_touch = open("/dev/input/event1", O_RDWR);
    if(fd_touch<=0) {
        LOGD("open touch error:%s",strerror(errno));
        return -2;
    }

    simulate_touch(fd_touch, 360, 900);
    close(fd_touch);
    return 0;
}
