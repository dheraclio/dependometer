#ifndef APPLICATION_CUSTOMER_ORDERS
#define APPLICATION_CUSTOMER_ORDERS

#include "Products.cpp"

namespace Application_Customer
{
class Orders
{
public:

	void buy()
	{
	   Application_Common::Products pd;
	   pd.getProd();
	}
	
	void cancel()
	{
	   Application_Common::Products pd;
	   pd.removeProd();
	}
	
};
}
#endif 