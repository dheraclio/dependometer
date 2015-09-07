#include "BaseFormat.cpp"

namespace formats
{
 class CSVFormat : public BaseFormat
    {
    public:
      void header()
      {
      }

      void text(string const& name, string const& value)
      {
        cout<<<name<<';'<<value<<'\n';
      }

      void footer()
      {
      }
    };
  }