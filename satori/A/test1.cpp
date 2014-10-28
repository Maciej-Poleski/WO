#include <functional>
#include <vector>
#include <iostream>
#include <random>

constexpr std::size_t maxN=100000;
constexpr std::size_t maxM=1000000;

void generateEmptyGraph(std::size_t size)
{
    std::cout<<size<<" 0\n";
}

template<class Generator>
void generateRandomEdges(Generator& engine, const std::size_t vertices, const std::size_t edges)
{
    std::uniform_int_distribution<int> vertexGenerator(1,vertices);
    auto vertex=std::bind(vertexGenerator,std::ref(engine));
    std::cout<<vertices<<' '<<edges<<'\n';
    for(std::size_t i=0;i<edges;++i)
	std::cout<<vertex()<<' '<<vertex()<<'\n';
}

template<class Generator>
std::vector<std::function<void()>> prepareTests(Generator& engine)
{
    std::uniform_int_distribution<std::size_t> vertexRangeDistribution(1,maxN);
    auto vertexNumerGenerator = std::bind(vertexRangeDistribution,std::ref(engine));

    std::vector<std::function<void()>> tests=
    {
        std::bind(generateEmptyGraph,1),
        std::bind(generateEmptyGraph,2),
        std::bind(generateEmptyGraph,maxN),
        std::bind(generateEmptyGraph,vertexNumerGenerator()),
        std::bind(generateEmptyGraph,vertexNumerGenerator()),
        std::bind(generateEmptyGraph,vertexNumerGenerator())
    };

    for(const std::size_t vc : {5ul,10ul,20ul,50ul,maxN})	// Liczba wierzchołków
	for(const std::size_t ec :{vc, vc*2, vc*vc, vc*vc*2})	// Liczba krawędzi
	{
	    if(ec>maxM)
		continue;
	    for(std::size_t i=0;i<3;++i)			// Zestawy
		tests.push_back([&engine,vc,ec]{ generateRandomEdges(engine,vc,ec); });
	}


    return tests;
}

void runTests(const std::vector<std::function<void()>>& tests)
{
    std::cout<<tests.size()<<'\n';
    for(const auto t : tests)
        t();
}

int main()
{
    std::ios_base::sync_with_stdio(false);
    std::mt19937 engine(404);
    runTests(prepareTests(engine));
}
