cmake_minimum_required(VERSION 3.4.1)
project("Spout")

option(ANDROID "Enable Android Build instead Desktop" ON)

include_directories(${PROJECT_NAME})

set(HEADERS ${PROJECT_NAME}/config.h
            ${PROJECT_NAME}/font.h
            ${PROJECT_NAME}/piece.h
            ${PROJECT_NAME}/sintable.h
            ${PROJECT_NAME}/spout.h)

set(SOURCES ${PROJECT_NAME}/piece.c
            ${PROJECT_NAME}/spout.c)

if(ANDROID)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -ffast-math -fomit-frame-pointer -DANDROID_NDK")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -frtti -ffast-math -fomit-frame-pointer -DANDROID_NDK")
    set(HEADERS ${HEADERS}
                ru_exlmoto_spout_SpoutNativeLibProxy.h
                SpoutNativeLibProxy.h)
    set(SOURCES ${SOURCES}
                SpoutNativeLibProxy.cpp)
    add_library(${PROJECT_NAME} SHARED ${SOURCES} ${HEADERS})
    target_link_libraries(${PROJECT_NAME} log GLESv1_CM)
else()

endif()