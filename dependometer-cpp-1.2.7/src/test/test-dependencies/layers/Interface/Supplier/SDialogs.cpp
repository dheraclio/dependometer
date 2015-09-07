#ifndef INTERFACE_SUPPLIER_DIALOGS
#define INTERFACE_SUPPLIER_DIALOGS

#include "Windows.cpp"
#include "Products.cpp"

namespace Interface_Supplier
{
class SDialogs
{
public:
	
	void productEntryDialog()  
	{
	   Interface_Common::Windows win;
	   Application_Common::Products prod;
	    
	   win.in();
	   prod.newProd();
	}
};
}
#endif 