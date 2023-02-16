#ifndef TREE
#define TREE
#include <fstream>
#include <iostream>

#include "Treend.h"

template<class ItemType>
class TreeType
{
    
public:
    TreeType();
    TreeType (const TreeType &original){CopyTree(root, original.root);}
    void operator=(TreeType &orginalTree);
    bool Search(ItemType &value);
    void Insert(ItemType value);
    void Print() ;
    ~TreeType();
    bool isEmpty() {return root==0;}
    bool DeleteItem(ItemType value);
    bool PrintOne(ItemType value);
    void Update(ItemType value, ItemType &newvalue);
    
    
private:
    TreeNode<ItemType>* root;
    void CopyTree(TreeNode<ItemType>*& copy, const TreeNode<ItemType>* original);
    
    bool BinarySearch(TreeNode<ItemType>* root, ItemType &value);
    void InsertItem(TreeNode<ItemType>*&  root, ItemType value);
    void PrintTree(TreeNode<ItemType>* root);
    void Destroy(TreeNode<ItemType>*& root);
    bool PrintOneNode(TreeNode<ItemType>* root, ItemType value);
    bool UpdateTree(TreeNode<ItemType>*& root, ItemType value, ItemType newvalue);
    bool Delete(TreeNode<ItemType>*& root, ItemType);
    void FindMin(TreeNode<ItemType>* root, ItemType &data);
    ItemType FindMax(TreeNode<ItemType>* root);
    
};

#include "tree.cpp"
#endif
