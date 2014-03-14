CZD_Android_Lib
===============

Android库，包含一些常用的函数，自己做了一些比较常用的View，比如瀑布流的View，并且集成了Github中几个比较热门的库，但是为了package名的统一和实现一些功能，做了一些修改，下面一一说明。

【更新记录】
- 2014-03-14
	重写SmartImageView，使用android-async-http的SyncHttpClient来加载图片，启用cookie，以方便加载验证码图片。支持视频缩略图，联系人图和网络图片。  
	修改android-async-http的重定向设置部分代码，避免因URL中出现空格引起的URISyntaxException  
	增加RangeFileAsyncHttpResponseHandler，支持对文件进行断点续传  
	优化BaseArrayAdapter和BaseJsonAdapter，添加删除元素方法  
	增加晃动动画的xml  
- 2013-01-16  
	将android-async-http退回上一版本，最新版本无关内容过多。与官方库有不同，有更新，修改了可能造成"Invalid cookie header ..."的错误   
	加入了Android-Bootstrap控件，好看的Button和EditText，字体文件在assets中  
	完善了瀑布流的加载，不会因滑动过快造成不加载当前显示内容  

【功能说明】
- czd.lib.adapter 继承自BaseAdapter，包含ArrayList和JsonArray内容的两种adapter
- czd.lib.application 包含ActivityUtil,APKUtil,ApplicationUtil,DeviceUtil,Timer
- czd.lib.cache 将内容缓存到文件或内存，包含BitmapCache,FileCache,JsonCache,MemCache,StringCache
- czd.lib.data 包含CookieUtil,DateUtil,FileUtil,ImageUtil,JSONUtil,MathUtil,PreferenceUtil,SQLiteUtil,StreamUtil,StringUtil,ValidateUtil,XMLUtil
- czd.lib.encode Base64,Blowfish,CRC32,Hex,MD5,Reversible,Rijndael
- czd.lib.location 仅包含基本定位功能和简单的查询
- czd.lib.network 源自**[android-async-http](https://github.com/loopj/android-async-http)**
- czd.lib.view.abslistview.pull 下拉刷新的ListView和GridView
- czd.lib.view.abslistview.recyclable 对元素执行回收操作
- czd.lib.view.abslistview.stable 定长的ListView和GridView
- czd.lib.view.bootstrap 源自**[Android-Bootstrap](https://github.com/Bearded-Hen/Android-Bootstrap)**
- czd.lib.view.dropdown 下拉菜单
- czd.lib.view.gestureimageview 源自**[gesture-imageview](https://github.com/jasonpolites/gesture-imageview)**
- czd.lib.view.progress 三种展示进度的view
- czd.lib.view.scrollview.observable 绑定onScroll(),onTop(),outTop(),onBottom(),outBottom(),onStop()事件响应
- czd.lib.view.scrollview.pull 下拉刷新的scrollview
- czd.lib.view.slidingmenu 源自**[sliding-menu](https://github.com/jfeinstein10/SlidingMenu)**
- czd.lib.view.smartimageview 源自**[android-smart-image-view](https://github.com/loopj/android-smart-image-view)**，加入了一个SmartGestureImageView，接受手势控制
- czd.lib.view.viewpagerindicator 源自**[Android-ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator)**
- czd.lib.view.waterfall 瀑布流组件，基本上不会有卡顿现象，前提是implement WaterfallItem的**recycle()**要做好。包含了BasicWaterfallContainer（普通瀑布流）和PullWaterfallContainer（支持下拉刷新的瀑布流）
- czd.lib.view.ToastUtil
- czd.lib.view.ViewUtil

在编辑布局时可用的控件包括:
- czd.lib.view.PullListView
- czd.lib.view.smartimageview.SmartImageView
- czd.lib.view.smartimageview.GestureImageView **包含自定义的attr**
- czd.lib.view.smartimageview.SmartGestureImageView 其实就在在GestureImageView前面一层ProgressBar，并且用SmartImageView的逻辑来加载它
- czd.lib.view.StableGridView
- czd.lib.view.StableListView
- czd.lib.view.TabButton **包含自定义的attr**
- czd.lib.view.ObservableScrollView
- czd.lib.view.BasicWaterfallContainer
- czd.lib.view.PullWaterfallContainer
- ...

其他控件包括:
- czd.lib.view.dropdown.DropdownContainer 通过new来创建
- czd.lib.view.waterfall.WaterfallItem 通过implement创建

Animation包括:
- fadein
- pop_in/pop_out
- rotate
- shift_bottom_in/shift_bottom_out
- shift_left_in/shift_left_out
- shift_right_in/shift_right_out
- shift_top_in/shift_right_out

布局文件包括:
- common_loading_frame
- common_loading_text
- common_pull_header
- common_toast_layout

Attribute包括:
- pullview
- slidingmenu
- vpi(viewpageindicator)

===============
其他具体信息详见代码  

部分代码未经测试，有任何问题，欢迎邮件联系傻东(njutczd+gmail.com)  
