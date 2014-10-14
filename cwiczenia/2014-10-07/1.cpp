#include <vector>
#include <cstdlib>
using namespace std;

inline void zero(int & x)
{
    x = 0;
}

int main()
{
    int *t=new int[100];
    for (int i; i<=100; ++i)
        zero(t[i]);
    
    zero(t[108]);
    return 0;
}
