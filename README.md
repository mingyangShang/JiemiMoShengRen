# JiemiMoShengRen
解密陌生人，陌生人交友app

**<font size='+1'>第三方框架</font>**
 - UIL：管理图片的加载，显示，下载和回收
 - Volley：网络请求
 - 环信：即时通讯
 - BaiduMap：百度地图，地图显示和处理
 - Base-Adapter-Helper:简化Adapter的书写，减少Adapter编写的重复冗余代码
 - PinYin4j：处理中文和拼音之间的转换
 - Gson：处理json字符串解析，使用Model类对象化
 

**<font size='+1'>遇到的问题</font>**
 - PagerSlidingStrip和BadgeView结合不能显示提示小红点：badgeview实现的原理是在设置targetView时从targetView的布局中删除掉targetView，然后创建一个FrameLayout，将target和自身（也就是显示小红点的TextView）添加进去，所以这就要求在调用BadgeView的setTargetView方法时一定要保证此时要设置的targetView已经存在于一个父布局中，这样才能够正确地将提示小红点添加上去，解决该问题的话就是重写PagerSlidingStrip中的addTab(position,tab)方法，首先调用父类的方法，然后创建BadgeView并设置tab为targetView，具体使用见`WeiXinPagerSlidingStrip`
 - FragmentPagerAdapter和fragment结合造成在activity中调用fragment方法报空指针异常：FragmentPagerAdapter在其内部封装了平常用FragmentTransaction进行的一系列对Fragment添加、移除等操作，这些操作会被它的父类PagerAdapter在其回调方法中调用，现在还不清楚PagerAdapter在其内部究竟是如何工作的，但可以验证Fragment的onAttach函数是发生在onResume之后的，也就是说在onResume之后才会开始Fragment的生命周期（首先执行onAttach函数），所以如果在Activity的onResume之前调用如改变Fragment视图的方法时会报NullPointerException，因为此时Fragment的onCreateView还没有执行，是没有view和Fragment绑定的，具体内部实现见`PagerAdapter`和`FragmentPagerAdapter`，值得读一下源码，另外一点是FragmentPagerAdapter会在内存中一直存储着所创建的Fragment，即使Fragment的内容可能被回收，所以FragmentPagerAdapter只适用于创建比较少而且不太复杂的Fragment，否则会占用大量的内存，而这种情况下最好使用FragmentStatePagerAdapter，见`FragmentStatePagerAdapter`
 - 在xml中指定view的属性android:onClick="func"报`Can't find func(View)……`错误：在xml里为view指定处理函数的话，就要相应地在Controller（更多时候就是指Activity）里创建和该属性指定的名字相同的方法，并且带上一个View的参数，还有一点不能忽略的是，要把这个方法声明成public的，也就是说要让所有地方都能访问这个方法，举个例子
xml中:
 ```
 <TextView
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   android:onClick="back"/>
 ```
java代码中:
 ```
 Java
 public void back(View v){}
 ```
 很小的一个知识点，但是以前没有搞懂，出错了也没找出来是为什么，现在还不能说彻底明白了，因为还不知道xml中onClick是怎样和java代  码中的handler函数绑定的，`View`的源码还是得好好看看
 - 登录效果：有时间再把效果图传上来，实现这种效果的话是属性动画再加上自定义view包装基本的Button，xml中的shape是如何转换成java代码的，自定义属性时如何使用系统自带的并且在代码中获得，在onMeasure中获得的高度和宽度不对
 - 创建JSONObject的封装类EasyJSONObject时出错：首先从JSONObject的getJSONObject方法中拿到的必须要是JSONObject才行，而且不能是符合json格式的字符串，另外封装类一定要重写toString方法，返回jsonObject的toString，现在还不清楚到底是在哪用到了这些，见`EasyJsonObject`,`JSONObject`
 - 百度地图使用：注意从百度地图拿到的坐标要重新显示到地图上还要经过一个转换，方法见`BaiduMapActivity.convert(Latlng)`，这点比较坑，自家拿到的东西还得让开发者自己去做转换，太不人性化了；在添加完覆盖物标记后，要记得更新地图,见`addMarker(...)`；百度地图的初始化`SDKInitializer.initialize(getApplicationContext());`要写在onCreate的setContentView之前，但是这样界面加载好慢，以后要想想在这方面做下优化，现在的思路是提前初始化，在任务比较轻松而且下个界面可能会用到地图的时候提前初始化好地图
 - 环信在某些地方自动设置nick：因为环信的用户名不能是汉字，而在app里肯定显示的更多是汉字昵称，这就给程序带来了很多不方便，还要手动去设置本地存储的用户的昵称，以便显示的是昵称而不是用户名。在ChatActivity中会有某个地方自动调用setNick方法，传入的参数是当前用户名，这就使得当从聊天界面返回到主界面的时候该用户的显示信息又显示成username，现在已经确认是在onresume方法后执行的setNick方法，以后要查证到底是在哪重新设置的昵称，现在的解决办法见`User.setNick(String newNick)`
 - 上传图片采用base64编码，直接放在form中提交，服务器用base64解码，存储文件，这样就省去了单独传送文件的麻烦，不过在注册中如果其他个人信息不成功的话，再上传图片的话是对流量的浪费，也让交互的时间延长
 - 图片、布局的适配：这应该在大多数的app中都会遇到，之前没有仔细思考一种好的解决方案，这次为了不辜负美工做的好图，就使劲思考，最终想出了解决问题的办法，一种是动态调整布局的LayoutParams设置宽高，根据这个宽度再去创建合适的bitmap，设置给需要这个图片的地方，有时候还要用MarginLayoutParams去动态调整布局，保证ui效果的比例去实现好看的界面，并不是那么简单……
