#include <cstdio>
using namespace std;

int main()
{
    int n = 100;
    char * data = new char[n];
    int k = fread(data, 1, n, stdin);
    fwrite(data, 1, n, stdout);
    return 0;
}
