#ifndef DEBUG
#define NDEBUG
#endif
#include <functional>
#include <vector>
#include <iostream>
#include <random>
#include <cassert>
#include <queue>
#include <algorithm>
#include <memory>

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

public:
    using VertexRange = std::pair<int,int>;
    using VertexGenerator = std::function<VertexRange()>;

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
	if(u!=v)
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
    std::size_t generateTree(Generator& engine, VertexGenerator vgen, std::size_t vertices, const std::function<std::size_t()> chldCountGenerator, const std::function<bool()> deathGenerator);

private:
    std::vector<Vertex> G;
    int n=0;
    int m=0;
};

void Graph::serialize()
{
    assert(n==G.size());
    assert(n<=maxN);
    assert(m<=maxM);
    std::cout<<n<<' '<<m<<'\n';
    std::size_t edges=0;
    for(std::size_t u=0;u<n;++u)
    {
	assert(u<n);
	for(const auto v : G[u].edges)
	{
	    assert(v<n);
	    if(u<=v)
	    {
		std::cout<<u+1<<' '<<v+1<<'\n';
		edges+=1;
	    }
	}
    }
    assert(edges==m);
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
	const auto v=vgen();
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
    if(vertices>1)
	assert(VI.first.second<VI.second.first);
    else
	assert(VI.first==VI.second);
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
std::size_t
Graph::generateTree(Generator& engine, const VertexGenerator vgen, const std::size_t vertices, const std::function<std::size_t()> chldCountGenerator, const std::function<bool()> deathGenerator)
{
    assert(vertices>0);
    std::queue<VertexRange> queue;
    queue.push(vgen());
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
	    makeEdge(engine,vDist(engine),chld);
	    queue.push(chld);
	}
    }
    return vertices-verticesLeft;
}

template<class Generator>
std::function<std::size_t()> makePoissonDistributionGenerator(Generator& generator, double mean)
{
    std::poisson_distribution<std::size_t> dist(mean);
    return [&generator,dist]() mutable {
	return dist(generator);
    };
}

template<class Generator>
std::function<std::size_t()> makeGeometricDistributionGenerator(Generator& generator, double p)
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

//     for(const std::size_t vc : {5ul,10ul,20ul,50ul,maxN})	// Liczba wierzchołków
    for(const std::size_t vc : {5ul,50ul,1000ul,maxN})	// Liczba wierzchołków
// 	for(const std::size_t ec :{vc, vc*2, vc*(maxM/maxN), vc*vc, vc*vc*2})	// Liczba krawędzi
	for(const std::size_t ec : {10,10000,1000000})
	{
	    if(ec>maxM)
		continue;
	    for(std::size_t i=0;i<3;++i)			// Zestawy
		tests.push_back([&engine,vc,ec]{ generateRandomEdges(engine,vc,ec); });
	}

//     for(std::size_t n:{1,80,2000,100000})
    for(std::size_t n:{80,2000,100000})
    {
	/*
	 * Mogą być statefull (żeby pilnować ilość stworzonych wierzchołków/krawędzi)
	 */
	std::vector<std::function<Graph::VertexGenerator(Graph&)>> vertexFactories;

	const auto singleVertexGeneratorFactory = [](Graph& g) {
	    return std::bind(&Graph::newVertex,&g);
	};

	if(n!=1 && n!=maxN)
	{
	    const double EV = (maxN-double(n))/n*5.0+1.0;	// Najpierw rzuć monetą!
	    const double EE = (maxM-double(n-1))/n*5.0;
	    vertexFactories={
		// Tylko cykl
		[n,&engine,singleVertexGeneratorFactory,EV] (Graph& g) {
		    std::function<std::size_t()> cycleSizeGenerator=makePoissonDistributionGenerator(engine,EV);
		    std::shared_ptr<std::size_t> verticesLeft=std::make_shared<std::size_t>(maxN-n);
		    const auto vgen = singleVertexGeneratorFactory(g);
		    const auto wantSingle=makeBernoulliDistributionGenerator(engine,0.8);
		    return [&g,&engine,vgen,cycleSizeGenerator,verticesLeft,wantSingle] () {
			if(wantSingle())
			    return g.newVertex();
			else
			{
			    for(;;)
			    {
				const std::size_t size=std::min(cycleSizeGenerator(),*verticesLeft+1);
				if(size==0)
				    continue;
				*verticesLeft-=size-1;
				return g.generateCycle(engine,vgen,size);
			    }
			}
		    };
		},
		// Cykl + ścieżki
		[n,&engine,singleVertexGeneratorFactory,EV,EE] (Graph& g)
		{
		    std::function<std::size_t()> verticesAllocationGenerator=makePoissonDistributionGenerator(engine,EV);	// Przydział wierzchołków dla tej DSS
		    std::function<std::size_t()> cycleSizeGenerator=makePoissonDistributionGenerator(engine,EV/3.0);
		    std::function<std::size_t()> pathSizeGenerator=makePoissonDistributionGenerator(engine,EV/9.0);
		    std::shared_ptr<std::size_t> verticesLeft=std::make_shared<std::size_t>(maxN-n);
		    std::shared_ptr<std::size_t> edgesLeft=std::make_shared<std::size_t>(maxM-(n-1));
		    const auto vgen = singleVertexGeneratorFactory(g);
		    const auto wantSingle=makeBernoulliDistributionGenerator(engine,0.8);
		    return [&g,&engine,vgen,verticesAllocationGenerator,cycleSizeGenerator,pathSizeGenerator,verticesLeft,edgesLeft,wantSingle] () {
			if(wantSingle() || *edgesLeft==0)
			    return g.newVertex();
			else
			{
			    for(;;)
			    {
				std::size_t verticesAllocation=verticesAllocationGenerator();
				if(*verticesLeft+1<verticesAllocation)
				    verticesAllocation=*verticesLeft+1;
				const auto VEmin=std::min(verticesAllocation,*edgesLeft);
				const std::size_t cycleSize=std::min(cycleSizeGenerator(),VEmin);
				if(cycleSize==0)
				    continue;
				*verticesLeft-=cycleSize-1;
				verticesAllocation-=cycleSize;
				*edgesLeft-=cycleSize;
				Graph::VertexRange wholeRange=g.generateCycle(engine,vgen,cycleSize);
				auto lastRange=wholeRange;
				while(verticesAllocation>0 && *edgesLeft>1)
				{
				    const auto thisPathSize=std::min(pathSizeGenerator(),verticesAllocation);
				    if(thisPathSize==0)
				    {
					// Tylko krawędź
					g.makeEdge(engine,lastRange,wholeRange);
					*edgesLeft-=1;
				    }
				    else
				    {
					std::pair<Graph::VertexRange,Graph::VertexRange> pathRange=g.generatePath(engine,vgen,thisPathSize);
					*verticesLeft-=thisPathSize;
					verticesAllocation-=thisPathSize;
					assert(pathRange.first.first==pathRange.first.second);
					assert(pathRange.second.first==pathRange.second.second);
					g.makeEdge(engine,lastRange,pathRange.first);
					g.makeEdge(engine,wholeRange,pathRange.second);
					*edgesLeft-=2;
					assert(wholeRange.second+1==pathRange.first.first);
					wholeRange.second=pathRange.second.second;
					lastRange=std::make_pair(pathRange.first.first,pathRange.second.second);
				    }
				}
				return wholeRange;
			    }
			}
		    };
		},
		// Cykl + ścieżki + krawędzie
		[n,&engine,singleVertexGeneratorFactory,EV,EE] (Graph& g)
		{
		    std::function<std::size_t()> verticesAllocationGenerator=makePoissonDistributionGenerator(engine,EV);	// Przydział wierzchołków dla tej DSS
		    std::function<std::size_t()> edgesAllocationGenerator=makePoissonDistributionGenerator(engine,EE);	// Przydział krawędzi dla tej DSS
		    std::function<std::size_t()> cycleSizeGenerator=makePoissonDistributionGenerator(engine,EV/3.0);
		    std::function<std::size_t()> pathSizeGenerator=makePoissonDistributionGenerator(engine,EV/9.0);
		    std::shared_ptr<std::size_t> verticesLeft=std::make_shared<std::size_t>(maxN-n);
		    std::shared_ptr<std::size_t> edgesLeft=std::make_shared<std::size_t>(maxM-(n-1));
		    const auto vgen = singleVertexGeneratorFactory(g);
		    const auto wantSingle=makeBernoulliDistributionGenerator(engine,0.8);
		    return [&g,&engine,vgen,verticesAllocationGenerator,edgesAllocationGenerator,cycleSizeGenerator,pathSizeGenerator,verticesLeft,edgesLeft,wantSingle] () {
			if(wantSingle() || *edgesLeft==0)
			    return g.newVertex();
			else
			{
			    for(;;)
			    {
				std::size_t verticesAllocation=verticesAllocationGenerator();
				std::size_t edgesAllocation=edgesAllocationGenerator();
				if(*verticesLeft+1<verticesAllocation)
				    verticesAllocation=*verticesLeft+1;
				if(*edgesLeft<edgesAllocation)
				    edgesAllocation=*edgesLeft;
				const auto VEmin=std::min(verticesAllocation,edgesAllocation);
				const std::size_t cycleSize=std::min(cycleSizeGenerator(),VEmin);
				if(cycleSize==0)
				    continue;
				*verticesLeft-=cycleSize-1;
				verticesAllocation-=cycleSize;
				*edgesLeft-=cycleSize;
				edgesAllocation-=cycleSize;
				Graph::VertexRange wholeRange=g.generateCycle(engine,vgen,cycleSize);
				auto lastRange=wholeRange;
				while(verticesAllocation>0 && edgesAllocation>1)
				{
				    const auto thisPathSize=std::min(pathSizeGenerator(),std::min(verticesAllocation,edgesAllocation-1));
				    if(thisPathSize==0)
				    {
					// Tylko krawędź
					g.makeEdge(engine,lastRange,wholeRange);
					*edgesLeft-=1;
					edgesAllocation-=1;
				    }
				    else
				    {
					std::pair<Graph::VertexRange,Graph::VertexRange> pathRange=g.generatePath(engine,vgen,thisPathSize);
					*verticesLeft-=thisPathSize;
					verticesAllocation-=thisPathSize;
					*edgesLeft-=thisPathSize-1;
					edgesAllocation-=thisPathSize-1;
					assert(pathRange.first.first==pathRange.first.second);
					assert(pathRange.second.first==pathRange.second.second);
					g.makeEdge(engine,lastRange,pathRange.first);
					g.makeEdge(engine,wholeRange,pathRange.second);
					*edgesLeft-=2;
					edgesAllocation-=2;
					assert(wholeRange.second+1==pathRange.first.first);
					wholeRange.second=pathRange.second.second;
					lastRange=std::make_pair(pathRange.first.first,pathRange.second.second);
				    }
				}
				while(edgesAllocation>0)
				{
				    g.makeEdge(engine,wholeRange,wholeRange);
				    *edgesLeft-=1;
				    edgesAllocation-=1;
				}
				return wholeRange;
			    }
			}
		    };
		},
	    };
	}
	else if(n==1)
	{
	    const double EV = (maxN-double(n))/n*5.0+1.0;	// Najpierw rzuć monetą!
	    const double EE = (maxM-double(n-1))/n*5.0;
	    vertexFactories={
		// Tylko cykl długości maxN
		[&engine,singleVertexGeneratorFactory] (Graph& g) {
		    const auto vgen = singleVertexGeneratorFactory(g);
		    return [&g,&engine,vgen] () {
			return g.generateCycle(engine,vgen,maxN);
		    };
		},
		// Cykl + ścieżki
		[n,&engine,singleVertexGeneratorFactory,EV,EE] (Graph& g)
		{
		    std::function<std::size_t()> cycleSizeGenerator=makePoissonDistributionGenerator(engine,EV/3.0);
		    std::function<std::size_t()> pathSizeGenerator=makePoissonDistributionGenerator(engine,EV/9.0);
		    std::shared_ptr<std::size_t> verticesLeft=std::make_shared<std::size_t>(maxN-n);
		    std::shared_ptr<std::size_t> edgesLeft=std::make_shared<std::size_t>(maxM-(n-1));
		    const auto vgen = singleVertexGeneratorFactory(g);
		    return [&g,&engine,vgen,cycleSizeGenerator,pathSizeGenerator,verticesLeft,edgesLeft] () {
			for(;;)
			{
			    std::size_t verticesAllocation=maxN;
			    if(*verticesLeft+1<verticesAllocation)
				verticesAllocation=*verticesLeft+1;
			    const auto VEmin=std::min(verticesAllocation,*edgesLeft);
			    const std::size_t cycleSize=std::min(cycleSizeGenerator(),VEmin);
			    if(cycleSize==0)
				continue;
			    *verticesLeft-=cycleSize-1;
			    verticesAllocation-=cycleSize;
			    *edgesLeft-=cycleSize;
			    Graph::VertexRange wholeRange=g.generateCycle(engine,vgen,cycleSize);
			    auto lastRange=wholeRange;
			    while(verticesAllocation>0 && *edgesLeft>1)
			    {
				const auto thisPathSize=std::min(pathSizeGenerator(),std::min(verticesAllocation,*edgesLeft-1));
				if(thisPathSize==0)
				{
				    // Tylko krawędź
				    g.makeEdge(engine,lastRange,wholeRange);
				    *edgesLeft-=1;
				}
				else
				{
				    std::pair<Graph::VertexRange,Graph::VertexRange> pathRange=g.generatePath(engine,vgen,thisPathSize);
				    *verticesLeft-=thisPathSize;
				    verticesAllocation-=thisPathSize;
				    *edgesLeft-=thisPathSize-1;
				    assert(pathRange.first.first==pathRange.first.second);
				    assert(pathRange.second.first==pathRange.second.second);
				    g.makeEdge(engine,lastRange,pathRange.first);
				    g.makeEdge(engine,wholeRange,pathRange.second);
				    *edgesLeft-=2;
				    assert(wholeRange.second+1==pathRange.first.first);
				    wholeRange.second=pathRange.second.second;
				    lastRange=std::make_pair(pathRange.first.first,pathRange.second.second);
				}
			    }
			    return wholeRange;
			}
		    };
		},
		// Cykl + ścieżki + krawędzie
		[n,&engine,singleVertexGeneratorFactory,EV,EE] (Graph& g)
		{
		    std::function<std::size_t()> cycleSizeGenerator=makePoissonDistributionGenerator(engine,EV/3.0);
		    std::function<std::size_t()> pathSizeGenerator=makePoissonDistributionGenerator(engine,EV/9.0);
		    std::shared_ptr<std::size_t> verticesLeft=std::make_shared<std::size_t>(maxN-n);
		    std::shared_ptr<std::size_t> edgesLeft=std::make_shared<std::size_t>(maxM-(n-1));
		    const auto vgen = singleVertexGeneratorFactory(g);
		    return [&g,&engine,vgen,cycleSizeGenerator,pathSizeGenerator,verticesLeft,edgesLeft] () {
			if(*edgesLeft==0)
			    return g.newVertex();
			else
			{
			    for(;;)
			    {
				std::size_t verticesAllocation=maxN;
				std::size_t edgesAllocation=maxM;
				if(*verticesLeft+1<verticesAllocation)
				    verticesAllocation=*verticesLeft+1;
				if(*edgesLeft<edgesAllocation)
				    edgesAllocation=*edgesLeft;
				const auto VEmin=std::min(verticesAllocation,edgesAllocation);
				const std::size_t cycleSize=std::min(cycleSizeGenerator(),VEmin);
				if(cycleSize==0)
				    continue;
				*verticesLeft-=cycleSize-1;
				verticesAllocation-=cycleSize;
				*edgesLeft-=cycleSize;
				edgesAllocation-=cycleSize;
				Graph::VertexRange wholeRange=g.generateCycle(engine,vgen,cycleSize);
				auto lastRange=wholeRange;
				while(verticesAllocation>0 && edgesAllocation>1)
				{
				    const auto thisPathSize=std::min(pathSizeGenerator(),std::min(verticesAllocation,edgesAllocation-1));
				    if(thisPathSize==0)
				    {
					// Tylko krawędź
					g.makeEdge(engine,lastRange,wholeRange);
					*edgesLeft-=1;
					edgesAllocation-=1;
				    }
				    else
				    {
					std::pair<Graph::VertexRange,Graph::VertexRange> pathRange=g.generatePath(engine,vgen,thisPathSize);
					*verticesLeft-=thisPathSize;
					verticesAllocation-=thisPathSize;
					*edgesLeft-=thisPathSize-1;
					edgesAllocation-=thisPathSize-1;
					assert(pathRange.first.first==pathRange.first.second);
					assert(pathRange.second.first==pathRange.second.second);
					g.makeEdge(engine,lastRange,pathRange.first);
					g.makeEdge(engine,wholeRange,pathRange.second);
					*edgesLeft-=2;
					edgesAllocation-=2;
					assert(wholeRange.second+1==pathRange.first.first);
					wholeRange.second=pathRange.second.second;
					lastRange=std::make_pair(pathRange.first.first,pathRange.second.second);
				    }
				}
				while(edgesAllocation>0)
				{
				    g.makeEdge(engine,wholeRange,wholeRange);
				    *edgesLeft-=1;
				    edgesAllocation-=1;
				}
				return wholeRange;
			    }
			}
		    };
		},
	    };
	}
	else if(n==maxN)
	{
	    vertexFactories={ singleVertexGeneratorFactory };
	}

	for(const auto vertexFactory : vertexFactories)
	{
	    std::vector<std::function<void(Graph&)>> generators={
		[&engine,vertexFactory,n] (Graph& g) {
		    g.generatePath(engine,vertexFactory(g),n);
		},
		[&engine,vertexFactory,n] (Graph& g) {
		    g.generateStar(engine,vertexFactory(g),n);
		},
		[&engine,vertexFactory,n] (Graph& g) {
		    g.generateVeryWideTree(engine,vertexFactory(g),n);
		},
		[&engine,vertexFactory,n] (Graph& g) {
		    auto sizeLeft=n;
		    const auto vgen=vertexFactory(g);
		    while(sizeLeft>0)
		    {
			const auto I=g.generateTree(engine,vgen,sizeLeft,makePoissonDistributionGenerator(engine,3.5),makeBernoulliDistributionGenerator(engine,0.33));
			sizeLeft-=I;
		    }
		},
		[&engine,vertexFactory,n] (Graph& g) {
		    auto sizeLeft=n;
		    const auto vgen=vertexFactory(g);
		    while(sizeLeft>0)
		    {
			const auto I=g.generateTree(engine,vgen,sizeLeft,makePoissonDistributionGenerator(engine,100),makeBernoulliDistributionGenerator(engine,0.1));
			sizeLeft-=I;
		    }
		},
		[&engine,vertexFactory,n] (Graph& g) {
		    auto sizeLeft=n;
		    const auto vgen=vertexFactory(g);
		    while(sizeLeft>0)
		    {
			const auto I=g.generateTree(engine,vgen,sizeLeft,makeGeometricDistributionGenerator(engine,0.85),makeBernoulliDistributionGenerator(engine,0.05));
			sizeLeft-=I;
		    }
		},
	    };
	    std::transform(generators.begin(),generators.end(),std::back_inserter(tests),buildGraphAndSerialize);
	}
    }

    for(int i=1;i<=32;i+=2)
    {
	tests.push_back([i] () {
	    std::cout<<maxN<<' '<<maxM<<'\n';
	    for(int j=0;j<maxM;++j)
	    {
		std::cout<<i<<' '<<i+1<<'\n';
	    }
	});
    }

    tests.push_back([] () {
	std::cout
	<<"3\n"
	<<"1 1\n"
	<<"1 1\n"
	<<"2 2\n"
	<<"1 1\n"
	<<"2 2\n"
	<<"3 2\n"
	<<"1 1\n"
	<<"2 2\n";
    });

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

