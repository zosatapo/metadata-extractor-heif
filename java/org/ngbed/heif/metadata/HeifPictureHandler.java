package org.ngbed.heif.metadata;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.ngbed.heif.HeifHandler;
import org.ngbed.heif.boxes.AuxiliaryTypeProperty;
import org.ngbed.heif.boxes.Box;
import org.ngbed.heif.boxes.ColourInformationBox;
import org.ngbed.heif.boxes.ImageRotationBox;
import org.ngbed.heif.boxes.ImageSpatialExtentsProperty;
import org.ngbed.heif.boxes.ItemInfoBox;
import org.ngbed.heif.boxes.ItemInfoBox.ItemInfoEntry;
import org.ngbed.heif.boxes.ItemLocationBox;
import org.ngbed.heif.boxes.ItemLocationBox.Extent;
import org.ngbed.heif.boxes.ItemLocationBox.ItemLocation;
import org.ngbed.heif.boxes.ItemProtectionBox;
import org.ngbed.heif.boxes.PixelInformationBox;
import org.ngbed.heif.boxes.PrimaryItemBox;
import org.ngbed.heif.io.RandomAccessReader;

import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.heif.HeifBoxTypes;
import com.drew.metadata.heif.HeifContainerTypes;

public class HeifPictureHandler extends HeifHandler
{
	ItemProtectionBox itemProtectionBox;
	PrimaryItemBox primaryItemBox;
	ItemInfoBox itemInfoBox;
	ItemLocationBox itemLocationBox;

	public HeifPictureHandler(Metadata metadata) {
		super(metadata);

		itemProtectionBox = null;
		primaryItemBox = null;
		itemInfoBox = null;
		itemLocationBox = null;
	}

	@Override
	protected boolean shouldAcceptContainer(Box box)
	{
		return box.type.equals(HeifContainerTypes.BOX_IMAGE_PROPERTY)
				|| box.type.equals(HeifContainerTypes.BOX_ITEM_PROPERTY);
	}

	@Override
	protected void processContainer(int depth, Box box, RandomAccessReader reader) throws IOException
	{
		reader.skip(box.countBytesUnread());
	}

	@Override
	protected boolean shouldAcceptBox(Box box)
	{
		List<String> boxes = Arrays.asList(HeifBoxTypes.BOX_ITEM_PROTECTION, HeifBoxTypes.BOX_PRIMARY_ITEM,
				HeifBoxTypes.BOX_ITEM_INFO, HeifBoxTypes.BOX_ITEM_LOCATION, HeifBoxTypes.BOX_IMAGE_SPATIAL_EXTENTS,
				HeifBoxTypes.BOX_AUXILIARY_TYPE_PROPERTY, HeifBoxTypes.BOX_IMAGE_ROTATION, HeifBoxTypes.BOX_COLOUR_INFO,
				HeifBoxTypes.BOX_PIXEL_INFORMATION);

		return boxes.contains(box.type);
	}

	@Override
	protected void processBox(int depth, Box box, RandomAccessReader reader) throws IOException
	{
		if (box.type.equals(HeifBoxTypes.BOX_ITEM_PROTECTION)) {
			itemProtectionBox = new ItemProtectionBox(reader, box);
		}
		else if (box.type.equals(HeifBoxTypes.BOX_PRIMARY_ITEM)) {
			primaryItemBox = new PrimaryItemBox(reader, box);
		}
		else if (box.type.equals(HeifBoxTypes.BOX_ITEM_INFO)) {
			itemInfoBox = new ItemInfoBox(reader, box);
//			System.out.println(HeifReader.ZERO_PADDINGS[depth] + itemInfoBox);
			itemInfoBox.addMetadata(directory);
		}
		else if (box.type.equals(HeifBoxTypes.BOX_ITEM_LOCATION)) {
			itemLocationBox = new ItemLocationBox(reader, box);
//			System.out.println(HeifReader.ZERO_PADDINGS[depth] + itemLocationBox);
		}
		else if (box.type.equals(HeifBoxTypes.BOX_IMAGE_SPATIAL_EXTENTS)) {
			ImageSpatialExtentsProperty imageSpatialExtentsProperty = new ImageSpatialExtentsProperty(reader, box);
			imageSpatialExtentsProperty.addMetadata(directory);
		}
		else if (box.type.equals(HeifBoxTypes.BOX_AUXILIARY_TYPE_PROPERTY)) {
			AuxiliaryTypeProperty auxiliaryTypeProperty = new AuxiliaryTypeProperty(reader, box);
			auxiliaryTypeProperty.addMetadata(directory);
		}
		else if (box.type.equals(HeifBoxTypes.BOX_IMAGE_ROTATION)) {
			ImageRotationBox imageRotationBox = new ImageRotationBox(reader, box);
			imageRotationBox.addMetadata(directory);
		}
		else if (box.type.equals(HeifBoxTypes.BOX_COLOUR_INFO)) {
			ColourInformationBox colourInformationBox = new ColourInformationBox(reader, box, metadata);
			colourInformationBox.addMetadata(directory);
		}
		else if (box.type.equals(HeifBoxTypes.BOX_PIXEL_INFORMATION)) {
			PixelInformationBox pixelInformationBox = new PixelInformationBox(reader, box);
			pixelInformationBox.addMetadata(directory);
		}
	}

	@Override
	protected void processCompleted(int depth, RandomAccessReader reader) throws IOException
	{
		readExifMetadata(depth, reader);
	}

	private void readExifMetadata(int depth, RandomAccessReader reader) throws IOException
	{
		ItemInfoEntry exifEntry = itemInfoBox.getExifItemInfoEntry();
		if (exifEntry == null) { return; }

		ItemLocation location = itemLocationBox.getLocation((int) exifEntry.itemID);
		Extent extent = location.extents.get(0);
		long position = reader.getPosition();
		reader.seek(extent.offset);

		byte[] PREAMBLE = { 0, 0, 0, 6 };
		int EXIF_SEGMENT_PREAMBLE_OFFSET = PREAMBLE.length + ExifReader.JPEG_SEGMENT_PREAMBLE.length();

		ByteArrayReader exifReader = new ByteArrayReader(reader.getBytes(extent.length));
		new com.drew.metadata.exif.ExifReader().extract(exifReader, this.metadata, EXIF_SEGMENT_PREAMBLE_OFFSET, null);

		reader.seek(position);

	}
}
