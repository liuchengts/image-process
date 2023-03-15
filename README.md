# image-process
前一阵在尝试实现一个`robot rpa`相关的设计理论，涉及到需要快速的对比图像的差异，并且提取出差异部分的内容。

### 一、思路
* 使用`opencv`读取图像为`RGB`数据
* 根据`RGB`的坐标`宽、高、通道`确定通道值
* 使用`hash`对其进行标记，以便后续快速查找
* 根据`宽、高、通道`快速对比查找找出不符合的部分，求`出差异矩形`
* 根据像素差异点截取`出差异矩形`部分
* 将`出差异矩形`部分的图片流送到`Tesseract`识别

#### 注意：这里如果涉及到不同分辨率之间的图像识别，需要先做以下前置处理：
* 对比`2`张图片的分辨率，求出`最小分辨率`范围
* 根据`最小分辨率`进行图像压缩，使其都按固定分辨率进行后续的工作

### 二、获取opencv
- 访问 [opencv releases](https://opencv.org/releases/) 选择合适的平台下载对应版本的包，我这里以`windows`为例(官网没有提供`linux`的安装方式，都是直接下载源代码了`cmake`)
- 安装之后`lib`位置为
    - `windows`在安装目录下的`build\java\x64`
    - `linux`在安装目录下的`build/lib/`
- 将`opencv-470.jar`（我下载的版本是`4.7`）包放到你的项目工程下，用包管理器添加引用
- 将`opencv_java470.dll`（我下载的版本是`4.7`）包放到你的项目工程下
  到这里，项目就具备了`opencv`的使用条件了

### 三、获取tesseract
我这里用的不是原生 [tesseract](https://github.com/tesseract-ocr/tesseract) ，我用的是 [tess4j](https://github.com/nguyenq/tess4j) ，它对前者进行了一些简易封装，不过我还是建议大家使用前者，至于原因大家可以去看一下 [tess4j issues](https://github.com/nguyenq/tess4j/issues)
- 访问 [tess4j releases](https://github.com/nguyenq/tess4j/releases) 获取
- 也可以在`maven中央仓库`中搜索到
#### 获取`tesseract`所需的语言包
- 语言包下载 [tessdata](https://github.com/tesseract-ocr/tessdata)
- 语言包说明 [tessdoc](https://tesseract-ocr.github.io/tessdoc/Data-Files.html)
- 将下载的语言包放到`项目的tessdata`目录下


说明：
* 该代码只有`windows`下运行的示例，想在其他环境下运行请按`opencv`的文档要求更改
* 该代码缺少`opencv`对应的`opencv_java470.dll`（我下载的版本是`4.7`）文件，`github`限制单个文件不能超过`100MB`
* 该代码缺少`项目的tessdata`文件夹及语言包，太大上传不上来，需要自己下载