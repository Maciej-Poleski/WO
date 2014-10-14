CPP := $(wildcard *.cpp)
EXE := $(patsubst %.cpp,%,${CPP})

all : ${EXE}

${EXE} : % : %.cpp
	g++ -std=c++11 $^ -g -o $@

.PHONY : all
