/* assert example */
#include <assert.h>

using namespace std;

int main ()
{
  int a[1000];
  int i;
  
  for (i = 0; i < 1000; ++i) 
    a[i] = 1000 - i - 1;

  for (i = 0; i < 1000; ++i) 
    assert (a[i] == i);
  
  return 0;

}
