package taiwan.toolbox;

import java.lang.Throwable;
import java.lang.RuntimeException;


public class MerkleTreeFromatException extends RuntimeException
{
	public MerkleTreeFromatException(String errorMessage)
	{
		super(errorMessage);
	}

	public MerkleTreeFromatException(Throwable cause)
	{
		super(cause);
	}

	public MerkleTreeFromatException(String errorMessage, Throwable cause)
	{
		super(errorMessage, cause);
	}
}
