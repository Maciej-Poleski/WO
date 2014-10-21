#include <relacy/relacy.hpp>
#include <algorithm>
#include <utility>
using namespace rl;

template <int T>
struct test : test_suite<test<T>, T>
{
    atomic<bool>    entering[T];
    atomic<int>     number[T];

    test()
    {
        for (int i=0; i<T; ++i)
            entering[i]($) = false;
        for (int i=0; i<T; ++i)
            number[i]($) = 0;
    }

    void thread(unsigned i)
    {
        entering[i]($).store(true);
        int num = 0;
        for (unsigned j=0; j<T; ++j)
            num = std::max(num, number[j]($).load());
        number[i]($).store(num+1);
        entering[i]($).store(false);

        for (unsigned j=0; j<T; ++j)
        {
            while (entering[j]($).load());
            while (number[j]($).load() &&
                   std::make_pair(number[j]($).load(), j) < std::make_pair(number[i]($).load(), i));
        }

        // critical section

        number[i]($).store(0);
    }
};

int main()
{
    simulate<test<2>>();
    return 0;
}


