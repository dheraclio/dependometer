#ifndef INTERFACE_CUSTOMER_CDIALOGS
#define INTERFACE_CUSTOMER_CDIALOGS

#include "Orders.cpp"
#include "Windows.cpp"

namespace Interface_Customer
{
class CDialogs
{
public:

	void buyDialog()
	{
	   Interface_Common::Windows win;
	   Application_Customer::Orders ord;
	    
	   win.in();
	   ord.buy();
	}
	
};
}
#endif 