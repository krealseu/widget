# Custom widget
自己的视图库  <a href="#filepickdialog">filepickdialog</a>  <a href="#qrshow">qrshow</a>

## <a name="filepickdialog">filepickdialog</a>

### how to use:
#### 1. gradle
```groovy
implementation 'com.github.krealseu.widget:filepickdialog:c36e0993d0'
```
#### 2. code
```Kotlin
FilePickDialogFragment().apply {
    selectFolder = true
    multiSelect = true
    setListener {
        //  (result: Array<out Uri>) -> Unit
    }.show(fragmentManager, "l")
```
#### 3. 方法说明
  code | 说明
  --- | ---
selectFolder | 选择文件夹
multiSelect | 是否多选文件
miniType | 选择文件的过滤
setListener | 设置返回接口

___

## <a name="qrshow">qrshow</a>
### how to use:
#### 1. gradle
```groovy
implementation 'com.github.krealseu.widget:qrshow:c36e0993d0'
```
#### 2. code
```Kotlin
QRShow(context) show "info"
```
