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

	int NTIME;	//how many while loops

	int type;	//type stands for different parameter combinations

	int seq;	//if this is a sequential access

	char *str = "%d threads, %s, %s block size, have throughput %f MB/s, latency %f ns.\n";
	

	for(type = 0; type < 12; type++)
	{
		int numThreads;		//number of threads
		char *seqString;	
		char *sizeString;
		

		if(type < 6)
			omp_set_num_threads(1);		//single thread
		else	
			omp_set_num_threads(2);		//2 threads

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

		//1 byte block size
		if(type % 3 == 0)
		{
			blockSize = 1;
			totalSize = 1000000;
			sizeString = "1 Byte";
			NTIME = 200;
		}

		//1 Kbyte block size
		else if(type % 3 == 1)
		{
			blockSize = 1000;
			totalSize = 100000000;
			sizeString = "1 KByte";
			NTIME = 100;
		}

		//1 Mbyte block size
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
        		#pragma omp parallel	//open mp parallel execution
        		{
						int randNumber;

            			int mySize = totalSize / omp_get_num_threads();		//each thread is assigned a sub-total size 

            			void *source = malloc(mySize);
            			void *dest = malloc(mySize);

            			void *soffset;		
            			void *doffset;

            			int i;

            			soffset = source;	//set offset at the beginning
            			doffset = dest;		//set offset at the beginning

            			#pragma omp barrier	//set the barrier in order to record begginning time

            			#pragma omp master  
            			{
							numThreads = omp_get_num_threads(); 
				
               				before = getCurrentTime();
            			}

            			for(i = 0; i < mySize / blockSize; i++)
            			{
							//copy a block-size memory from source to destination
               		 		memcpy(doffset, soffset, blockSize);
					
							if (seq == 0)	//if random access
							{
								randNumber = rand() % mySize;
								if (randNumber > mySize - blockSize)	//in case the randomized offset is at the tail
								{
									randNumber -= blockSize;
								}
								soffset = source + randNumber;
								doffset = dest + randNumber;	
							}
							else		//if sequential access
							{
								soffset += blockSize;
								doffset += blockSize;
							}
            			}

            			#pragma omp barrier	//make a barrier in order to record the ending time

            			#pragma omp master
            			{
                			after = getCurrentTime();		//record the ending time
                			interval = interval + (after - before);
            			}

            			free(source);
            			free(dest);
        		}

        		timeToRun++;
    		}

    		//printf("time intverval is %f\n", interval);

    		printf(str, numThreads, seqString, sizeString, 
    				((double)totalSize * NTIME / interval / 1000000),		//print throughput
    				((double)interval * 1000000000 / totalSize / NTIME));	//print latency
		sleep(3);
	}

    	return 0;
}


