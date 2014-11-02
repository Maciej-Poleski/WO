#include <iostream>
#include <string>

[[noreturn]] void die(const std::string& str,int r)
{
    std::cout<<str<<'\n';
    exit(r);
}

constexpr long maxN=100000;
constexpr long maxM=1000000;

int main()
{
    long z;
    std::cin>>z;
    if(z<0)
    {
	std::cout<<"Ujemna ilość zestawów testowych\n";
	return 1;
    }
    while(z--)
    {
	long n,m;
	std::cin>>n>>m;
	if(n<0)
	    die("Ujemna ilość wierzchołków",2);
	if(m<0)
	    die("Ujemna ilość krawędzi",3);
	if(n>maxN)
	    die("Przekroczono maksymalną dopuszczalną ilość wierzchołków ("+std::to_string(n)+")",4);
	if(m>maxM)
	    die("Przekroczono maksymalną dopuszczalną ilość krawędzi",5);
	while(m--)
	{
	    long a,b;
	    std::cin>>a>>b;
	    if(a<=0 || b<=0)
		die("Wierzchołek ma identyfikator poniżej dopuszczalnego zakresu identyfikatorów",6);
	    if(a>n || b>n)
		die("Wierzchołek ma identyfikator powyżej dopuszczalnego zakresu identyfikatorów ("+std::to_string(std::max(a,b))+">"+std::to_string(n)+")",7);
	}
    }
    std::cout<<"OK\n";
    return 0;
}