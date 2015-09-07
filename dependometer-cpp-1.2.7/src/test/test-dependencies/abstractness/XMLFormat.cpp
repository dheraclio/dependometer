#include "BaseFormat.cpp"

 namespace formats
{
 class XMLFormat : public BaseFormat
    {
    public:
      void header()
      {
        cout<<"<definition>\n";
      }

      void text(string const& name, string const& value)
      {
        cout<<"\t<"<<name<<'>'<<value<<"</"<<name<<">\n";
      }

      void footer()
      {
        cout<<"</definition>\n";
      }
    };
  }