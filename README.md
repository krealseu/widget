# Custom widget
[![](https://jitpack.io/v/krealseu/widget.svg)](https://jitpack.io/#krealseu/widget)  
自己的视图库  <a href="#filepickdialog">filepickdialog</a> &nbsp; <a href="#qrshow">qrshow</a>

## <a name="filepickdialog">filepickdialog</a>

### how to use:
#### 1. gradle
```groovy
implementation 'com.github.krealseu.widget:filepickdialog:c36e0993d0'
```
#### 2. code
```Kotlin
FilePickDialogFragment().apply {
    type = FilePickDialogFragment.DIRECTORY_CHOOSE
    setListener {
        //  (result: Array<out Uri>) -> Unit
    }.show(fragmentManager, "l")
```
#### 3. 方法说明
  code | 说明
  --- | ---
type | FILE_PICK &nbsp; DIRECTORY_CHOOSE &nbsp; MULTI_FILE_PICK
miniType | 选择文件的过滤
setListener | 设置返回接口
  
___
  
## <a name="qrshow">qrshow</a>
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
