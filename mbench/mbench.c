#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <float.h>
#include <limits.h>
#include <sys/time.h>
#include <string.h>

#ifndef BSIZE
#    define BSIZE 1024
#endif

#ifndef TSIZE
#    define TSIZE 2000000000
#endif

#ifndef NTIME
#   define NTIME 500
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

    double interval = 0.0;    

    double before;
    double after;


    while (timeToRun < NTIME)
    {
        #pragma omp parallel
        {

            int myTSIZE = TSIZE / omp_get_num_threads();

            void *source = malloc(myTSIZE);
            void *dest = malloc(myTSIZE);

            void *soffset;
            void *doffset;

            int i;

            soffset = source;
            doffset = dest;

            #pragma omp barrier

            #pragma omp master
            {
                before = getCurrentTime();
            }

            for(i = 0; i < myTSIZE/BSIZE/100; i++)
            {
                memcpy(doffset, soffset, BSIZE);
                soffset += (BSIZE+65);
                doffset += (BSIZE+65);
            }

            #pragma omp barrier

            #pragma omp master
            {
                after = getCurrentTime();
                interval = interval + (after - before);
            }

            free(source);
            free(dest);
        }

        timeToRun++;
    }

    printf("time intverval is %f\n", interval);

    printf("the throughput is %f MB/s.\n", (double)TSIZE * NTIME / 100 / interval / 1000000);

    return 0;
}



    






