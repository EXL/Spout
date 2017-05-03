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
    set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})
    find_package(SDL)
    find_package(OpenGL)
    include_directories(${INCLUDE_DIRECTORIES} ${SDL_INCLUDE_DIR} ${OPENGL_INCLUDE_DIR})
    add_executable(${PROJECT_NAME} ${SOURCES} ${HEADERS})
    target_link_libraries(${PROJECT_NAME} ${SDL_LIBRARY} ${OPENGL_LIBRARIES})
    add_custom_target(finalMessage ALL
        ${CMAKE_COMMAND} -E cmake_echo_color --green "Output directory: ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE}, executable: ${PROJECT_NAME}."
        COMMENT "Final Message")
    add_dependencies(finalMessage ${PROJECT_NAME})
endif()
