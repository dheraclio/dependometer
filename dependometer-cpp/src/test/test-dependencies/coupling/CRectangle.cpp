#include "CPolygon.cpp"

 namespace geometry
{
class CRectangle: public CPolygon {
  public:
    int area ()
      { return (width * height); }
  };
  }