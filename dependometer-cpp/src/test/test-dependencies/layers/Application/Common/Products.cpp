#ifndef APPLICATION_COMMON_PRODUCTS
#define APPLICATION_COMMON_PRODUCTS

#include "Database.cpp"

namespace Application_Common
{
class Products
{
public:

    void newProd()
	{
	   Storage_Common::Database db;
	   db.newRec();
	}
	
	void getProd()
	{
	   Storage_Common::Database db;
	   db.loadRec();
	}
	
	void setProd()
	{
	   Storage_Common::Database db;
	   db.saveRec();
	}
	
	void removeProd()
	{
	   Storage_Common::Database db;
	   db.deleteRec();
	}
};
}
#endif 