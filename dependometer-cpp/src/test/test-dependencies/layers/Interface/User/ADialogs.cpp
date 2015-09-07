#ifndef INTERFACE_USER_ADMIN
#define INTERFACE_USER_ADMIN

#include "Console.cpp"
#include "Database.cpp"

namespace Interface_User
{
class ADialogs
{
public:

	void newUser()
	{
	    Storage_Common::Database db;
	    Interface_Common::Console cons;
	    cons.in();
	    db.newRec();
	    
	}
};
}
#endif 