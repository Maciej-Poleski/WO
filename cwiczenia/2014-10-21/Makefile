ROOT := /home/studia/WO/

CPP := $(wildcard *.cpp)
EXE := $(patsubst %.cpp,%,${CPP})

all : ${EXE}

${EXE} : % : %.cpp
	g++ -std=c++11 -I"${ROOT}/include" $^ -Wall -O2 -o $@

clean :
	rm -f ${EXE}

.PHONY : all clean
