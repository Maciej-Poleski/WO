#include <functional>
#include <vector>
#include <iostream>

std::vector<std::function<void()>> prepareTests()
{
    std::vector<std::function<void()>> tests=
    {
        
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
