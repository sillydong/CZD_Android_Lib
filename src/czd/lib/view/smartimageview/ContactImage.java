package czd.lib.view.smartimageview;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.InputStream;

public class ContactImage implements SmartImage {
	private long contactId;

	public ContactImage(long contactId) {
		this.contactId = contactId;
	}

	@Override
	public void getBitmap(Context context, AbsSmartView.ViewHandler handler) {
		handler.sendEmptyMessage(AbsSmartView.MSG_START);
		try
		{
			ContentResolver contentResolver = context.getContentResolver();

			if (ContactsContract.Contacts.CONTENT_URI != null)
			{
				Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
				if (uri != null)
				{
					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
					if (input != null)
					{
						BitmapFactory.Options o = new BitmapFactory.Options();
						o.inPurgeable = true;
						o.inInputShareable = true;
						handler.sendMessage(handler.obtainMessage(AbsSmartView.MSG_SUCCESS, BitmapFactory.decodeStream(input, new Rect(0, 0, 0, 0), o)));
					}
					else
						handler.sendEmptyMessage(AbsSmartView.MSG_FAILURE);
				}
				else
					handler.sendEmptyMessage(AbsSmartView.MSG_FAILURE);
			}
			else
				handler.sendEmptyMessage(AbsSmartView.MSG_FAILURE);
		} catch (Exception e)
		{
			handler.sendEmptyMessage(AbsSmartView.MSG_FAILURE);
		}
		handler.sendEmptyMessage(AbsSmartView.MSG_FINISH);
	}


	@Override
	public void recycle() {

	}

	@Override
	public String toString() {
		return super.toString();
	}
}
