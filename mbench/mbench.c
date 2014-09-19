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
    void *source = malloc(TSIZE);
    void *dest = malloc(TSIZE);

//    printf("source begins at %p\n", source);
//    printf("dest begins at %p\n", dest);

    void *soffset;
    void *doffset;

    int i;

    double before;
    double after;
    double interval;

    soffset = source;
    doffset = dest;

    before = getCurrentTime();

    for(i = 0; i < TSIZE/BSIZE; i++)
    {
//	printf("copying %d data from %p to %p\n", BSIZE, soffset, doffset);
	memcpy(soffset, doffset, BSIZE);
	soffset += BSIZE;
	doffset += BSIZE;
    }

    after = getCurrentTime();

    interval = after - before;

    printf("time intverval is %f\n", interval);

    printf("the throughput is %f MB/s.\n", (double)TSIZE / interval / 1000000.00);

    return 0;
}



    






