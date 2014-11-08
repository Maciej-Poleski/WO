#include <iostream>
#include <random>

int main()
{
    constexpr int z=40;
    std::cout<<z<<'\n';
    std::mt19937 engine(404);
    for(auto i=z;i;--i)
    {
	constexpr int N=100000;
	constexpr int maxM=1000000;
	std::cout<<N<<' '<<maxM<<'\n';
	int m=0;
	for(int i=1;i<=N;++i)
	{
	    std::cout<<1<<' '<<i<<'\n';
	    m+=1;
	}
	std::uniform_int_distribution<int> edgeDist(1,N);
	for(;m<maxM;++m)
	{
	    std::cout<<edgeDist(engine)<<' '<<edgeDist(engine)<<'\n';
	}
    }
    return 0;
}
