#include <algorithm>
#include <cstdio>
#include <vector>
using namespace std;

const int MAXV = 100000;
const int MAXE = 1000000;

int numv, nume;
vector<int> edges[MAXV + 1];

int nums;        // liczba odwiedzonych wierzchołków
int num[MAXV + 1]; // numer preorder wierzchołka
int par[MAXV + 1]; // numer preorder rodzica w drzewie DFS
int low[MAXV + 1]; // numer "low"
int art[MAXV + 1]; // dla ilu krawędzi drzewowych wierzchołek jest punktem artykulacji

// obliczanie indeksów low[], znajdowanie punktów artykulacji
void dfs1(int u, int p = 0)
{
    low[u] = num[u] = ++nums;
    par[u] = p;
    art[u] = 0;
    for(int i = 0; i < edges[u].size(); ++i)
    {
        int v = edges[u][i];
        if(v == p)
            continue;
        if(num[v] == 0)
        {
            dfs1(v, u);
            if(low[v] >= num[u])
                art[u]++;
            low[u] = min(low[u], low[v]);
        }
        else
            low[u] = min(low[u], num[v]);
    }
}

int bccs;                // liczba znalezionych duwspójnych składowych
vector<int> bcc[MAXV + 1]; // sąsiedzi w grafie dwuspójnych składowych

// konstrukcja grafu dwuspójnych składowych
void dfs2(int u, int b = 0)
{
    for(int i = 0; i < edges[u].size(); ++i)
    {
        int v = edges[u][i];
        if(par[v] != u)
            continue;
        if(low[v] >= num[u])
        {
            int bb = ++bccs;
            dfs2(v, bb);
            if(par[u] > 0 || art[u] > 1)
                bcc[bb].push_back(u);
        }
        else
            dfs2(v, b);
    }
    if(/*par[u] > 0 && */art[u] > 0)
        bcc[b].push_back(u);
    par[u] = 0;
}

void testcase()
{
    // wczytywanie danych
    scanf("%d%d", &numv, &nume);
    for(int e = 0; e < nume; ++e)
    {
        int u, v;
        scanf("%d%d", &u, &v);
        if(u == v)
            continue;
        edges[u].push_back(v);
        edges[v].push_back(u);
    }
    // inicjalizacja
    for(int v = 1; v <= numv; ++v)
        low[v] = num[v] = 0;
    // znajdź punkty artykulacji
    nums = 0;
    for(int v = 1; v <= numv; ++v)
        if(num[v] == 0)
            dfs1(v);
    // wyznacz dwuspójne składowe
    bccs = 0;
    for(int v = 1; v <= numv; ++v)
        if(par[v] == 0)
            dfs2(v);
    // uwzględnij wierzchołki izolowane
    int exits = 0;
    for(int v = 1; v <= numv; ++v)
        if(edges[v].size() == 0)
            exits++;
    // uwzględnij liście w lesie dwuspójnych składowych
    for(int b = 1; b <= bccs; ++b)
        switch(bcc[b].size())
        {
        case 0:
            exits += 2;
            break;
        case 1:
            exits += 1;
            break;
        }
    if(exits == 1)
        exits = 0;
    printf("%d\n", exits);
    // sprzątanie
    for(int v = 1; v <= numv; ++v)
        vector<int>().swap(edges[v]);
    for(int b = 1; b <= bccs; ++b)
        vector<int>().swap(bcc[b]);
}

int main()
{
    int z;
    scanf("%d", &z);
    while(z--)
        testcase();
    return 0;
}
