#include <algorithm>
#include <cstdio>
#include <queue>
#include <vector>
#include <cassert>
using namespace std;

const int MAXV = 100000;
//const int MAXE = 1000000;
const int INFTY = MAXV+5;

int *number;
int nextnumber;
int *low;
int *dist;
vector<int> *edges;
int answer;

template<class T>
void clear(T& v)
{
    T w;
    swap(v,w);
}

void dfs(const int v, const int t)
{
    low[v] = number[v] = nextnumber++;
    for (int i = 0; i < edges[v].size(); ++i)
    {
        int w = edges[v][i];
        if(w==t)
            continue;
        if (number[w]==-1)
        {
            dfs(w, v);
            if (low[w] < low[v])
                low[v] = low[w];
            
            if((t!=-1) && (low[w] >= number[v]))
                answer = max(answer, dist[v]);
        }
        else if (number[w] < low[v])
            low[v] = number[w];
    }
}

int main()
{
    int Z, V, E, S;
    for (scanf("%d", &Z); Z; --Z)
    {
        scanf("%d%d%d", &V, &E, &S);
        nextnumber=0;
        answer=0;
        edges=new vector<int>[V];
        for (int e=0; e<E; ++e)
        {
            int v, w;
            scanf("%d%d", &v, &w);
//             if(v>V || w>V)
//                 continue;
            edges[v-1].push_back(w-1);
            edges[w-1].push_back(v-1);
        }
        queue<int> Q;
        dist=new int[V];
        for (int v=0; v<V; ++v)
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
        clear(Q);
        //dfs(S, 0);      // Poza zakresem (na odwrót?)
        number=new int[V];
        for (int v=0; v<V; ++v)
        {
            number[v] = -1;
        }
        low=new int[V];
        dfs(S-1,-1);
        delete [] edges;
        delete [] dist;
        delete [] number;
        delete [] low;
        printf("%d\n", answer);
    }
    return 0;
}

