package taiwan.toolbox;

import java.lang.Throwable;
import java.lang.RuntimeException;


public class MerkleTreeFormatException extends RuntimeException
{
	public MerkleTreeFormatException(String errorMessage)
	{
		super(errorMessage);
	}

	public MerkleTreeFormatException(Throwable cause)
	{
		super(cause);
	}

	public MerkleTreeFormatException(String errorMessage, Throwable cause)
	{
		super(errorMessage, cause);
	}
}
