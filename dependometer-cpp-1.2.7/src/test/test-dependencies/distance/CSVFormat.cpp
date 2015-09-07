#include "BaseFormat.cpp"
#include "BaseAction.cpp"

namespace formats
{
 class CSVFormat : public base::BaseFormat
    {
    public:
      void header()
      {
         base::BaseAction ba;
         ba.doAction();
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