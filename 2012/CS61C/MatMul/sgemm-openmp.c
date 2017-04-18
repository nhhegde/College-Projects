//  Neil Hegde (c) 2012
#include <emmintrin.h>
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <nmmintrin.h>
#include <string.h>


void sgemm( int m, int n, float *A, float *C )
{
    
    __m128 AT;
    __m128 AT2;
    __m128 AT3;
    __m128 AT4;
    
    
    __m128 C0;
    

    for( int j = 0; j < m; j++ ){
        for( int k = 0; k < n*m/(4*m)*(4*m); k += 4*m ){
            AT = _mm_load1_ps(A+j+k);
            AT2 = _mm_load1_ps(A+j+k+m);
            AT3 = _mm_load1_ps(A+j+k+2*m);
            AT4 = _mm_load1_ps(A+j+k+3*m);
            for (int i = 0; i < m/4*4; i+=4) {
                C0 = _mm_loadu_ps(C+i+j*m);
                C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k + i)), AT));
                C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +m + i)), AT2));
                C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(2*m) + i)), AT3));
                C0 = _mm_add_ps(C0, _mm_mul_ps(_mm_loadu_ps((A + k +(3*m) + i)), AT4));
                _mm_storeu_ps((C+i+j*m), C0);
            }
            for (int i = m/4*4; i < m; i++) {
                C[j*m+i] += A[i+k]*A[j+k] + A[i+k+m]*A[j+k+m] + A[i+k+(2*m)]*A[j+k+(2*m)] + A[i+k+(3*m)]*A[j+k+(3*m)];
            }
            
        }
        for( int k = n*m/(4*m)*(4*m); k < n*m; k += m ){
            AT = _mm_load1_ps(A+j+k);
            for (int i = 0; i < m/4*4; i+=4) {
                _mm_storeu_ps((C+i+j*m), _mm_add_ps(_mm_loadu_ps(C+i+j*m), _mm_mul_ps(_mm_loadu_ps((A + k + i)), AT)));
            }
            for (int i = m/4*4; i < m; i++) {
                C[j*m+i] += A[i+k]*A[j+k];
            }
            
        }
        
    }

    
}
/*
float* padder(int m, int n, int padm, int padn, float *A) {
        float *padA = (float*) malloc(padm * padn * sizeof(float));

        for(int numx = 0; numx < n; numx++) {
                for(int numy = 0; numy < m; numy++) {
                        padA[numy + numx * padm] = A[numy + numx * m];
                }
                for(int i = m; i < padm; i++) {
                        padA[i + numx * padm] = 0;
                }
        }

        for(int numx = n; numx < padn; numx++) {
                for(int numy = 0; numy < padm; numy++) {
                        padA[numy + numx * padm] = 0;
                }
        }
        return padA;
}

float* unpadder(int startm, int endm, int endn, float *A){
        float *unpadA = (float*) malloc(endm * endn * sizeof(float));

        for(int numx = 0; numx < endn; numx++) {
                for(int numy = 0; numy < endm; numy++) {
                        unpadA[numy + numx * endm] = A[numy + numx * startm];
                }
        }
        return unpadA;
}
*/
