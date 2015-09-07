#include "CPolygon.cpp"

namespace geometry
{
class CTriangle: public CPolygon {
  public:
    int area ()
      { return (width * height / 2); }
  };
  }