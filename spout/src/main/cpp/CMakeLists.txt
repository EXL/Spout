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
    add_definitions(-DANDROID_NDK -DGL_GLEXT_PROTOTYPES)
    set(HEADERS ${HEADERS}
                ru_exlmoto_spout_SpoutNativeLibProxy.h
                SpoutNativeLibProxy.h)
    set(SOURCES ${SOURCES}
                SpoutNativeLibProxy.cpp)
    add_library(${PROJECT_NAME} SHARED ${SOURCES} ${HEADERS})
    target_link_libraries(${PROJECT_NAME} log GLESv1_CM)
else()
    find_package(SDL)
    find_package(OpenGL)
    include_directories(${INCLUDE_DIRECTORIES} ${SDL_INCLUDE_DIR} ${OPENGL_INCLUDE_DIR})
    add_executable(${PROJECT_NAME} ${SOURCES} ${HEADERS})
    target_link_libraries(${PROJECT_NAME} ${SDL_LIBRARY} ${OPENGL_LIBRARIES})
endif()
