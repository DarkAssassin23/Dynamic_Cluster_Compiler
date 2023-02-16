#include "tree.h"
#ifndef TREE_CPP
#define TREE_CPP

using std::cout;
using std::endl;

template<class ItemType>
void TreeType<ItemType>::operator=(TreeType<ItemType> &original)
{
    if(original == this)
        return; //ignore assigning self to self
    Destroy(root);
    CopyTree(root, original.root);
    cout<<"";
}


template<class ItemType>
void TreeType<ItemType>::CopyTree(TreeNode<ItemType>*& copy, const TreeNode<ItemType>* original)
{
    if(original!=NULL)
    {
        copy = new TreeNode<ItemType>;
        copy->data = original->data;
        copy->left = nullptr;
        copy->right = nullptr;
        CopyTree(copy->left,original->left);
        CopyTree(copy->left,original->left);
    }
}

template<class ItemType>
bool TreeType<ItemType>::DeleteItem(ItemType item)
{
    return Delete(root, item);
}


template<class ItemType>
void TreeType <ItemType>::FindMin(TreeNode <ItemType>* root, ItemType &data)
{
    while(root->left!=0)
        root = root->left;
    data = root->data;
}

template<class ItemType>
ItemType TreeType <ItemType>::FindMax(TreeNode<ItemType>* root)
{
    while(root->right!=nullptr)
        root = root->right;
    return root->data;
}

template<class ItemType>
TreeType<ItemType>::~TreeType()
{
    Destroy(root);
}


template <class ItemType>
void TreeType<ItemType>::Destroy(TreeNode<ItemType>*& root)
{
    if(root!=0)
    {
        Destroy(root->left);
        Destroy(root->right);
        delete root;
    }
}


template <class ItemType>
bool TreeType<ItemType>::BinarySearch(TreeNode<ItemType>* root, ItemType &value)
{
    if(root == 0)
    {
        cout<<value<<" not found\n";
        return false;
    }
    else if(root -> data == value)
    {
        value = root -> data;
        return true;
    }
    else if (root -> data <= value)
        return(BinarySearch(root->right, value));
    else
        return(BinarySearch(root->left, value));
    
}
template <class ItemType>
TreeType<ItemType>::TreeType()
{
    root = 0;
}
template <class ItemType>
bool TreeType<ItemType>::Search(ItemType &value)
{
    return(BinarySearch(root, value));
}
template<class ItemType>
void TreeType<ItemType>::Insert(ItemType item)
{
    InsertItem(root, item);
}

//changed the ItemType from TreeNode

template<class ItemType>
void TreeType<ItemType>::InsertItem(TreeNode<ItemType>*& root, ItemType value)
{
    if(root==nullptr)
    {
        root = new TreeNode<ItemType>;
        root->data = value;
        root->left = nullptr;
        root->right = nullptr;
    }
    else if(root->data>value)
        InsertItem(root->left,value);
    else if(root->data<value)
        InsertItem(root->right,value);
    else
        return;
        
}


template<class ItemType>
void TreeType<ItemType>::Print()
{
    PrintTree(root);
}


template <class ItemType>
void TreeType<ItemType>::PrintTree(TreeNode<ItemType>* root)
{
    if(root!=nullptr)
    {
        PrintTree(root->left);
        cout<<root->data<<" ";
        PrintTree(root->right);
    }
}

template <class ItemType>
bool TreeType<ItemType>::PrintOne(ItemType value)
{
    return (PrintOneNode(root, value));
}

template <class ItemType>
bool TreeType<ItemType>::PrintOneNode(TreeNode<ItemType>* root, ItemType value)
{
    if(root == 0)
    {
        cout<<"Not Found"<<endl;
        return false;
    }
    else if (root-> data == value)
    {
        cout << root->data;
        return false;
    }
    else if (root -> data >= value)
        return PrintOneNode(root->left, value);
    else
        return PrintOneNode(root->right, value);
}




template <class ItemType>
void TreeType<ItemType>::Update(ItemType value, ItemType &newvalue)
{
    UpdateTree(root, value, newvalue);
}

template <class ItemType>
bool TreeType<ItemType>::UpdateTree(TreeNode <ItemType>*& root, ItemType value, ItemType newvalue)
{
    if(root==nullptr)
    {
        cout<<"It was not found\n";
        return false;
    }
    else if(root->data==value)
    {
        root->data = newvalue;
        return true;
    }
    else if(root->data > value)
        return UpdateTree(root->left,value,newvalue);
    else
        return UpdateTree(root->right,value,newvalue);
}

template<class ItemType>
bool TreeType<ItemType>::Delete(TreeNode<ItemType>*& root, ItemType item)
{
    if(root!=nullptr)
    {
        if(root->data<item)
            return Delete(root->right,item);
        else if(root->data>item)
            return Delete(root->left,item);
        else
        {
            if(root->left==nullptr&&root->right==nullptr)
            {
                item = root->data;
                delete root;
                root = nullptr;
                return true;
            }
            else if(root->left==nullptr&&root->right!=nullptr)
            {
                TreeNode<ItemType> *temp = root;
                root = root->right;
                item = temp->data;
                delete temp;
                return true;
            }
            else if(root->left!=nullptr&&root->right==nullptr)
            {
                TreeNode<ItemType> *temp = root;
                root = root->left;
                item = temp->data;
                delete temp;
                return true;
            }
            else
            {
                item = root->data;
                root->data = FindMax(root->left);
                Delete(root->left,root->data);
                return true;
            }
        }
    }
    return false;
}
#endif



