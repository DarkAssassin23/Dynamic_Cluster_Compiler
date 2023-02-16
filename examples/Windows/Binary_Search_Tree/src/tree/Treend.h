#ifndef TREEND_H
#define TREEND_H
template <class NODETYPE>
class TreeNode{

public:
	TreeNode(const NODETYPE &value);
	TreeNode(){right=0;left=0;}
	TreeNode<NODETYPE> *left, *right;
	NODETYPE data;

	
};
template<class NODETYPE>
TreeNode<NODETYPE>::TreeNode(const NODETYPE &value)
{
	left = 0;
	right = 0;
	data = value;
}

	
#endif	
