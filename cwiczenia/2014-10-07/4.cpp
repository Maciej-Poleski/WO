#include <iostream>
using namespace std;

int main()
{
    int n;
    cin >> n;
    for (int i=0; i<n; i++)
    {
        char * s = new char[100];
        cin >> s;
        int k = 0;
        for (int j=0; s[j]; j++)
            if (s[j]=='a')
                k++;
        cout << k << endl;
        delete[] s;
    }
    return 0;
}
