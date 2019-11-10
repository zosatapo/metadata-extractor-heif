package org.ngbed.heif.metadata;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.ngbed.heif.HeifHandler;
import org.ngbed.heif.HeifReader;
import org.ngbed.heif.boxes.Box;
import org.ngbed.heif.boxes.FileTypeBox;
import org.ngbed.heif.boxes.FullBox;
import org.ngbed.heif.boxes.HandlerBox;
import org.ngbed.heif.io.RandomAccessReader;

import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifBoxTypes;
import com.drew.metadata.heif.HeifContainerTypes;

public class HeifBoxHandler extends HeifHandler
{
	private HeifHandlerFactory handlerFactory = new HeifHandlerFactory(this);

	public HeifBoxHandler(Metadata metadata)
	{
		super(metadata);
	}

	@Override
	public boolean shouldAcceptContainer( Box box)
	{
		List<String> boxes = Arrays.asList(HeifContainerTypes.BOX_METADATA);
		return boxes.contains(box.type);
	}


	@Override
	public void processContainer(int depth, Box box,  RandomAccessReader reader) throws IOException
	{
		if (box.type.equals(HeifContainerTypes.BOX_METADATA))
		{			
			FullBox metaBox = new FullBox(reader, box);	
//			System.out.println(HeifReader.ZERO_PADDINGS[depth] + "box[processContainer] -->" + metaBox);
			HandlerBox handlerBox = new HandlerBox(reader, new Box(reader));
//			System.out.println(HeifReader.ZERO_PADDINGS[depth] + "box[processContainer] -->" + handlerBox);
			HeifHandler handler = handlerFactory.getHandler(handlerBox, this.metadata);
			HeifReader.processBoxes(++depth,reader, metaBox.offset + metaBox.size, handler);
		}
	}

	

	@Override
	public boolean shouldAcceptBox( Box box)
	{
		List<String> boxes = Arrays.asList(HeifBoxTypes.BOX_FILE_TYPE, HeifBoxTypes.BOX_HANDLER, HeifBoxTypes.BOX_HVC1);
		return boxes.contains(box.type);
	}

	
	@Override
	public void processBox(int depth, Box box,  RandomAccessReader reader) throws IOException
	{
		if (box.type.equals(HeifBoxTypes.BOX_FILE_TYPE))
		{
			processFileType(reader, box);
		}
	}
	
	@Override
	public  void processCompleted(int depth,  RandomAccessReader reader)
			throws IOException{}
	
	private void processFileType( RandomAccessReader reader,  Box box) throws IOException
	{
		FileTypeBox fileTypeBox = new FileTypeBox(reader, box);
		fileTypeBox.addMetadata(directory);
		if (!fileTypeBox.getCompatibleBrands().contains("mif1"))
		{
			directory.addError("File Type Box does not contain required brand, mif1");
		}
	}
}
