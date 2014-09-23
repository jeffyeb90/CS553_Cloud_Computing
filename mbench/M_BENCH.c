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

	int blockSize;
	int totalSize;

	int NTIME;

	int type;

	int seq;

	char *str = "%d threads, %s, %s block size, have throughput %f MB/s, latency %f ns.\n";
	

	for(type = 0; type < 12; type++)
	{
		int numThreads;
		char *seqString;
		char *sizeString;
		

		if(type < 6)
			omp_set_num_threads(1);
		else
			omp_set_num_threads(2);

		if( (type < 3)||(type >= 6 && type < 9) )
		{
			seq = 1;
			seqString = "sequential";
		}
		else
		{
			seq = 0;
			seqString = "random";
		}


		if(type % 3 == 0)
		{
			blockSize = 1;
			totalSize = 1000000;
			sizeString = "1 Byte";
			NTIME = 200;
		}

		else if(type % 3 == 1)
		{
			blockSize = 1000;
			totalSize = 100000000;
			sizeString = "1 KByte";
			NTIME = 100;
		}
		else
		{
			blockSize = 1000000;
			totalSize = 100000000;
			sizeString = "1 MByte";
			NTIME = 100;
		}
		
   		int timeToRun = 0;

    		double interval = 0.0;

    		double before;
    		double after;


    		while (timeToRun < NTIME)
    		{
        		#pragma omp parallel
        		{
				int randNumber;

            			int mySize = totalSize / omp_get_num_threads();

            			void *source = malloc(mySize);
            			void *dest = malloc(mySize);

            			void *soffset;
            			void *doffset;

            			int i;

            			soffset = source;
            			doffset = dest;

            			#pragma omp barrier

            			#pragma omp master
            			{
							numThreads = omp_get_num_threads(); 
				
               				before = getCurrentTime();
            			}

            			for(i = 0; i < mySize / blockSize; i++)
            			{
					
               		 		memcpy(doffset, soffset, blockSize);
					
							if (seq == 0)
							{
								randNumber = rand() % mySize;
								if (randNumber > mySize - blockSize)
								{
									randNumber -= blockSize;
								}
								soffset = source + randNumber;
								doffset = dest + randNumber;	
							}
							else
							{
								soffset += blockSize;
								doffset += blockSize;
							}
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

    		//printf("time intverval is %f\n", interval);

    		printf(str, numThreads, seqString, sizeString, 
    				((double)totalSize * NTIME / interval / 1000000),
    				((double)interval * 1000000000 / totalSize / NTIME));
		sleep(3);
	}

    	return 0;
}


