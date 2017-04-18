//  Neil Hegde (c) 2012
#include <emmintrin.h>
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <nmmintrin.h>
#include <string.h>


int pad(int m);

float* padArray(float* A, int padrows, int padcols, int rows, int cols);

void sgemm( int m, int n, float *A, float *C )
{
    
    __m128 AT;
    __m128 AT2;
    __m128 AT3;
    __m128 AT4;
    
    __m128 C0;
    
    if( n == 60 && m == 60){
        
        
        __m128 AT5;
        __m128 AT6;
        __m128 AT7;
        __m128 AT8;
        __m128 AT9;
        __m128 AT10;
        __m128 AT11;
        __m128 AT12;
        __m128 AT13;
        __m128 AT14;
        __m128 AT15;
        __m128 AT16;
        __m128 AT17;
        __m128 AT18;
        __m128 AT19;
        __m128 AT20;
        
        for( int j = 0; j < m; j++ ) {
            for( int k = 0; k < (n*m); k += 20*m ){
                AT = _mm_load1_ps(A+j+k);
                AT2 = _mm_load1_ps(A+j+k+m);
                AT3 = _mm_load1_ps(A+j+k+2*m);
                AT4 = _mm_load1_ps(A+j+k+3*m);
                AT5 = _mm_load1_ps(A+j+k+4*m);
                AT6 = _mm_load1_ps(A+j+k+5*m);
                AT7 = _mm_load1_ps(A+j+k+6*m);
                AT8 = _mm_load1_ps(A+j+k+7*m);
                AT9 = _mm_load1_ps(A+j+k+8*m);
                AT10 = _mm_load1_ps(A+j+k+9*m);
                AT11 = _mm_load1_ps(A+j+k+10*m);
                AT12 = _mm_load1_ps(A+j+k+11*m);
                AT13 = _mm_load1_ps(A+j+k+12*m);
                AT14 = _mm_load1_ps(A+j+k+13*m);
                AT15 = _mm_load1_ps(A+j+k+14*m);
                AT16 = _mm_load1_ps(A+j+k+15*m);
                AT17 = _mm_load1_ps(A+j+k+16*m);
                AT18 = _mm_load1_ps(A+j+k+17*m);
                AT19 = _mm_load1_ps(A+j+k+18*m);
                AT20 = _mm_load1_ps(A+j+k+19*m);
                for( int i = 0; i < m; i +=4 ){
                    C0 = _mm_loadu_ps(C+i+j*m);
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k + i)), AT));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +m + i)), AT2));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(2*m) + i)), AT3));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(3*m) + i)), AT4));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(4*m) + i)), AT5));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(5*m) + i)), AT6));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(6*m) + i)), AT7));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(7*m) + i)), AT8));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(8*m) + i)), AT9));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(9*m) + i)), AT10));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(10*m) + i)), AT11));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(11*m) + i)), AT12));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(12*m) + i)), AT13));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(13*m) + i)), AT14));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(14*m) + i)), AT15));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(15*m) + i)), AT16));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(16*m) + i)), AT17));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(17*m) + i)), AT18));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(18*m) + i)), AT19));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(19*m) + i)), AT20)); //Did not unroll inner loop because the compiler will do it for us.
                    _mm_storeu_ps((C+i+j*m), C0);
                }
                
            }
        }
        
    } else {
        int paddedrows = pad(m);
        int paddedcols = pad(n);
        if(paddedcols > n || paddedrows > m){
            int counter = 0;
//            printf("%d, %d\n", paddedcols, paddedrows);
            counter++;
            A = padArray(A, paddedrows, paddedcols, m, n);
            counter++;
            printf("2\n");
            counter++;
            printf("c: %d\n", counter);
            m = paddedrows;
            n = paddedcols;
        }
        for( int j = 0; j < m; j++ ){
            for( int k = 0; k < n*m; k += 4*m ){
                AT = _mm_load1_ps(A+j+k);
                AT2 = _mm_load1_ps(A+j+k + m);
                AT3 = _mm_load1_ps(A+j+k + 2*m);
                AT4 = _mm_load1_ps(A+j+k + 3*m);
                for (int i = 0; i < m; i+=4) {
                    C0 = _mm_loadu_ps(C+i+j*m);
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k + i)), AT));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +m + i)), AT2));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(2*m) + i)), AT3));
                    C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(3*m) + i)), AT4));
                    _mm_storeu_ps((C+i+j*m), C0);
                }
                
            }
        }

        

    }
}

int pad(int m){
    for (m; m % 4 != 0; m++);
    return m;
}


float* padArray(float* A, int padrows, int padcols, int rows, int cols){
    float *A2 = (float*) realloc((padrows*padcols * sizeof(float)));
    if (A2 == NULL){
        A2 = (float*) realloc(A, (padrows*padcols * sizeof(float)));
    }
    A = A2;
    printf("here\n");
    
    for (int x = cols; x < padcols; x++) {
        for (int y = rows; y < padrows; y++) {
            printf("i: %d", (x*padrows + y));

            printf("(%d, %d)111\n", x, y);
                        A[x*padrows + y] = 0.0;
        }
    }
    printf("1\n");
    return A;
}
