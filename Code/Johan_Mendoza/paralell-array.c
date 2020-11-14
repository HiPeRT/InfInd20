#include <stdio.h>
#include <pthread.h>

const unsigned int NUM_THREADS = 4;
const unsigned int NUM_ELEMENT = 20;
//const unsigned int chuncks = NUM_ELEMENT/NUM_THREADS;
int index = 0;


void * pthreads_fn(void * args)
{
    int *val_p = (int *) args;

    for(int i = 0; i<NUM_ELEMENT/NUM_THREADS; i++)
    {
        val_p[index] = index*2;
        index++;

    }

    printf("CHUNCK\n");
    //sleep(10);
    
    return NULL;

}


int main() 
{
    int err;
    //const unsigned int NUM_THREADS = 16;
    pthread_t mythreads[NUM_THREADS];
    pthread_attr_t myattr;
    void *returnvalue;

    int array[NUM_ELEMENT];

    
    
    
    for (int i = 0; i<NUM_THREADS; i++)
    {
        pthread_attr_init(&myattr);
        int err = pthread_create(&mythreads[i], &myattr, pthreads_fn, (void *)array );
        pthread_attr_destroy(&myattr);
       
    }

    for(int i=0; i<NUM_ELEMENT; i++)
    {
        printf("array[%d] = %d\n",i,array[i]);
    }


    for(int i=0; i<NUM_THREADS; i++)
    {
        pthread_join(mythreads[i], &returnvalue);
    }

    //size_t n = sizeof(array)/sizeof(array[0]);

    return 0;
}