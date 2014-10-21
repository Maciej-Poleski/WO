#include <relacy/relacy.hpp>
#include <cstdio>
using namespace rl;

struct test : test_suite<test, 2>
{
    atomic<unsigned>    turn;
    atomic<bool>        wants[2];

    var<int> v;
    
    test()
    {
        turn($) = -1;
        wants[0]($) = wants[1]($) = false;
    }

    void thread(unsigned i)
    {
        wants[i]($).store(true,mo_release);
        turn($).exchange(1-i,mo_release);
        while (wants[i^1]($).load() && turn($).load() == (i^1));
        // critical section goes here
        v($)=5;
        
        wants[i]($).store(false,mo_release);
    }
};

int main()
{
    simulate<test>();
    return 0;
}

