#include <algorithm>
#include <iostream>
#include <string>
#include <vector>
using namespace std;

void sort(string S[], int n)
{
    for (int i=0; i<n; ++i)
        for (int j=0; i+j<n; ++j)
            if (S[j] > S[j+1])
                swap(S[j], S[j+1]);
}

int find(string S[], string q, int lo, int hi)
{
    if (lo == hi)
        return lo;
    int me = (lo+hi) / 2;
    if (q < S[me])
        return find(S, q, lo, me);
    else
        return find(S, q, me, hi);
}

int main()
{
    vector<string> S;
    int n;
    cin >> n;
    for (int i=0; i<n; ++i)
    {
        string s;
        cin >> s;
        S.push_back(s);
    }
    sort(S.data(), S.size());
    int pos = find(S.data(), "query", 0, S.size());
    cout << pos << endl;
    return 0;
}
