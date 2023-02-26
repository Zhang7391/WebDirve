package taiwan.toolbox;

import java.lang.Math;
import java.math.BigInteger;
import java.util.LinkedList;
import java.lang.StringBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MerkleTree
{
	private final static boolean DEBUG = false;

	private class TreeNode
	{
		public String value = null;
		public boolean status = true;

		public TreeNode left = null;
		public TreeNode right = null;
		public TreeNode parent = null;

		public TreeNode() {}

		public TreeNode(String value) {this.value = value;}

		public TreeNode(String value, TreeNode parent)
		{
			this.value = value;
			this.parent = parent;
		}
	}

	private TreeNode root = null;
	private TreeNode anchor = null;

	private int treedeep = 0;
	private int treenodeNum = 0;

	public MerkleTree() {}

	public boolean append(String checksum)
	{
		if(this.root == null) 
		{
			if(this.DEBUG) System.out.println("Init this tree"); 

			this.treedeep += 1;
			this.treenodeNum += 1;

			this.root = new TreeNode(checksum);
			this.root.value = checksum;
			this.root.status = false;

			this.anchor = this.root;

			return true;
		}

		if(this.anchor == this.root)
		{
			if(this.DEBUG) System.out.println("Add this tree deep"); 

			TreeNode oldRoot = this.root;
			
			this.root = null;
			this.root = new TreeNode();
			this.root.left = oldRoot;

			int deep = 1;
			oldRoot.status = false;
			oldRoot.parent = this.root;

			TreeNode newNode = new TreeNode("", this.root);
			this.root.right = newNode;

			LinkedList<TreeNode> nodes = new LinkedList<TreeNode>();
			nodes.addLast(newNode);

			while(this.treedeep > deep)
			{
				int counter = nodes.size();

				while(counter != 0)
				{
					TreeNode now = nodes.pollFirst();
					now.left = new TreeNode("", now);
					now.right = new TreeNode("", now);

					nodes.addLast(now.left);
					nodes.addLast(now.right);

					counter -= 1;
				}

				deep += 1;
			}
			nodes.clear();

			while(newNode.left != null) newNode = newNode.left;
			newNode.value = checksum;
			newNode.status = false;

			this.anchor = newNode;

			while(newNode.parent != this.root)
			{
				newNode = newNode.parent;
//				newNodw.right.value = newNode.left.value;
				newNode.value = this.sha224(newNode.left.value + newNode.left.value);
			}

			this.root.value = sha224(this.root.left.value + this.root.right.value);

			this.findEmptyNode();
			
			this.treedeep += 1;
			this.treenodeNum += 1;

			return true;
		}

		if(this.DEBUG) System.out.println("Fill blank"); 

		this.anchor.value = checksum;
		this.anchor.status = false;

		TreeNode now = this.anchor;
		now = now.parent;

		while(now != null)
		{
			if(this.DEBUG) System.out.println("now value: " + now.value);

			if(now.right.value.length() != 0) now.value = sha224(now.left.value + now.right.value);
			else now.value = sha224(now.left.value + now.left.value);
			
			now = now.parent;
			
		}
		if(this.DEBUG) System.out.println("now value: " + now);

		this.findEmptyNode();
		this.treenodeNum += 1;

		return true;
	}

	private void findEmptyNode()
	{
		if(this.DEBUG) 
		{
			System.out.println("finding empty node...");
			System.out.println("anchor value: " + this.anchor.value);
		}

		if(this.anchor.right == null && this.anchor.left == null)
		{
			this.anchor.status = false;
			this.anchor = this.anchor.parent;
		}

		while(this.anchor.right != null && this.anchor.left != null)
		{
			if(this.DEBUG) System.out.println("anchor value: " + this.anchor.value);
			
			if(this.anchor == this.root) 
			{
				if(this.DEBUG) System.out.println("Match root");
				break;
			}

			if(this.anchor.left.status)
			{
				this.anchor = this.anchor.left;
				continue;
			}

			if(this.anchor.right.status)
			{
				this.anchor = this.anchor.right;
				continue;
			}

			this.anchor.status = false;
			this.anchor = this.anchor.parent;
		}

		if(this.DEBUG) System.out.println("anchor value: " + this.anchor.value);
	}

	public boolean remove(String checksum)
	{
		return true;
	}

	private String sha224(String input)
	{
		return input;

/*		try
		{
			MessageDigest sha224 = MessageDigest.getInstance("SHA-224");

			byte[] passwordDigest = sha224.digest(input.getBytes());

			BigInteger decimal = new BigInteger(1, passwordDigest);

			String hashtext = decimal.toString(16);

			while(hashtext.length() < 32) hashtext = "0" + hashtext;

			return hashtext;
		}
		catch(NoSuchAlgorithmException e) {throw new RuntimeException(e);}*/
	}

	public int size() {return this.treenodeNum;}

	public LinkedList<LinkedList<String>> toLinkedList()
	{
		LinkedList<LinkedList<String>> result = new LinkedList<LinkedList<String>>();

		LinkedList<TreeNode> nodes = new LinkedList<TreeNode>();
		nodes.addLast(this.root);

		while(!nodes.isEmpty())
		{
			int counter = nodes.size();
			LinkedList<String> temp = new LinkedList<String>();

			while(counter != 0)
			{
				TreeNode node = nodes.pollFirst();

				temp.addLast(node.value);

				if(node.left != null) nodes.addLast(node.left);
				if(node.right != null) nodes.addLast(node.right);

				counter -= 1;
			}

			result.addLast(temp);
		}

		return result; 
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer("{");

		LinkedList<TreeNode> nodes = new LinkedList<TreeNode>();
		nodes.addLast(this.root);

		while(!nodes.isEmpty())
		{
			int counter = nodes.size();

			while(counter != 0)
			{
				TreeNode node = nodes.pollFirst();

				result.append(node.value + ",");

				if(node.left != null) nodes.addLast(node.left);
				if(node.right != null) nodes.addLast(node.right);

				counter -= 1;
			}
		}

		return new String(result.deleteCharAt(result.length()-1).append('}'));
	}
    
    public static TreeNode createTree(String input) {
        input = input.substring(1, input.length()-1);
        String[] data = input.split(",");

        TreeNode root = null;
        if (data.length == 0) return root;

        root = createTreeHelper(data, root, 0, null);
        return root;
    }
    
    public static TreeNode createTreeHelper(String[] data, TreeNode root, int i, TreeNode parent) {
        if (i < data.length) {
            root = new TreeNode(data[i]);
            root.status = false;
            if (i != 0) root.parent = parent;
            root.left = createTreeHelper(data, root.left, 2 * i + 1, root);
            root.right = createTreeHelper(data, root.right, 2 * i + 2, root);
            if (data[i] == "") {
                root.status = true;
                TreeNode node = root;
                while (root.parent != null) {
                    root.parent.status = true;
                    root = root.parent;
                }
                root = node;
            }
        }
        return root;
    }
}
