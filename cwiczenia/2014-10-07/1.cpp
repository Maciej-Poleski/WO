#include <vector>
using namespace std;

inline void zero(int & x)
{
    x = 0;
}

int main()
{
    vector<int> t(100);
    for (int i=0; i<=100; ++i)
        zero(t[i]);
    return 0;
}
