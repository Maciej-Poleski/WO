#include <functional>
#include <vector>
#include <iostream>
#include <random>
#include <cassert>
#include <queue>
#include <algorithm>

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

/**
 * Graf nieskierowany
 */
class Graph
{
    struct Vertex
    {
	std::vector<int> edges;
    };

    using VertexRange = std::pair<int,int>;
    using VertexGenerator = std::function<VertexRange()>;

public:
    Graph() {}

    void serialize();

    VertexRange newVertex()
    {
	int v=n++;
	G.push_back({{}});
	assert(G.size()==n);
	return std::make_pair(v,v);
    }

    void makeEdge(int u, int v)
    {
	G[u].edges.push_back(v);
	G[v].edges.push_back(u);
	m+=1;
    }

    template<class Generator>
    void makeEdge(Generator& engine, int u, VertexRange v);

    template<class Generator>
    void makeEdge(Generator& engine, VertexRange u, VertexRange v);

    /**
     * Generuje ścieżke, zwraca początek i koniec (jako przedziały identyfikatorów)
     * @return Para przedziałów stworzonych identyfikatorów wierzchołków (begVInt,endVInt)
     */
    template<class Generator>
    std::pair<VertexRange,VertexRange> generatePath(Generator& engine, VertexGenerator vgen, std::size_t vertices);

    /**
     * Generuje cykl
     * @return Przedział stworzonych identyfikatorów wierzchołków ([;])
     */
    template<class Generator>
    VertexRange generateCycle(Generator& engine, VertexGenerator vgen, std::size_t vertices);

    /**
     * Generuje gwiazde (drzewo).
     * @return korzeń i przedział wszystkich stworzonych identyfikatorów (rootInt,fullInt)
     */
    template<class Generator>
    std::pair<VertexRange,VertexRange> generateStar(Generator& engine, VertexGenerator vgen, std::size_t vertices);

    /**
     * Generuje bardzo szerokie drzewo (1-2 poziomy)
     * @return korzeń i przedział wszystkich stworzonych identyfikatorów (rootInt,fullInt)
     */
    template<class Generator>
    std::pair<VertexRange,VertexRange> generateVeryWideTree(Generator& engine, VertexGenerator vgen, std::size_t vertices);

    /**
     * Generuje drzewo o stopniu węzłów zadanym rozkładem Poissona i prawdopodobieństwem śmierci
     * @return korzeń i przedział wszystkich stworzonych identyfikatorów (rootInt,fullInt)
     */
    template<class Generator>
    std::pair<VertexRange,VertexRange> generateTree(Generator& engine, VertexGenerator vgen, std::size_t vertices, const std::function<std::size_t()> chldCountGenerator, const std::function<bool()> deathGenerator);

private:
    std::vector<Vertex> G;
    int n=0;
    int m=0;
};

void Graph::serialize()
{
    std::cout<<n<<' '<<m<<'\n';
    for(std::size_t u=0,e=G.size();u<e;++u)
	for(const auto v : G[u].edges)
	    if(u<=v)
		std::cout<<u+1<<' '<<v+1<<'\n';
}

template<class Generator>
void Graph::makeEdge(Generator& engine, int u, VertexRange v)
{
    std::uniform_int_distribution<int> vVGen(v.first,v.second);
    makeEdge(u,vVGen(engine));
}

template<class Generator>
void Graph::makeEdge(Generator& engine, VertexRange u, VertexRange v)
{
    std::uniform_int_distribution<int> uVGen(u.first,u.second);
    std::uniform_int_distribution<int> vVGen(v.first,v.second);
    makeEdge(uVGen(engine),vVGen(engine));
}

template<class Generator>
std::pair<Graph::VertexRange,Graph::VertexRange> Graph::generatePath(Generator& engine, VertexGenerator vgen, std::size_t vertices)
{
    assert(vertices>0);
    auto oldVInterval=vgen();
    const VertexRange firstV=oldVInterval;
    std::uniform_int_distribution<int> oldVGen(oldVInterval.first,oldVInterval.second);
    for(std::size_t i=1;i<vertices;++i)
    {
	const auto v=newVertex();
	assert(v.first==oldVInterval.second+1);
	std::uniform_int_distribution<int> newVGen(v.first,v.second);
	makeEdge(oldVGen(engine),newVGen(engine));
	oldVInterval=v;
	oldVGen=newVGen;
    }
    return std::make_pair(firstV,oldVInterval);
}

template<class Generator>
Graph::VertexRange Graph::generateCycle(Generator& engine, VertexGenerator vgen, std::size_t vertices)
{
    const auto VI=generatePath(engine,vgen,vertices);
    assert(VI.first.second<VI.second.first);
    makeEdge(engine,VI.first,VI.second);
    return std::make_pair(VI.first.first,VI.second.second);
}

template<class Generator>
std::pair<Graph::VertexRange,Graph::VertexRange> Graph::generateStar(Generator& engine, VertexGenerator vgen, std::size_t vertices)
{
    assert(vertices>0);
    const auto root=vgen();
    std::uniform_int_distribution<int> rootGen(root.first,root.second);
    VertexRange v=root;		// Jeżeli wygenerowano tylko korzeń
    for(std::size_t i=1;i<vertices;++i)
    {
	v=vgen();
	makeEdge(engine,rootGen(engine),v);
    }
    return std::make_pair(root,std::make_pair(root.first,v.second));
}

template<class Generator>
std::pair<Graph::VertexRange,Graph::VertexRange> Graph::generateVeryWideTree(Generator& engine, VertexGenerator vgen, std::size_t vertices)
{
    assert(vertices>0);
    const auto root=vgen();
    auto verticlesLeft=vertices-1;
    std::uniform_int_distribution<int> rootGen(root.first,root.second);
    VertexRange v=root;		// Jeżeli wygenerowano tylko korzeń
    for(std::size_t i=0,e=std::size_t(sqrt(vertices-1));i<e;++i)
    {
	if(verticlesLeft==0)
	    break;
	std::uniform_int_distribution<std::size_t> sizeDist(1,verticlesLeft);
	const auto subSize=sizeDist(engine);
	verticlesLeft-=subSize;
	const auto child=generateStar(engine,vgen,subSize);
	makeEdge(engine,root,child.first);
	v=child.second;
    }
    if(verticlesLeft>0)
    {
	const auto child=generateStar(engine,vgen,verticlesLeft);
	makeEdge(engine,root,child.first);
	v=child.second;
    }
    return std::make_pair(root,std::make_pair(root.first,v.second));
}

template<class Generator>
std::pair<Graph::VertexRange,Graph::VertexRange>
Graph::generateTree(Generator& engine, const VertexGenerator vgen, const std::size_t vertices, const std::function<std::size_t()> chldCountGenerator, const std::function<bool()> deathGenerator)
{
    assert(vertices>0);
    const auto root=vgen();
    auto end=root.second;
    std::queue<VertexRange> queue;
    queue.push(root);
    auto verticesLeft=vertices-1;
    while(!queue.empty() && verticesLeft>0)
    {
	auto v=queue.front();
	queue.pop();
	if(deathGenerator())
	    continue;
	std::uniform_int_distribution<int> vDist(v.first,v.second);
	for(std::size_t i=0,e=chldCountGenerator();i<e && verticesLeft>0;++i)
	{
	    auto chld=vgen();
	    verticesLeft-=1;
	    end=chld.second;
	    makeEdge(engine,vDist(engine),chld);
	    queue.push(chld);
	}
    }
    return std::make_pair(root,std::make_pair(root.first,end));
}

template<class Generator>
std::function<std::size_t()> makePoissonChildrenCountGenerator(Generator& generator, double mean)
{
    std::poisson_distribution<std::size_t> dist(mean);
    return [&generator,dist]() mutable {
	return dist(generator);
    };
}

template<class Generator>
std::function<std::size_t()> makeGeometricChildrenCountGenerator(Generator& generator, double p)
{
    std::geometric_distribution<std::size_t> dist(p);
    return [&generator,dist]() mutable {
	return dist(generator)+1;
    };
}

template<class Generator>
std::function<bool()> makeBernoulliDistributionGenerator(Generator& generator, double p)
{
    std::bernoulli_distribution dist(p);
    return [&generator,dist]() mutable {
	return dist(generator);
    };
}

std::function<void()> buildGraphAndSerialize(std::function<void(Graph&)> builder)
{
    return [builder] ()
    {
	Graph g;
	builder(g);
	g.serialize();
    };
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
	for(const std::size_t ec :{vc, vc*2, vc*(maxM/maxN), vc*vc, vc*vc*2})	// Liczba krawędzi
	{
	    if(ec>maxM)
		continue;
	    for(std::size_t i=0;i<3;++i)			// Zestawy
		tests.push_back([&engine,vc,ec]{ generateRandomEdges(engine,vc,ec); });
	}

    std::vector<std::function<void(Graph&)>> generators={
	[&engine] (Graph& g) {
	    g.generatePath(engine,std::bind(&Graph::newVertex,&g),maxN);
	},
	[&engine] (Graph& g) {
	    g.generateStar(engine,std::bind(&Graph::newVertex,&g),maxN);
	},
	[&engine] (Graph& g) {
	    g.generateVeryWideTree(engine,std::bind(&Graph::newVertex,&g),maxN);
	},
	[&engine] (Graph& g) {
	    auto sizeLeft=maxN;
	    while(sizeLeft>0)
	    {
		const auto I=g.generateTree(engine,std::bind(&Graph::newVertex,&g),sizeLeft,makePoissonChildrenCountGenerator(engine,3.5),makeBernoulliDistributionGenerator(engine,0.33));
		sizeLeft-=1+I.second.second-I.second.first;
// 		std::cerr<<sizeLeft<<'\n';
	    }
	},
	[&engine] (Graph& g) {
	    auto sizeLeft=maxN;
	    while(sizeLeft>0)
	    {
		const auto I=g.generateTree(engine,std::bind(&Graph::newVertex,&g),sizeLeft,makePoissonChildrenCountGenerator(engine,100),makeBernoulliDistributionGenerator(engine,0.1));
		sizeLeft-=1+I.second.second-I.second.first;
// 		std::cerr<<sizeLeft<<'\n';
	    }
	},
	[&engine] (Graph& g) {
	    auto sizeLeft=maxN;
	    while(sizeLeft>0)
	    {
		const auto I=g.generateTree(engine,std::bind(&Graph::newVertex,&g),sizeLeft,makeGeometricChildrenCountGenerator(engine,0.85),makeBernoulliDistributionGenerator(engine,0.05));
		sizeLeft-=1+I.second.second-I.second.first;
// 		std::cerr<<sizeLeft<<'\n';
	    }
	},
    };

    std::transform(generators.begin(),generators.end(),std::back_inserter(tests),buildGraphAndSerialize);

    return tests;
}

void runTests(const std::vector<std::function<void()>>& tests)
{
    std::cout<<tests.size()<<'\n';
    for(const auto t : tests)
        t();
}

template<class T,class U>
std::ostream& operator<<(std::ostream& out, const std::pair<T,U>& p)
{
    return out<<'('<<p.first<<' '<<p.second<<')';
}

int main()
{
    std::ios_base::sync_with_stdio(false);
    std::mt19937 engine(404);
    runTests(prepareTests(engine));
}

