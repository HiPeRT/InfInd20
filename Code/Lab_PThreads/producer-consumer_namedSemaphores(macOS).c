#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>

int a = 0;

// dichiaro un semaforo, e anche il suo nome
sem_t *s1;
static const char *semname1 = "Semaphore1";

void* producer_fn(void * arg)
{
    a = 11;

    sem_post(s1); // post del semaforo senza la "&"
    return NULL; // Ritorno NULL perchè se no mi dà dei warning fastidiosi, siccome è una non-void function (è void*)
}

void* consumer_fn(void * arg)
{   
    int b = 5;

    sem_wait(s1); //Attesa semaforo, senza la "&"
    printf("a is %d, and b is %d\n", a, b);
    return NULL; // Ritorno NULL perchè se no mi dà dei warning fastidiosi, siccome è una non-void function (è void*)
}

int main()
{
    //pthread_attr_t myattr; //Non serve
    pthread_t producer, consumer;
    void *returnvalue;
    int err;

    // Inizializzazione del semaforo 
   s1 = sem_open(semname1, O_CREAT, 0777, 0);
    /*
    Arg1:   *name -> Nome del semaforo
    Arg2:   oflag -> se impostato su 'O_CREATE' viene creato se non esiste. 
            Se viene impostato su 'O_CREATE | O_EXCL' verrà lanciato un errore nel caso il semaforo con lo stesso nome già esista.
    Arg3:   mode -> Maschera dei permessi Linux del semaforo
    Arg4:   value -> Valore iniziale del semaforo, se 0 indica un semaforo "locked", se maggiore di 0 un semaforo "unlocked"

    Fonte: http://www.qnx.com/developers/docs/qnxcar2/index.jsp?topic=%2Fcom.qnx.doc.neutrino.lib_ref%2Ftopic%2Fs%2Fsem_open.html
    Fonte: https://www.man7.org/linux/man-pages/man3/sem_open.3.html
    */

   if (s1 == SEM_FAILED)
   {
      fprintf(stderr, "%s\n", "ERROR creating semaphore semname1");
      return 1;
   }

     //Creo un primo thread
    //pthread_attr_init(&myattr);
    err = pthread_create (&consumer, NULL, consumer_fn, NULL); // Passo NULL al posto di myattr
    //pthread_attr_destroy(&myattr);

    //Creo un secondo thread => FORK TWO THREADS
    //pthread_attr_init(&myattr);
    err = pthread_create (&producer, NULL, producer_fn, NULL); // Passo NULL al posto di myattr
    //pthread_attr_destroy(&myattr);

    //Da qui in poi i due thread stanno andando...

    //Join dei due threads
    pthread_join(producer, &returnvalue);
    pthread_join(consumer, &returnvalue);


   sem_unlink(semname1); // Rimove il semaforo dato il suo nome

    return 0;
}