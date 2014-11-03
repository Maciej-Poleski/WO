#include <iostream>

constexpr int n=1414;
constexpr int m=n*(n-1)/2;

int main()
{
    std::cout<<1<<'\n';
    std::cout<<n<<' '<<m<<'\n';
    for(int a=1;a<=n;++a)
    {
        for(int b=a+1;b<=n;++b)
        {
            std::cout<<a<<' '<<b<<'\n';
        }
    }
    return 0;
}
