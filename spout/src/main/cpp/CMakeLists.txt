##########################################################################################
## The MIT License (MIT)
##
## Copyright (c) 2017 EXL
##
## Permission is hereby granted, free of charge, to any person obtaining a copy
## of this software and associated documentation files (the "Software"), to deal
## in the Software without restriction, including without limitation the rights
## to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
## copies of the Software, and to permit persons to whom the Software is
## furnished to do so, subject to the following conditions:
##
## The above copyright notice and this permission notice shall be included in all
## copies or substantial portions of the Software.
##
## THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
## IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
## FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
## AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
## LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
## OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
## SOFTWARE.
##########################################################################################

cmake_minimum_required(VERSION 3.4.1)
project("Spout")

option(ANDROID "Enable Android Build instead Desktop" ON)

include_directories(${PROJECT_NAME})

set(SOURCES ${PROJECT_NAME}/piece.c
            ${PROJECT_NAME}/spout.c)

if(ANDROID)
    add_definitions(-DANDROID_NDK -DGL_GLEXT_PROTOTYPES)
    set(SOURCES ${SOURCES}
                SpoutNativeLibProxy.cpp)
    add_library(${PROJECT_NAME} SHARED ${SOURCES})
    target_link_libraries(${PROJECT_NAME} log GLESv1_CM)
else()
    set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})
    find_package(SDL)
    find_package(OpenGL)
    include_directories(${INCLUDE_DIRECTORIES} ${SDL_INCLUDE_DIR} ${OPENGL_INCLUDE_DIR})
    add_executable(${PROJECT_NAME} ${SOURCES})
    target_link_libraries(${PROJECT_NAME} ${SDL_LIBRARY} ${OPENGL_LIBRARIES})
    add_custom_target(finalMessage ALL
        ${CMAKE_COMMAND} -E cmake_echo_color --green "Output directory: ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE}, executable: ${PROJECT_NAME}."
        COMMENT "Final Message")
    add_dependencies(finalMessage ${PROJECT_NAME})
endif()
