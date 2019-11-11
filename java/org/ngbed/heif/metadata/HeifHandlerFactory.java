package org.ngbed.heif.metadata;

import org.ngbed.heif.HeifHandler;
import org.ngbed.heif.boxes.HandlerBox;

import com.drew.metadata.Metadata;

public class HeifHandlerFactory
{
	private static final String HANDLER_PICTURE = "pict";

	private HeifHandler caller;

	public HeifHandlerFactory(HeifHandler caller)
	{
		this.caller = caller;
	}

	public HeifHandler getHandler(HandlerBox box, Metadata metadata)
	{
		String type = box.getHandlerType();
		if (type.equals(HANDLER_PICTURE)) { return new HeifPictureHandler(
				metadata); }
		return caller;
	}
}