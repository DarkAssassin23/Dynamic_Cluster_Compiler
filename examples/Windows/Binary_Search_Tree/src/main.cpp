#include <iostream>
#include <cstdlib>
#include <ctime>
#include <climits>

#include "tree.h"
using std::cout;
using std::endl;

int main (void)
{

	TreeType <int> tree;
    srand (time(NULL));
	cout<<"\nINSERTING\n";
	for (int i=0;i<15;i++)
	{
		int number=rand()%100;
		cout<<"Atttempting to insert "<<number<<endl;
		tree.Insert(number);
	}
	cout<<"\nPRINT ALL\n";
	tree.Print();
	cout<<endl;

    int array[16]={40,23,1,5,28,84,81,80,97,90,47,56,70,71,79,90};	
	for(int i=0;i<16;i++)
	{
		if (tree.DeleteItem(array[i]))
			cout<<"Attempting to delete "<<array[i]<<endl;
	}
	
	cout<<"\nPRINT ALL\n";
	tree.Print();
	cout<<endl;

	return 0;
}
