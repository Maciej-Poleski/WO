struct T
{
    virtual ~T() { }
};

struct L;

struct R;

struct L : virtual T
{
    R * r;
};

struct R : virtual T
{
    L * l;
};

struct M : L, R
{
};

L * l;
M * m;
R * r;

int main()
{
    l = new M();
    m = new M();
    r = new M();
    l->r = m;
    m->r = r;
    r->l = m;
    m->l = l;
    l = nullptr;
    m = nullptr;
    r = nullptr;
    return 0;
}
