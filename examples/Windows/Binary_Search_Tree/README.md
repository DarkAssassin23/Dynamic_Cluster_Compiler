# Binary Search Tree Example Using a Unix Based Cluster

About
---------
This is a Binary Search Tree implementation which uses multiple source files and
directories and uses a couple of nodes running Windows to compile it.

It's <code>distributed_makefile</code>, <code>inventory.ini</code>,
and <code>mnt.cfg</code> files can be found in the <code>cfg/</code>
folder. 

_________
Compiling Setup
---------
In order to compile this example, ensure that you place this folder
in a centraly located place which can be accessed by SSH by your nodes.


Next, you will need to modify the <code>inventory.ini</code> and 
<code>mnt.cfg</code> files (located in the <code>cfg/</code> folder).
If you are unsure how Ansible inventory files work you can view the 
documentation [here](https://docs.ansible.com/ansible/latest/inventory_guide/intro_inventory.html),
but put simply, make sure the <code>ansible_user</code> and <code>ansible_password</code>
is set to the user you log into each of your Windows nodes with via Ansible, 
and the IP addresses for your worker nodes and master node are correct.

Noteable changes that need to be made to the <code>mnt.cfg</code> file are:

 * Changing <code>remoteHost</code> to be the IP address of the device where
 your source code is located
 * Changing <code>remoteUser</code> to be the user you use to login to the device
 where your source code is, and your individual nodes have copied their SSH Keys to
 for automatic authentication.
 * Changing <code>remoteDir</code> to be the path to the source code on the device where
 your source code is.

_________

Compiling
---------
Once you have made the neccesary changes to your config files. On your machine that will
be running the compiler, naviate to the shared location where the <code>distributed.cfg</code>
file is and run the <code>dcc.py</code> program. 

________
Compiling Examples
--------
This example assumes you have mounted your centrally located device at <code>/mnt/Code</code> 
on your local machine running the compiler, and have the location of <code>dcc.py</code> as part
off your $PATH
```bash
cd /mnt/Code/Distributed-Compiler/examples/Binary_Search_Tree
python3 dcc.py
```

This example assumes you have mounted your centrally located device at <code>~/shared</code> 
on your local machine running the compiler, and have <code>dcc.py</code> with the alias of
<code>dcc</code> or it is saved in your <code>/usr/local/bin</code> directory as <code>dcc</code>. 
Also, in the latter case, it is assumed the <code>deploy.yml</code> file is also located 
in <code>/usr/local/bin</code> as well.
```bash
dcc ~/shared/Distributed-Compiler/examples/Binary_Search_Tree
```