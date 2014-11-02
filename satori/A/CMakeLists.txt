cmake_minimum_required(VERSION 2.8 FATAL_ERROR)
project(test)

set(CMAKE_CXX_FLAGS "-std=c++11")

add_executable(test test1.cpp)
add_executable(checker checker.cpp)
add_executable(size size.cpp)
