CZD_Android_Lib
===============

Android库，包含一些常用的函数，自己做了一些比较常用的View，实现了瀑布流，并且集成了Github中几个比较热门的库，但是为了package名的统一和实现一些功能，做了一些修改，下面一一说明。

包含的package包括:
- czd.lib.application
- czd.lib.data
- czd.lib.encode
- czd.lib.io
- czd.lib.location (仅包含基本定位功能)
- czd.lib.network 源自**[android-async-http](https://github.com/loopj/android-async-http)**
- czd.lib.view
- czd.lib.view.abslistview.pull 下拉刷新的ListView和GridView
- czd.lib.view.abslistview.stable 定长的ListView和GridView
- czd.lib.view.dropdown 下拉菜单
- czd.lib.view.gestureimageview 源自**[gesture-imageview](https://github.com/jasonpolites/gesture-imageview)**
- czd.lib.view.scrollview.observable 绑定onScroll(),onTop(),outTop(),onBottom(),outBottom(),onStop()事件响应
- czd.lib.view.progress 进度展示的控件
- czd.lib.view.scrollview.observerble 提供滚动反馈的ScrollView
- czd.lib.view.scrollview.pull 下拉刷新的ScrollView
- czd.lib.view.slidingmenu 源自**[sliding-menu](https://github.com/jfeinstein10/SlidingMenu)
- czd.lib.view.smartimageview 源自**[android-smart-image-view](https://github.com/loopj/android-smart-image-view)**，加入了一个SmartGestureImageView，接受手势控制
- czd.lib.view.tab 
- czd.lib.view.viewpagerindicator
- czd.lib.view.waterfall 瀑布流组件，基本上不会有卡顿现象，前提是implement WaterfallItem的**recycle()**要做好。包含了BasicWaterfallContainer（普通瀑布流）和PullWaterfallContainer（支持下拉刷新的瀑布流）

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
- tabbutton
- vpi(viewpageindicator)

===============
其他具体信息详见代码

部分代码未经测试，有任何问题，欢迎邮件联系傻东(njutczd+gmail.com)
