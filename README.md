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
