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

        int blockSize;      //size of a block      
        int totalSize;      //total size of the disk file

        int type;   //type stands for different combination of parameters

        int seq;    //if this is a sequentical access

        char *str = "%d threads, %s, %s block size, have throughput %f MB/s, latency %f ns.\n";

        //multiple threads reading from different files
        const char *fileString[4];

        fileString[0] = "file 0";
        fileString[1] = "file 1";
        fileString[2] = "file 2";
        fileString[3] = "file 3";

        

        for(type = 0; type < 18; type++)
        {
                int numThreads;     //number of threads
                char *seqString;
                char *sizeString;


                if(type < 6)
                {
                        omp_set_num_threads(1);
                        numThreads = 1;
                }
                else if ((type >= 6) && (type < 12))
                {
                        omp_set_num_threads(2);
                        numThreads = 2;
                }
                else
                {
                        omp_set_num_threads(4);
                        numThreads = 4;
                }

                

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
                        blockSize = 1;              //block size is 1 byte
                        totalSize = 100000;
                        sizeString = "1 Byte";
                }

                else if(type % 3 == 1)

                {
                        blockSize = 1000;           //block size is 1 Kbyte
                        totalSize = 500000000;
                        sizeString = "1 KByte";
                    

                }
                else
                {
                        blockSize = 1000000;        //block size is 1 Mbyte
                        totalSize = 500000000;
                        sizeString = "1 MByte";
                }



                int j;

                //first write a file to be read later

                for(j = 0; j < numThreads; j++)
                {
                    void *p = malloc(totalSize / numThreads);
                    
                    FILE *f = fopen(fileString[j], "w");

                    fwrite(p, blockSize, ( totalSize / blockSize / numThreads ), f);
                    
                    fclose(f);
                    
                    free(p);
                }

                int timeToRun = 0;

                double interval = 0.0;

                double before;
                double after;


                #pragma omp parallel        //run in parallel using open mp
                {

                    FILE *fp = fopen(fileString[omp_get_thread_num()], "r");

                    int randNumber;

                    int mySize = totalSize / omp_get_num_threads();     //each thread has a sub-size to read from disk

                    void *pointer = malloc(totalSize);                  //allocate a piece of memeory to store the read data

                    void *offset = pointer;                             

                    int i;

                    #pragma omp barrier     //make a barrier in order to record the beginning time.

                    #pragma omp master
                    {
                            if(numThreads != omp_get_num_threads())
                                printf("error, wrong thureads number\n");
                            before = getCurrentTime();              //only master records the time
                    }

                            for(i = 0; i < totalSize / blockSize; i++)
                            {
                                fread(offset, blockSize, 1, fp);        //for each time, read a block-size piece 

                                if (seq == 0)   //if it's random access
                                {
                                    randNumber = rand() % mySize;       //randomize the position of the file
                                    if(randNumber > mySize - blockSize) //in case that the position is at the tail
                                    {
                                            randNumber -= blockSize;
                                    }
                                    fseek(fp, randNumber, SEEK_SET);
                                }

                                offset = offset + blockSize;
                            }

                            #pragma omp barrier     //make a barrier to record the ending time.

                            #pragma omp master
                            {
                                after = getCurrentTime();
                                interval = interval + (after - before);
                            }

                            free(pointer);
                            
                            fclose(fp);
                              
                }

                //printf("time intverval is %f\n", interval);

                printf(str, numThreads, seqString, sizeString, 
                    ((double)totalSize / interval / 1000000),               //print the throughput
                    ((double)interval * 1000000000 / totalSize));           //print the latency

                sleep(2);
        }

        return 0;
}






