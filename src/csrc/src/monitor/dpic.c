#include "tet.h"
#include <utils/macro.h>
#include <utils/debug.h>


uint8_t softmax_input[16][16] = {{1, 8, 1, 8, 1, 8, 1, 8, 1, 8, 1, 8, 1, 8, 1, 8},
                                 {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                                 {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4},
                                 {1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 8, 8, 8, 8, 4}};


extern "C" void softmax_read_matrix(int line_num, char *line_data) {
    for (int i = 0; i < 16; i++) {
        line_data[i] = softmax_input[line_num][i];
    }
}
