## <a name="qrshow">qrshow</a>
将String信息转化为bitmap，并可以将其展示。  
可以绑定到指定imageview，或生成一个dialog显示结果。
### how to use:
#### 1. gradle
```groovy
implementation 'com.github.krealseu.widget:qrshow:3a0cfb82e6'
```
#### 2. code
```Kotlin
QRShow(context) show "info"
QRShow(imageView) show "info"
val bitmap = QRShow().show("info").get()
```
#### 3. 方法说明
  code | 说明
  --- | ---
constructor(context: Context) | 构造函数，在show方法后，直接打开一个Activity或Dialog用以显示图片
constructor(imageView: ImageView) | 构造函数，在show方法调用后，会对该imagView设置QR图片
constructor() | 构造函数，在show方法调用后，不会更新视图，其Bitmap通过get方法获得
setSize(width: Int, height: Int) | 设置生成图片的大小，默认值800，需要在show方法前调用
show(info:String) | 生成相应的QR图片，并根据构造函数参数，将图片绑定到imageView
getState():Status | 获得当前AsyncTask的工作状态
get(): Bitmap? | 获得AsyncTask的工作结果，会阻塞线程
get(timeout: Long, unit: TimeUnit): Bitmap? | loadTask?.get(timeout, unit)
