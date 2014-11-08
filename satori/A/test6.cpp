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
	    if(i+1<=N)
	    {
		std::cout<<i<<' '<<i+1<<'\n';
		m+=1;
	    }
	}
	std::uniform_int_distribution<int> edgeDist(1,6);
	for(;m<maxM;)
	{
	    for(int i=1;i<=N && m<maxM;++i)
	    {
		std::cout<<i<<' '<<std::min(i+edgeDist(engine),N)<<'\n';
		m+=1;
	    }
	}
    }
    return 0;
}
