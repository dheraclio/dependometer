#ifndef CPOLYGON
#define CPOLYGON

namespace geometry
{
class CPolygon {
  protected:
    int width, height;
  public:
    void set_values (int a, int b)
      { width=a; height=b;}
  };
}
#endif 



