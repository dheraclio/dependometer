package generics.four;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

public class AbstractListener<R extends Object> extends ArrayList<R> implements IListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	public final ActionListener updateEffectivityEnd(final Object objectToSave, final int logId, final Calendar effBegin)
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
			}
		};
	}
}
