package org.ngbed.heif;

import java.io.IOException;

import org.ngbed.heif.boxes.Box;
import org.ngbed.heif.io.RandomAccessReader;

import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifDirectory;

public abstract class HeifHandler
{
	protected Metadata metadata;
	protected HeifDirectory directory;

	public HeifHandler(Metadata metadata)
	{
		this.metadata = metadata;
		this.directory = new HeifDirectory();
		this.metadata.addDirectory(directory);
	}

	protected abstract boolean shouldAcceptBox(Box box);

	protected abstract boolean shouldAcceptContainer(Box box);

	protected abstract void processBox(int depth, Box box, RandomAccessReader reader) throws IOException;

	/**
	 * There is potential for a box to both contain other boxes and contain
	 * information, so this method will handle those occurences.
	 */
	protected abstract void processContainer(int depth, Box box, RandomAccessReader reader) throws IOException;

	protected abstract void processCompleted(int depth, RandomAccessReader reader) throws IOException;
}
