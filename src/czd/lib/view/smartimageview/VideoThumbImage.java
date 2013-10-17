/*
 * Copyright (c) 2013 Chen.Zhidong <njutczd@gmail.com>
 * http://sillydong.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package czd.lib.view.smartimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-10-17
 * Time: 上午9:57
 */
public class VideoThumbImage implements SmartImage{
	private int videoid;
	private int thumbnailkind;

	public VideoThumbImage(int videoid,int thumbnailkind){
		this.videoid=videoid;
		this.thumbnailkind=thumbnailkind;
	}

	@Override
	public Bitmap getBitmap(Context context) {
		return MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),videoid,thumbnailkind,null);
	}
}
