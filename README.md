# Alpha

```.java
    //创建build对象
    ConstructionImpl.Build build = new ConstructionImpl.Build();

    //添加默认占位参数,替换用{}包括的字符值，可以看下面实例
    build.addGlobalParams("name", "value");

    //添加默认参数，默认参数会在每个请求的url后面添加已经添加的请求参数，可以看下面实例
    build.addDefaultParams("name","value")



    //AKXApi 设置api方法如下
    @Get("{akx}pub/api/getHome") //{akx}表示替换占位参数akx对应值
    
    HttpDataGet<GetHomeBean> getHomeNew(@params("version")String version);//返回值的泛型可以是string，或者服务器返回对象的实体。方法参数带Params标签如果是get 请求方式会向url尾端添加查询参数，如果是post 请求方式会以表单方式提交

    @Post("{akx}usr/api/markNoticRead")
    HttpDataGet<CommonBean> markNoticRead(@Param("token") String token, @JsonBody Object nids);//JsonBody 接收对象实体或者json字符串，会以请求体的方式向服务器提交json数据

    //AKXApiServer 获取方法如下

    //get方法演示
    HttpDataGet dataGet=AKXApiServer.api().getHomeNew("2");



    //post方式演示
    class Persion{
        String name;
    }
    Persion bean=new Persion();
    bean.name="bean";

    HttpDataGet dataGet=AKXApiServer.api().markNoticRead("token", bean);
    //or
    HttpDataGet dataGet1=AKXApiServer.api().markNoticRead("token", {\"name\":\"bean\"});

 ```
 ```.java 


    //GET和POST方法返回的HttpDataGet对应需要注册com.azl.obs.retrofit.itf.Observer进行监听,如下
    //准备一个作为请求实体的类
    class Body{
        public Body(String name){
            this.name=name;
        }
        String name;
    }

    //数据返回的实体可以是实体也可以使String字符串
    class Response{
        int code;
        Data data;
        class Data{
            String data;
        }
    }
    
    //创建一个接口统一管理api
    public interface API{
        @Post("{akx}usr/api/markNoticRead")
        HttpDataGet<Response> markNoticRead(@Param("token") String token, @JsonBody Object nids;
    }
    
    Body body=new Body("zhongq");
    
    //通过代理创建出API对象
    ConstructionImpl.Build build = new ConstructionImpl.Build();
    API api=build.build().create(API.class);
    
    //创建httpDataGet对象
    HttpDataGet<Response> dataget=api.markNoticRead("token",body);

    dataget.register(new Observer<Response>{//也可以实现ObserverAdapter类
        @Override
        public void onComplete() {
            //请求已经完成不管是成功还是失败
        }

        @Override
        public void onBegin() {
            //请求准备开始，还未开始
        }

        @Override
        public void onNext(Response bean) {
            //请求完成，并且请求成功，返回成功的实体
        }

        @Override
        public void onCache(Response bean) {
            //请求缓存，还未做
        }

        @Override
        public void onError(int code, String msg) {
            //请求失败
        }
    });

```


## 广播（HandleMsg）

* 如果无特殊要求可以用HandleMsg代替BroadcastReceiver的使用下面是HandleMsg的使用方式

```.java

    //注册广播，需要监听到广播发出需要先注册
    HandleMsg.bind(this);//参数为需要监听的对象，注册成功后会查找$符号开头结尾的方法，并判断是否是广播监听方法
    
    //注册广播后需要反注册防止内存泄漏
    HandleMsg.unBind(this)
    
    //发出广播
    HandleMsg.handleMark("mark1","data");
    
    /*
    * 接收对应发出广播,方法名字要用$符号开始和结束，便于更普通的方法区分开，不然也识别不出该方法是接收广播的方法。
    * 注解mark表示要注册接收的发送mark的标记
    * 接收的参数表示要接收的数据类型，只有mark和方法的数据类型与发送的时候相同才能接收到
    */
    @Mark("mark1")
    public void $handle$(String data){
        //接收到上面发出的data字符串
    }
    
```

## 下载(D)
* D下载通过发出广播向app全局通过指定文件的下载进度和状态。
```.java
    //添加下载任务
    
    //targetUrl 下载文件的url
    //mark 下载文件时会通过mark标记的广播进行通知
    D.download("targetUrl","mark");
    
    //监听的方法,方法的参数一定要为com.azl.file.bean.Info对象才能接到
    @Mark("mark")
    public void $handle$(Info info){
        //info 对象的字段信息的可以查看类文件的注释信息
    }
    
    D.stopDownload("targetUrl");//停止下载任务

    D.getDownloadInfo("targetUrl");//获取下载信息

    
```


## 上传
* D上传通过发出广播向app全局通过指定文件的下载进度和状态。
```java

    /**
     * @params targetUrl 上传服务器的地址
     * @params localPath 本地上传文件的地址
     * @params mark 上传文件时会通过mark标记的广播进行通知
     * @params tab 标记对象，可以为空
     **/
    D.upload( targetUrl,  localPath,  mark, tab);

```


## 对象取值
```.java
     class A{
        B b;
    }
    class B{
        C c;
    }

    class C{
        D d;
    }
    class D{
        String name;
    }
    //现在我们有一个A对象，如果要拿D对象name值的话通常都会写成
    A a;
    String name="";
    if(a!=null){
        if(a.b!=null){
            if(a.b.c!=null){
                if(a.b.c.d!=null){
                    name=a.b.c.d.name;
                }
            }
        }
    }
    
    //可以借助ObjectValueUtil进行取值
    name= (String) ObjectValueUtil.getInstance().getValueObject(a,"b/c/d/name");
    //如果其中路径的值为null会返回null，防止报空指针异常
```




