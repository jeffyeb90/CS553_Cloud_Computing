#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <float.h>
#include <limits.h>
#include <sys/time.h>
#include <string.h>

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
 		struct timeval seed;
        int i = gettimeofday(&seed, NULL);

        srand(seed.tv_usec);

        int blockSize = 1000;
        int totalSize = 2000000000;

        void *p = malloc(totalSize / 1);
                    
        FILE *f = fopen("testfile", "w");

        fread(p, blockSize, ( totalSize / blockSize ), f);
                    
        fclose(f);
                    
        free(p);

        printf("done writing\n");

        double interval = 0.0;

        double before;
        double after;

        //NTIME = 1;

        FILE *fp = fopen("testfile", "r");

        void *pointer = malloc(totalSize);

        void *offset = pointer;

        int m;

        before = getCurrentTime();
        printf("before is %f\n", before);

        for(m = 0; m < totalSize / blockSize; m++)
        {
        	fwrite(offset, blockSize, 1, fp);
        	offset = offset + blockSize;
        }


        //fread(pointer, blockSize, totalSize / blockSize, fp);

        after = getCurrentTime();
        printf("after is %f\n", after);
        interval = interval + (after - before);
                                
        free(pointer);
        fclose(fp);

        printf("throughput is %f\n", ((double)totalSize /interval / 1000000));

        return 0;
}








