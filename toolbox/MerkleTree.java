package taiwan.toolbox;

import java.lang.Math;
import java.util.HashMap;
import java.util.Objects;
import java.math.BigInteger;
import java.util.LinkedList;
import java.lang.StringBuffer;
import java.security.MessageDigest;
import org.apache.commons.lang3.StringUtils;
import java.security.NoSuchAlgorithmException;

import taiwan.toolbox.MerkleTreeFromatException;


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

	// lock
	private final Object treeReading = new Object();

	private final HashMap<String, TreeNode> nodePlace = new HashMap<String, TreeNode>();

	private TreeNode root = null;
	private TreeNode anchor = null;
	private TreeNode branch = null;

	private int treedeep = 0;

	// error message
	private final String parsedFail = "Unknow merkle tree";
	private final String startOrEnd = "Must start with \'{\' and end with \'}\'";

	public MerkleTree() {}

	public MerkleTree(String data)
	{
		this.root = this.createTree(data);

		this.anchor = this.root;
		this.findEmptyNode();
	}

	public boolean append(String checksum)
	{
		synchronized(this.treeReading)
		{
			if(this.root == null) 
			{
				this.treedeep += 1;
//				this.treenodeNum += 1;

				this.root = new TreeNode(checksum);
				this.root.value = checksum;
				this.root.status = false;

				this.anchor = this.root;
				this.nodePlace.put(checksum, this.anchor);

				return true;
			}

			if(this.anchor == this.root)
			{
				TreeNode oldRoot = this.root;

				this.root = null;
				this.root = new TreeNode();
				this.root.left = oldRoot;
				
				if(this.branch != null)
				{
					this.root.right = this.branch;
					this.branch.parent = this.root;

					this.anchor = this.root;
					this.findEmptyNode();

					this.nodePlace.put(checksum, this.anchor);
					this.changeNodeAndBubbling(this.anchor, checksum);

					this.findEmptyNode();

					this.branch = null;
					this.treedeep += 1;

					return true;
				}
			

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
				this.nodePlace.put(checksum, this.anchor);

				while(newNode.parent != this.root)
				{
					newNode = newNode.parent;
//					newNodw.right.value = newNode.left.value;
					newNode.value = this.sha224(newNode.left.value + newNode.left.value);
				}

				this.root.value = sha224(this.root.left.value + this.root.right.value);

				this.findEmptyNode();
			
				this.treedeep += 1;
//				this.treenodeNum += 1;

				return true;
			}

			this.anchor.value = checksum;
			this.anchor.status = false;

			this.nodePlace.put(checksum, this.anchor);

			TreeNode now = this.anchor;
			now = now.parent;

			while(now != null)
			{
				if(now.right.value.length() != 0) now.value = sha224(now.left.value + now.right.value);
				else now.value = sha224(now.left.value + now.left.value);
			
				now = now.parent;	
			}

			this.findEmptyNode();
//			this.treenodeNum += 1;

			return true;
		}
	}

	public TreeNode remove(String checksum)
	{
		synchronized(this.treeReading)
		{
			TreeNode delete = this.nodePlace.remove(checksum);
			if(delete == null) return null;

//			this.treenodeNum -= 1;

			if(this.nodePlace.size() == 0)
			{
				this.root = null;
				this.anchor = null;
				this.branch = null;

				this.treedeep = 0;

				this.nodePlace.clear();

				return delete;
			}

			if(this.nodePlace.size() == (int)Math.pow(2, 31 - Integer.numberOfLeadingZeros(this.nodePlace.size())))
			{
				this.treedeep -= 1;
				this.changeNodeAndBubbling(delete, "");

				this.branch = this.root.right;
				this.branch.parent = null;

				this.anchor = this.root.left;	
				TreeNode move = this.root.right;

				this.findEmptyNode(this.root.left);

				while(this.anchor != this.root.left)
				{
					while(move.right != null && move.left != null)
					{
						if(move.left.value.isEmpty()) move = move.right;
						else move = move.left;
					}

					this.changeNodeAndBubbling(this.anchor, move.value);

					this.changeNodeAndBubbling(move, "");

					move = this.root.right;
					this.findEmptyNode(this.root.left);
				}

				this.root = this.root.left;
				return delete;
			}

			this.changeNodeAndBubbling(delete, "");

			this.anchor = this.root;
			this.findEmptyNode();
		
			return delete;
		}
	}

	private void changeNodeAndBubbling(TreeNode change, String value)
	{
		change.value = value;
		change.status = value.isEmpty();
		change = change.parent;

		while(change != null)
		{
			change.status = (change.left.status || change.right.status);

			if(change.right.value.isEmpty() && change.left.value.isEmpty())
			{
				change.value = "";
				change = change.parent;
				continue;
			}

			if(change.right.value.isEmpty())
			{
				change.value = this.sha224(change.left.value + change.left.value);
				change = change.parent;
				continue;
			}

			if(change.left.value.isEmpty())
			{
				change.value = this.sha224(change.right.value + change.right.value);
				change = change.parent;
				continue;
			}

			change.value = this.sha224(change.left.value + change.right.value);	
			change = change.parent;
		}

	}

	private void findEmptyNode() {this.findEmptyNode(this.root);}
	private void findEmptyNode(TreeNode top)
	{
		if(this.anchor.right == null && this.anchor.left == null)
		{
			this.anchor.status = false;
			this.anchor = this.anchor.parent;
		}

		while(this.anchor.right != null && this.anchor.left != null)
		{
			if(this.anchor == top && !top.left.status && !top.right.status) break;

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

	public boolean compareTo(MerkleTree compare)
	{
		if(this.root == null && compare.size() == 0) return true;
		if(this.root == null || compare.size() == 0) return false;

		if(this.nodePlace.size() != compare.size()) return false;

		return (this.root.value.compareTo(compare.rootNodeValue()) == 0);
	}

	public boolean compareTo(String compare) throws MerkleTreeFromatException
	{
		if(this.root == null || compare == null) return false;

		if(compare.isEmpty())
		{
			if(this.nodePlace.size() == 0) return true;
			return false;
		}

		if(compare.length() == 1) throw new MerkleTreeFromatException(this.parsedFail);

		if(compare.length() == 2)
		{
			if(compare.compareTo("{}") != 0) 
				throw new MerkleTreeFromatException(this.startOrEnd);
			
			if(this.nodePlace.size() == 0) return true;
			return false;
		}

		if(compare.charAt(0) != '{' || compare.charAt(compare.length()-1) != '}')
			throw new MerkleTreeFromatException(this.startOrEnd);

		int counter = StringUtils.countMatches(compare, ',');
		counter += 1;

		if(((int)Math.pow(2, this.treedeep) - 1) != counter) return false;

		return (this.root.value.compareTo(compare.substring(1, compare.indexOf(','))) == 0);
	}

	public int size() 
	{
		synchronized(this.treeReading)
		{
			return this.nodePlace.size();
		}
	}

	public String rootNodeValue()
	{
		synchronized(this.treeReading)
		{
			return this.root.value;
		}
	}

	public LinkedList<LinkedList<String>> toLinkedList()
	{
		if(this.root == null) return null;

		synchronized(this.treeReading)
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
	}

	public String toString()
	{
		if(this.root == null) return "{}";

		synchronized(this.treeReading)
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
	}

    private TreeNode createTree(String input) 
	{
        String[] data = input.split(",");
		data[0] = data[0].substring(1, data[0].length());
		data[data.length-1] = data[data.length-1].substring(0, data[data.length-1].length()-1);

        TreeNode root = null;
        if (data.length == 0) return root;

        root = createTreeHelper(data, root, 0, null);
        return root;
    }

    private TreeNode createTreeHelper(String[] data, TreeNode root, int i, TreeNode parent) 
	{
        if (i < data.length) 
		{
            root = new TreeNode(data[i]);
            root.status = false;
            if (i != 0) root.parent = parent;
            root.left = createTreeHelper(data, root.left, 2 * i + 1, root);
            root.right = createTreeHelper(data, root.right, 2 * i + 2, root);
            if (data[i].isEmpty()) 
			{
                root.status = true;
                TreeNode node = root;
                while (root.parent != null) 
				{
                    root.parent.status = true;
                    root = root.parent;
                }
                root = node;
            }
        }
        return root;
    }
}
