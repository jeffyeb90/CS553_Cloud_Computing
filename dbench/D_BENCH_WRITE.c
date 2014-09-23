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

        char *str = "%d threads, %s, %s block size, have throughput %f MB/s.\n";

	const char *fileString[4];

	fileString[0] = "file 0";
	fileString[1] = "file 1";
	fileString[2] = "file 2";
	fileString[3] = "file 3";

        for(type = 0; type < 18; type++)
        {
                int numThreads;
                char *seqString;
                char *sizeString;


                if(type < 6)
                        omp_set_num_threads(1);
                else if ((type >= 6) && (type < 12))
                        omp_set_num_threads(2);
        		else
        			omp_set_num_threads(4);


                if( (type < 3)||(type >= 6 && type < 9) || (type >= 12 && type <15))
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
                        NTIME = 10;
                }

                else if(type % 3 == 1)
                {
                        blockSize = 1000;
                        totalSize = 1000000000;
                        sizeString = "1 KByte";
                        NTIME = 10;
                }
                else
                {
                        blockSize = 1000000;
                        totalSize = 100000000;
			            sizeString = "1 MByte";
			            NTIME = 50;
		        }

	            int timeToRun = 0;

                double interval = 0.0;

                double before;
                double after;

                //NTIME = 1;


                while (timeToRun < NTIME)
                {
                        #pragma omp parallel
                        {

				                FILE *fp = fopen(fileString[omp_get_thread_num()], "w");
			
                                int randNumber;

                                int mySize = totalSize / omp_get_num_threads();

                                void *pointer = malloc(mySize);

                                void *offset = pointer;

                                int i;

                                #pragma omp barrier

                                #pragma omp master
                                {
                                        numThreads = omp_get_num_threads();

                                        before = getCurrentTime();
                                }

                                for(i = 0; i < mySize / blockSize; i++)
                                {
					                    fwrite(offset, blockSize, 1, fp);

                                        if (seq == 0)
                                        {
                                                randNumber = rand() % mySize;
                                                while(randNumber > mySize - blockSize)
                                                {
                                                        randNumber = rand() % mySize;
                                                }
						                        fseek(fp, randNumber, SEEK_SET);
                                        }
                                    
                                        offset++;
                                       
                                }

                                #pragma omp barrier

                                #pragma omp master
                                {
                                        after = getCurrentTime();
                                        interval = interval + (after - before);
                                }

                                free(pointer);
                                fclose(fp);
                        }

                        timeToRun++;
                }

                printf("time intverval is %f\n", interval);

                printf(str, numThreads, seqString, sizeString, (double)totalSize * NTIME / interval / 1000000);
              
        }

        return 0;
}





		
