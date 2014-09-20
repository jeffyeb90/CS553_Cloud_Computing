#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <float.h>
#include <limits.h>
#include <sys/time.h>
#include <string.h>

#ifndef BSIZE
#    define BSIZE 1000
#endif

#ifndef TSIZE
#    define TSIZE 2000000000
#endif

#ifndef NTIME
#   define NTIME 10
#endif

double getCurrentTime()
{
    struct timeval tp;
    struct timezone tzp;
    int i;

    i = gettimeofday(&tp,&tzp);

    return ( (double) tp.tv_sec + (double) tp.tv_usec * 1.e-6 );
}


int main()
{
    int timeToRun = 0;

    double before;
    double after;
    double interval;




    interval = 0.0;


    void *pointer = malloc(1000);

    
    FILE *fp = fopen("file", "w");

    fwrite(pointer, 100, 10, fp);

    fclose(fp);

    free(pointer);


    printf("time intverval is %f\n", interval);

    printf("the throughput is %f MB/s.\n", (double)TSIZE * NTIME / interval / 1000000);

    return 0;
}
