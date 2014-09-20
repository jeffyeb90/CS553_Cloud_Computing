#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <float.h>
#include <limits.h>
#include <sys/time.h>
#include <string.h>

#ifndef BSIZE
#    define BSIZE 1
#endif

#ifndef TSIZE
#    define TSIZE 3000000000L
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
    



    while (timeToRun < NTIME)
    {
    	long i;
		
		void *source = malloc(TSIZE);
	    void *dest = malloc(TSIZE);

	    void *soffset;
	    void *doffset;

	    soffset = source;
	    doffset = dest;    	

	    before = getCurrentTime();

	    for (i = 0; i < TSIZE / BSIZE /100; i++)
	    {
	    	memcpy(doffset, soffset, BSIZE);
	    	soffset += (BSIZE+65);
	    	doffset += (BSIZE+65);
	    }

	    after = getCurrentTime();

	    interval = interval + (after - before);

	    timeToRun++;

	    free(source);
	    free(dest);
    }


    printf("Latency of copy one block of size %d byte is %lf ns.\n", BSIZE, (interval / (TSIZE / BSIZE / 100 * NTIME) * 1000000000) );

    return 0;

}





