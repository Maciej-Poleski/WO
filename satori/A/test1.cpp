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

std::vector<std::function<void()>> prepareTests()
{
    std::mt19937 engine(404);
    std::uniform_int_distribution<std::size_t> vertexRangeDistribution(1,maxN);
    auto vertexNumerGenerator = std::bind(vertexRangeDistribution,engine);
    
    std::vector<std::function<void()>> tests=
    {
        std::bind(generateEmptyGraph,1),
        std::bind(generateEmptyGraph,2),
        std::bind(generateEmptyGraph,maxN),
        std::bind(generateEmptyGraph,vertexNumerGenerator()),
        std::bind(generateEmptyGraph,vertexNumerGenerator()),
        std::bind(generateEmptyGraph,vertexNumerGenerator()),
        
    };
    
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
    runTests(prepareTests());
}
