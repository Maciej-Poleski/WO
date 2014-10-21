#include <relacy/relacy.hpp>
using namespace rl;

struct test : test_suite<test, 2>
{
    atomic<bool> initialized;
    atomic<bool> published;

    test() : initialized(false), published(false)
    {
    }

    void thread(unsigned i)
    {
        if (i)
        {
            initialized($).store(true,mo_relaxed);
            published($).store(true,mo_release);
        }
        else
        {
            if (published($).load(mo_acquire))
                assert(initialized($).load(mo_relaxed));
        }
    }
};

int main()
{
    simulate<test>();
    return 0;
}


