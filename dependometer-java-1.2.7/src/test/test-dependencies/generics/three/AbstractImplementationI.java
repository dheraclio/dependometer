package generics.three;

/**
 * Abstract base class for implementations of an interface
 *
 * @param <T>
 */
public abstract class AbstractImplementationI<T>
{
	public AbstractImplementationII<T> getImplementation()
	{
		@SuppressWarnings("unchecked")
		AbstractImplementationII<T> implementation = null;

		if (implementation == null)
		{
		}

		return implementation;
	}
}
