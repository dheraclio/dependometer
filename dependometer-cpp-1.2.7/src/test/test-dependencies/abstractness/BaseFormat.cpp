#ifndef BASEFORMAT
#define BASEFORMAT

namespace formats
{
   
    class BaseFormat
    {
    public:
      virtual ~BaseFormat() {}
      virtual void header() = 0;
      virtual void text(string const&, string const&) = 0;
      virtual void footer() = 0;
    };
 
}
#endif 



