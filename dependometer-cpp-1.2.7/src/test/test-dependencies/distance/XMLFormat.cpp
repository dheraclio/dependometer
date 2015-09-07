#include "BaseFormat.cpp"
#include "BaseAction.cpp"

 namespace formats
{
 class XMLFormat : public base::BaseFormat
    {
    public:
      void header()
      {
        base::BaseAction ba;
         ba.doAction();
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