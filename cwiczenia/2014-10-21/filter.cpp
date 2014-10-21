#include <relacy/relacy.hpp>
#include <cstdio>
using namespace rl;

template <int T>
struct test : test_suite<test<T>, T>
{
    atomic<int>     level[T];
    atomic<int>     victim[T-1];
    
    var<int> v;

    test()
    {
        for (int i=0; i<T; ++i)
            level[i]($) = -1;
        for (int i=0; i<T-1; ++i)
            victim[i]($) = -1;
    }

    bool sameOrHigher(unsigned i, int j)
    {
        for (int k=0; k<T; ++k)
            if (k!=i && level[k]($).load() >= j)
                return true;
        return false;
    }

    void thread(unsigned i)
    {
        for (int j=0; j<T-1; ++j)
        {
            level[i]($).store(j);
            victim[j]($).store(i);
            while (sameOrHigher(i, j) && victim[j]($).load() == i);
        }

        // critical section
        v($)=5;

        level[i]($).store(-1);
    }
};

int main()
{
    test_params pi;
    pi.search_type=sched_full;
    pi.execution_depth_limit=100000;
    simulate<test<2>>(pi);
    return 0;
}


