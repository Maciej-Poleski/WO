#include <algorithm>
#include <cstdio>
#include <queue>
#include <vector>
#include <cassert>
using namespace std;

const int MAXV = 100000;
const int MAXE = 1000000;
const int INFTY = MAXV;

int color[MAXV];
int number[MAXV];
int nextnumber;
int low[MAXV];
int dist[MAXV];
vector<int> edges[MAXV];
int answer;

void clear(vector<int>& v)
{
    vector<int> w;
    v.swap(w);
}

void dfs(const int v, const int t)
{
    assert(color[v]==0);
    color[v] = 1;
    low[v] = number[v] = nextnumber++;
    for (int i = 0; i < edges[v].size(); ++i)
    {
        int w = edges[v][i];
        if(w==t)
            continue;
        if (color[w] == 0)
        {
            dfs(w, v);
            if (low[w] < low[v])
                low[v] = low[w];
        }
        else if (number[w] < low[v])
            low[v] = number[w];
    }
    if ((low[v] < number[v]) || ((t==MAXV) && (edges[v].size()>1)))
       answer = max(answer, dist[v]);
}

int main()
{
    int Z, V, E, S;
    for (scanf("%d", &Z); Z; --Z)
    {
        scanf("%d%d%d", &V, &E, &S);
        for (int v=0; v<V; ++v) // Indeksacja przesunięta o jeden
        {
            color[v] = 0;
            clear(edges[v]);
        }
        nextnumber=0;
        answer=0;
        for (int e=0; e<E; ++e)
        {
            int v, w;
            scanf("%d%d", &v, &w);
            edges[v-1].push_back(w-1);
            edges[w-1].push_back(v-1);
        }
        queue<int> Q;
        for (int v=0; v<MAXV; ++v)
            dist[v] = INFTY;    // Wystarczy?
        dist[S-1] = 0;
        Q.push(S-1);
        while (!Q.empty())
        {
            int v = Q.front(); Q.pop();
            for (int i=0; i<edges[v].size(); ++i)
            {
                int w = edges[v][i];
                if (dist[w] == INFTY)
                {
                    dist[w] = dist[v] + 1;
                    Q.push(w);
                }
            }
        }
        //dfs(S, 0);      // Poza zakresem (na odwrót?)
        dfs(0,MAXV);
        printf("%d\n", answer);
    }
    return 0;
}

